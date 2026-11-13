<#
.SYNOPSIS
    Khôi phục thử một tệp sao lưu vào CSDL riêng và so với CSDL nguồn
    (TS-001, AC-067).

.DESCRIPTION
    Tạo lại CSDL đích (mặc định bluemoon_khoi_phuc_thu), nạp tệp sao lưu,
    rồi so số dòng từng bảng với CSDL nguồn, so tổng tiền phiếu thu và kiểm
    tra hai trigger chặn sửa/xóa của bảng nhat_ky. In bảng so sánh; thoát
    mã 1 nếu lệch, mã 2 nếu không chạy được.

    So với nguồn chỉ có nghĩa khi nguồn chưa đổi từ lúc sao lưu: chạy ngay
    sau sao-luu.ps1, lúc không ai dùng ứng dụng.

    Đích là "bluemoon" hoặc trùng CSDL nguồn (khôi phục thật) thì phải có
    -GhiDe và xác nhận bằng cách gõ lại tên CSDL (hoặc -XacNhan <tên>); khi
    đó không so với nguồn (nguồn vừa bị ghi đè), chỉ in số dòng và kiểm tra
    trigger. Dừng ứng dụng ở mọi máy trước khi khôi phục thật.

.EXAMPLE
    powershell -ExecutionPolicy Bypass -File scripts\khoi-phuc-thu.ps1 -TepSaoLuu "$env:USERPROFILE\BlueMoon\sao-luu\bluemoon-20261113-220000.sql"
#>
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$TepSaoLuu,
    [string]$CsdlDich = 'bluemoon_khoi_phuc_thu',
    [string]$CsdlNguon = 'bluemoon',
    [string]$Mysql,
    [string]$LoginPath = 'bluemoon-sao-luu',
    [string]$TepTuyChon,
    [switch]$GhiDe,
    [string]$XacNhan
)

$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'mysql-chung.ps1')

$CsdlThuMacDinh = 'bluemoon_khoi_phuc_thu'
$TriggerNhatKy = @('trg_nky_chan_sua', 'trg_nky_chan_xoa')

function Stop-Loi {
    param([string]$ThongBao)
    [Console]::Error.WriteLine("LỖI: $ThongBao")
    exit 2
}

foreach ($ten in @($CsdlDich, $CsdlNguon)) {
    # Tên CSDL ghép thẳng vào câu SQL nên chỉ cho chữ, số, gạch dưới.
    if ($ten -notmatch '^[A-Za-z0-9_]{1,64}$') {
        Stop-Loi "Tên CSDL '$ten' chỉ được gồm chữ không dấu, số và dấu gạch dưới."
    }
}

try {
    $exe = Find-CongCuMysql -Ten 'mysql' -ChiDinh $Mysql
    $dangNhap = Get-TuyChonDangNhap -LoginPath $LoginPath -TepTuyChon $TepTuyChon
} catch {
    Stop-Loi $_.Exception.Message
}
if (-not (Test-Path -LiteralPath $TepSaoLuu -PathType Leaf)) {
    Stop-Loi "Không thấy tệp sao lưu '$TepSaoLuu'."
}
$TepSaoLuu = (Resolve-Path -LiteralPath $TepSaoLuu).Path
if (-not (Test-KetThucDayDu $TepSaoLuu)) {
    Stop-Loi "Tệp '$TepSaoLuu' không kết thúc bằng dòng '$Script:DongKetThuc': tệp hỏng hoặc chưa sao lưu xong."
}

# Chạy SQL qua stdin, trả về các dòng kết quả (cột cách nhau bằng tab).
function Invoke-Sql {
    param([string]$Sql, [string]$Csdl)
    $doiSo = @($dangNhap) + @('--default-character-set=utf8mb4', '-N', '-B')
    if ($Csdl) { $doiSo += $Csdl }
    $kq = Invoke-ChuongTrinh -Tep $exe -DoiSo $doiSo -StdinChuoi $Sql
    if ($kq.MaThoat -ne 0) {
        throw "mysql trả mã $($kq.MaThoat): $(Format-MotDong $kq.Stderr)"
    }
    return @($kq.Stdout -split "`r?`n" | Where-Object { $_ -ne '' })
}

function Test-CoCsdl {
    param([string]$Ten)
    $r = Invoke-Sql "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = '$Ten';"
    return ($r[0] -eq '1')
}

function Get-SoDong {
    param([string]$Csdl)
    $bang = Invoke-Sql ("SELECT table_name FROM information_schema.tables " +
        "WHERE table_schema = '$Csdl' AND table_type = 'BASE TABLE' ORDER BY table_name;")
    $kq = [ordered]@{}
    if ($bang.Count -eq 0) { return $kq }
    $sql = ($bang | ForEach-Object {
        "SELECT '$_', COUNT(*) FROM ``$Csdl``.``$_``"
    }) -join ' UNION ALL '
    foreach ($dong in (Invoke-Sql "$sql;")) {
        $cot = $dong -split "`t"
        $kq[$cot[0]] = [long]$cot[1]
    }
    return $kq
}

function Get-TongTienPhieuThu {
    param([string]$Csdl)
    $co = Invoke-Sql ("SELECT COUNT(*) FROM information_schema.tables " +
        "WHERE table_schema = '$Csdl' AND table_name = 'phieu_thu';")
    if ($co[0] -ne '1') { return $null }
    return (Invoke-Sql "SELECT COALESCE(SUM(tong_tien), 0) FROM ``$Csdl``.phieu_thu WHERE da_huy = FALSE;")[0]
}

function Get-Trigger {
    param([string]$Csdl)
    return @(Invoke-Sql ("SELECT CONCAT(event_object_table, '.', trigger_name) " +
        "FROM information_schema.triggers WHERE trigger_schema = '$Csdl' ORDER BY 1;"))
}

try {
    # ---- bảo vệ CSDL thật ----
    $dichLaGoc = ($CsdlDich -eq 'bluemoon') -or ($CsdlDich -eq $CsdlNguon)
    $dichDaCo = Test-CoCsdl $CsdlDich
    if ($dichLaGoc) {
        if (-not $GhiDe) {
            Stop-Loi ("Từ chối khôi phục vào '$CsdlDich' (CSDL thật hoặc trùng CSDL nguồn). " +
                "Khôi phục thử dùng CSDL khác; khôi phục thật thì thêm -GhiDe.")
        }
        if ($XacNhan -ne $CsdlDich) {
            Write-Host "Sắp XÓA TOÀN BỘ dữ liệu hiện có của CSDL '$CsdlDich' và thay bằng '$TepSaoLuu'."
            Write-Host 'Dừng ứng dụng BlueMoon ở mọi máy trước khi tiếp tục.'
            try {
                $go = Read-Host "Gõ lại tên CSDL ($CsdlDich) để xác nhận"
            } catch {
                $go = ''
            }
            if ($go -ne $CsdlDich) {
                Stop-Loi 'Chưa xác nhận đúng tên CSDL, không khôi phục.'
            }
        }
    } elseif ($dichDaCo -and $CsdlDich -ne $CsdlThuMacDinh -and -not $GhiDe) {
        Stop-Loi "CSDL '$CsdlDich' đã có; thêm -GhiDe để xóa và khôi phục đè, hoặc chọn tên khác."
    }
    if (-not $dichLaGoc -and -not (Test-CoCsdl $CsdlNguon)) {
        Stop-Loi "Không thấy CSDL nguồn '$CsdlNguon' để so sánh."
    }

    # ---- khôi phục ----
    $dongHo = [Diagnostics.Stopwatch]::StartNew()
    [void](Invoke-Sql ("DROP DATABASE IF EXISTS ``$CsdlDich``; " +
        "CREATE DATABASE ``$CsdlDich`` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"))
    $doiSo = @($dangNhap) + @('--default-character-set=utf8mb4', $CsdlDich)
    $kq = Invoke-ChuongTrinh -Tep $exe -DoiSo $doiSo -StdinTuTep $TepSaoLuu
    if ($kq.MaThoat -ne 0) {
        $loi = Format-MotDong $kq.Stderr
        if ($loi -match '1419|SUPER privilege') {
            $loi += ' (tạo trigger cần quyền SUPER hoặc log_bin_trust_function_creators = 1, xem docs/setup.md)'
        }
        throw "Nạp tệp sao lưu lỗi, mysql trả mã $($kq.MaThoat): $loi"
    }
    $dongHo.Stop()
    Write-Output ("Đã khôi phục '{0}' vào CSDL '{1}' trong {2:0.0} s." -f
        (Split-Path $TepSaoLuu -Leaf), $CsdlDich, $dongHo.Elapsed.TotalSeconds)

    # ---- so sánh ----
    $dich = Get-SoDong $CsdlDich
    $lech = @()
    $dong = @()
    if ($dichLaGoc) {
        foreach ($b in $dich.Keys) {
            $dong += [pscustomobject]@{ 'Bảng' = $b; 'Khôi phục' = $dich[$b] }
        }
    } else {
        $nguon = Get-SoDong $CsdlNguon
        $tatCa = @($nguon.Keys) + @($dich.Keys) | Sort-Object -Unique
        foreach ($b in $tatCa) {
            $n = if ($nguon.Contains($b)) { $nguon[$b] } else { $null }
            $d = if ($dich.Contains($b)) { $dich[$b] } else { $null }
            $khop = ($null -ne $n) -and ($null -ne $d) -and ($n -eq $d)
            if (-not $khop) { $lech += "bảng $b" }
            $dong += [pscustomobject]@{
                'Bảng'      = $b
                'Nguồn'     = if ($null -eq $n) { '(không có)' } else { $n }
                'Khôi phục' = if ($null -eq $d) { '(không có)' } else { $d }
                'Khớp'      = if ($khop) { 'có' } else { 'LỆCH' }
            }
        }
        $tienNguon = Get-TongTienPhieuThu $CsdlNguon
        $tienDich = Get-TongTienPhieuThu $CsdlDich
        if ($tienNguon -ne $tienDich) { $lech += 'tổng tiền phiếu thu' }
        $dong += [pscustomobject]@{
            'Bảng' = 'Tổng tiền phiếu thu chưa hủy'; 'Nguồn' = $tienNguon
            'Khôi phục' = $tienDich; 'Khớp' = if ($tienNguon -eq $tienDich) { 'có' } else { 'LỆCH' }
        }
        $trgNguon = (Get-Trigger $CsdlNguon) -join ', '
        $trgDich = (Get-Trigger $CsdlDich) -join ', '
        if ($trgNguon -ne $trgDich) {
            $lech += "trigger (nguồn: $trgNguon; khôi phục: $trgDich)"
        }
    }
    $dong | Format-Table -AutoSize | Out-String -Width 200 | Write-Output

    $trgDich = Get-Trigger $CsdlDich
    foreach ($t in $TriggerNhatKy) {
        if ($trgDich -contains "nhat_ky.$t") {
            Write-Output "Trigger nhat_ky.${t}: có"
        } else {
            Write-Output "Trigger nhat_ky.${t}: THIẾU"
            $lech += "thiếu trigger nhat_ky.$t"
        }
    }
} catch {
    Stop-Loi (Format-MotDong $_.Exception.Message)
}

if ($lech.Count -gt 0) {
    Write-Output ("KẾT QUẢ: LỆCH - " + ($lech -join '; '))
    exit 1
}
if ($dichLaGoc) {
    Write-Output "KẾT QUẢ: ĐÃ KHÔI PHỤC '$CsdlDich', đủ trigger nhật ký."
} else {
    Write-Output "KẾT QUẢ: KHỚP - CSDL '$CsdlDich' có đủ dữ liệu như '$CsdlNguon'."
}
exit 0
