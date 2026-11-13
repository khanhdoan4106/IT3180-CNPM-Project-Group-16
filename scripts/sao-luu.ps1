<#
.SYNOPSIS
    Sao lưu CSDL BlueMoon bằng mysqldump (TS-001, AC-066, giả định 43).

.DESCRIPTION
    Tạo tệp bluemoon-yyyyMMdd-HHmmss.sql trong thư mục đích, ghi một dòng vào
    sao-luu.log (thời điểm, kết quả, tên tệp, dung lượng, thời gian chạy, lỗi)
    và chỉ khi sao lưu thành công mới xóa bản cũ, giữ -SoBanGiu bản mới nhất.
    Lỗi thì thoát với mã khác 0 để Task Scheduler ghi "lần chạy lỗi".

    Đăng nhập MySQL bằng login-path (mysql_config_editor) hoặc tệp tùy chọn
    [client] ngoài repo. Script không nhận mật khẩu qua tham số.

    Tệp sao lưu không có CREATE DATABASE/USE nên khôi phục được vào CSDL tên
    khác (xem khoi-phuc-thu.ps1 và docs/setup.md).

.EXAMPLE
    powershell -ExecutionPolicy Bypass -File scripts\sao-luu.ps1
.EXAMPLE
    pwsh scripts/sao-luu.ps1 -TepTuyChon D:\bi-mat\bluemoon.cnf -ThuMucDich E:\sao-luu
#>
[CmdletBinding()]
param(
    [string]$CoSoDuLieu = 'bluemoon',
    [string]$ThuMucDich,
    [ValidateRange(1, 10000)]
    [int]$SoBanGiu = 30,
    [string]$MysqlDump,
    [string]$LoginPath = 'bluemoon-sao-luu',
    [string]$TepTuyChon
)

$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'mysql-chung.ps1')

# TUY-CHON-MYSQLDUMP-BAT-DAU (SaoLuuIT đọc danh sách này để chạy đúng các
# tùy chọn của script). Không thêm --databases: tệp sẽ có USE <tên CSDL> và
# luôn khôi phục đè vào CSDL gốc.
$TuyChonMysqldump = @(
    '--single-transaction'
    '--routines'
    '--triggers'
    '--set-gtid-purged=OFF'
    '--default-character-set=utf8mb4'
)
# TUY-CHON-MYSQLDUMP-KET-THUC

$MauTen = '^bluemoon-\d{8}-\d{6}\.sql$'
$MauTenTam = '^bluemoon-\d{8}-\d{6}\.sql\.tmp$'

if (-not $ThuMucDich) {
    $nha = $env:USERPROFILE
    if (-not $nha) { $nha = $HOME }
    $ThuMucDich = Join-Path (Join-Path $nha 'BlueMoon') 'sao-luu'
}

try {
    if (-not (Test-Path -LiteralPath $ThuMucDich -PathType Container)) {
        [void](New-Item -ItemType Directory -Path $ThuMucDich -Force)
    }
    $ThuMucDich = (Resolve-Path -LiteralPath $ThuMucDich).Path
} catch {
    [Console]::Error.WriteLine("Không tạo được thư mục sao lưu '$ThuMucDich': $($_.Exception.Message)")
    exit 2
}
$TepLog = Join-Path $ThuMucDich 'sao-luu.log'

function Write-Log {
    param([string]$KetQua, [string]$TenTep, [string]$DungLuong,
        [string]$ThoiGian, [string]$ThongBao)
    $dong = '{0} | {1} | {2} | {3} | {4} | {5}' -f `
        (Get-Date -Format 'yyyy-MM-dd HH:mm:ss'), $KetQua, $TenTep,
        $DungLuong, $ThoiGian, $ThongBao
    [IO.File]::AppendAllText($TepLog, $dong.TrimEnd(' ', '|') + [Environment]::NewLine,
        (New-Object System.Text.UTF8Encoding($false)))
    Write-Output $dong
}

function Format-DungLuong {
    param([long]$Byte)
    $vn = [Globalization.CultureInfo]::GetCultureInfo('vi-VN')
    if ($Byte -ge 1MB) { return ($Byte / 1MB).ToString('0.0', $vn) + ' MB' }
    if ($Byte -ge 1KB) { return ($Byte / 1KB).ToString('0.0', $vn) + ' KB' }
    return "$Byte B"
}

$dongHo = [Diagnostics.Stopwatch]::StartNew()
$tenTep = ''
$tepTam = $null
try {
    $exe = Find-CongCuMysql -Ten 'mysqldump' -ChiDinh $MysqlDump
    $dangNhap = Get-TuyChonDangNhap -LoginPath $LoginPath -TepTuyChon $TepTuyChon

    do {
        $tenTep = 'bluemoon-{0}.sql' -f (Get-Date -Format 'yyyyMMdd-HHmmss')
        $tepDich = Join-Path $ThuMucDich $tenTep
        $tepTam = "$tepDich.tmp"
        if ((Test-Path -LiteralPath $tepDich) -or (Test-Path -LiteralPath $tepTam)) {
            # Hai lần chạy trong cùng một giây: chờ sang giây sau.
            Start-Sleep -Milliseconds 1100
            $trung = $true
        } else {
            $trung = $false
        }
    } while ($trung)

    $doiSo = @($dangNhap) + $TuyChonMysqldump + @($CoSoDuLieu)
    $kq = Invoke-ChuongTrinh -Tep $exe -DoiSo $doiSo -StdoutRaTep $tepTam
    if ($kq.MaThoat -ne 0) {
        $loi = Format-MotDong $kq.Stderr
        if (-not $loi) { $loi = 'không có thông báo' }
        if (-not $TepTuyChon -and $loi -match 'Access denied|1045') {
            $loi += " (kiểm tra login-path '$LoginPath' đã tạo bằng mysql_config_editor)"
        }
        throw "mysqldump trả mã $($kq.MaThoat): $loi"
    }
    if (-not (Test-KetThucDayDu $tepTam)) {
        throw "Tệp sao lưu không kết thúc bằng dòng '$Script:DongKetThuc' (bị cắt giữa chừng)."
    }
    Move-Item -LiteralPath $tepTam -Destination $tepDich
    $tepTam = $null
    $dungLuong = (Get-Item -LiteralPath $tepDich).Length
} catch {
    $dongHo.Stop()
    if ($tepTam -and (Test-Path -LiteralPath $tepTam)) {
        Remove-Item -LiteralPath $tepTam -Force -ErrorAction SilentlyContinue
    }
    $giay = $dongHo.Elapsed.TotalSeconds.ToString('0.0', [Globalization.CultureInfo]::GetCultureInfo('vi-VN'))
    Write-Log -KetQua 'LỖI' -TenTep $tenTep -DungLuong '' -ThoiGian "$giay s" `
        -ThongBao (Format-MotDong $_.Exception.Message)
    exit 1
}
$dongHo.Stop()
$giay = $dongHo.Elapsed.TotalSeconds.ToString('0.0', [Globalization.CultureInfo]::GetCultureInfo('vi-VN'))

# Chỉ tới đây (sao lưu vừa thành công) mới dọn bản cũ: giữ đúng $SoBanGiu
# bản mới nhất theo tên (tên có ngày giờ nên thứ tự tên là thứ tự thời gian),
# không đụng tệp không khớp mẫu tên.
$ghiChu = ''
try {
    $cacBan = @(Get-ChildItem -LiteralPath $ThuMucDich -File |
        Where-Object { $_.Name -cmatch $MauTen } |
        Sort-Object Name -Descending)
    $xoa = @($cacBan | Select-Object -Skip $SoBanGiu)
    foreach ($t in $xoa) {
        Remove-Item -LiteralPath $t.FullName -Force
    }
    # Tệp tạm của lần chạy trước bị ngắt giữa chừng (tắt máy khi đang sao lưu).
    Get-ChildItem -LiteralPath $ThuMucDich -File |
        Where-Object { $_.Name -cmatch $MauTenTam } |
        ForEach-Object { Remove-Item -LiteralPath $_.FullName -Force }
    if ($xoa.Count -gt 0) {
        $ghiChu = "xóa $($xoa.Count) bản cũ, giữ $([Math]::Min($cacBan.Count, $SoBanGiu)) bản"
    }
} catch {
    # Sao lưu đã xong; lỗi dọn dẹp chỉ ghi chú, không đổi kết quả.
    $ghiChu = 'không xóa được bản cũ: ' + (Format-MotDong $_.Exception.Message)
}
Write-Log -KetQua 'THÀNH CÔNG' -TenTep $tenTep -DungLuong (Format-DungLuong $dungLuong) `
    -ThoiGian "$giay s" -ThongBao $ghiChu
exit 0
