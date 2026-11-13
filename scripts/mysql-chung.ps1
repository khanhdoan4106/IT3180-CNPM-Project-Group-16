# BlueMoon - hàm dùng chung cho sao-luu.ps1 và khoi-phuc-thu.ps1 (TS-001).
# Chạy được trên Windows PowerShell 5.1 và PowerShell 7 (pwsh). Tệp này
# được dot-source, không chạy riêng.
#
# Mật khẩu MySQL không bao giờ đi qua dòng lệnh: chỉ đọc từ login-path
# (mysql_config_editor) hoặc tệp tùy chọn [client] ngoài repo.

Set-StrictMode -Version 2.0

$Script:ThuMucMysqlMacDinh = 'C:\Program Files\MySQL\MySQL Server 8.0\bin'
$Script:DongKetThuc = '-- Dump completed'

function Get-ThuMucRepo {
    return (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
}

# Tìm tệp chạy của một công cụ MySQL (mysqldump, mysql): đường dẫn chỉ định,
# rồi PATH, rồi thư mục cài mặc định của MySQL 8.0 trên Windows.
function Find-CongCuMysql {
    param([string]$Ten, [string]$ChiDinh)
    if ($ChiDinh) {
        if (-not (Test-Path -LiteralPath $ChiDinh -PathType Leaf)) {
            throw "Không thấy $Ten tại '$ChiDinh'."
        }
        return (Resolve-Path -LiteralPath $ChiDinh).Path
    }
    $lenh = Get-Command $Ten -CommandType Application -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if ($lenh) {
        return $lenh.Source
    }
    $macDinh = Join-Path $Script:ThuMucMysqlMacDinh "$Ten.exe"
    if (Test-Path -LiteralPath $macDinh -PathType Leaf) {
        return $macDinh
    }
    throw "Không tìm thấy $Ten trong PATH hay '$Script:ThuMucMysqlMacDinh'. Chỉ đường dẫn bằng tham số."
}

# Tùy chọn đăng nhập đặt đầu dòng lệnh (MySQL đòi --defaults-extra-file
# đứng đầu). Tệp tùy chọn phải nằm ngoài repo để không lỡ commit mật khẩu.
function Get-TuyChonDangNhap {
    param([string]$LoginPath, [string]$TepTuyChon)
    if ($TepTuyChon) {
        if (-not (Test-Path -LiteralPath $TepTuyChon -PathType Leaf)) {
            throw "Không thấy tệp tùy chọn '$TepTuyChon'."
        }
        $tep = (Resolve-Path -LiteralPath $TepTuyChon).Path
        $repo = Get-ThuMucRepo
        $tachRepo = $repo.TrimEnd('\', '/') + [IO.Path]::DirectorySeparatorChar
        if ($tep.StartsWith($tachRepo, [StringComparison]::OrdinalIgnoreCase)) {
            throw "Tệp tùy chọn '$tep' nằm trong repo; chuyển nó ra ngoài repo để không lỡ commit mật khẩu."
        }
        return @("--defaults-extra-file=$tep")
    }
    if (-not $LoginPath) {
        throw 'Cần -LoginPath hoặc -TepTuyChon.'
    }
    return @("--login-path=$LoginPath")
}

# Bọc một đối số theo quy tắc tách đối số của Windows (CommandLineToArgvW),
# .NET trên Linux tách theo cùng quy tắc.
function ConvertTo-DoiSo {
    param([string]$s)
    if ($s -ne '' -and $s -notmatch '[\s"]') {
        return $s
    }
    $sb = New-Object System.Text.StringBuilder
    [void]$sb.Append('"')
    $soGachCheo = 0
    foreach ($c in $s.ToCharArray()) {
        if ($c -eq '\') {
            $soGachCheo++
            continue
        }
        if ($c -eq '"') {
            [void]$sb.Append([char]92, 2 * $soGachCheo + 1)
        } elseif ($soGachCheo -gt 0) {
            [void]$sb.Append([char]92, $soGachCheo)
        }
        $soGachCheo = 0
        [void]$sb.Append($c)
    }
    [void]$sb.Append([char]92, 2 * $soGachCheo)
    [void]$sb.Append('"')
    return $sb.ToString()
}

# Chạy một chương trình, nối byte stdin từ tệp hoặc chuỗi và ghi byte stdout
# ra tệp hoặc trả về chuỗi UTF-8. Không qua đường ống của PowerShell nên
# không đổi mã hóa, không thêm BOM, không đổi xuống dòng.
# Trả về @{ MaThoat; Stdout; Stderr }.
function Invoke-ChuongTrinh {
    param(
        [string]$Tep,
        [string[]]$DoiSo,
        [string]$StdinTuTep,
        [string]$StdinChuoi,
        [string]$StdoutRaTep
    )
    $psi = New-Object System.Diagnostics.ProcessStartInfo
    $psi.FileName = $Tep
    $psi.Arguments = (($DoiSo | ForEach-Object { ConvertTo-DoiSo $_ }) -join ' ')
    $psi.UseShellExecute = $false
    $psi.CreateNoWindow = $true
    $psi.RedirectStandardOutput = $true
    $psi.RedirectStandardError = $true
    $psi.RedirectStandardInput = $true
    $p = New-Object System.Diagnostics.Process
    $p.StartInfo = $psi
    [void]$p.Start()
    $docLoi = $p.StandardError.ReadToEndAsync()
    $ra = $null
    try {
        if ($StdoutRaTep) {
            $ra = [IO.File]::Open($StdoutRaTep, [IO.FileMode]::CreateNew,
                [IO.FileAccess]::Write, [IO.FileShare]::Read)
        } else {
            $ra = New-Object System.IO.MemoryStream
        }
        $docRa = $p.StandardOutput.BaseStream.CopyToAsync($ra)
        try {
            if ($StdinTuTep) {
                $vao = [IO.File]::OpenRead($StdinTuTep)
                try { $vao.CopyTo($p.StandardInput.BaseStream) } finally { $vao.Dispose() }
            } elseif ($StdinChuoi) {
                $byte = (New-Object System.Text.UTF8Encoding($false)).GetBytes($StdinChuoi)
                $p.StandardInput.BaseStream.Write($byte, 0, $byte.Length)
            }
        } catch [System.IO.IOException] {
            # Chương trình thoát sớm (vd sai mật khẩu) nên đóng stdin; mã
            # thoát và stderr bên dưới cho biết lý do.
        } finally {
            try { $p.StandardInput.Close() } catch [System.IO.IOException] { }
        }
        $docRa.Wait()
        $p.WaitForExit()
        $stdout = ''
        if (-not $StdoutRaTep) {
            $stdout = [Text.Encoding]::UTF8.GetString($ra.ToArray())
        }
        return @{
            MaThoat = $p.ExitCode
            Stdout  = $stdout
            Stderr  = $docLoi.Result
        }
    } finally {
        if ($ra) { $ra.Dispose() }
        $p.Dispose()
    }
}

# Gộp stderr nhiều dòng thành một dòng cho log.
function Format-MotDong {
    param([string]$s)
    return (($s -split "`r?`n" | Where-Object { $_.Trim() -ne '' } |
        ForEach-Object { $_.Trim() }) -join ' / ')
}

# Tệp kết thúc bằng dòng "-- Dump completed ..." thì mysqldump đã ghi hết;
# thiếu dòng này là tệp bị cắt (hết đĩa, mất kết nối giữa chừng).
function Test-KetThucDayDu {
    param([string]$Tep)
    $fs = [IO.File]::OpenRead($Tep)
    try {
        $n = [int][Math]::Min(512, $fs.Length)
        if ($n -eq 0) { return $false }
        [void]$fs.Seek(-$n, [IO.SeekOrigin]::End)
        $buf = New-Object byte[] $n
        [void]$fs.Read($buf, 0, $n)
    } finally {
        $fs.Dispose()
    }
    $duoi = [Text.Encoding]::UTF8.GetString($buf).TrimEnd("`r", "`n")
    $dongCuoi = ($duoi -split "`r?`n")[-1]
    return $dongCuoi.StartsWith($Script:DongKetThuc)
}
