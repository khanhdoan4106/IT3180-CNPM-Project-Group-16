<#
.SYNOPSIS
    Đăng ký (hoặc gỡ) tác vụ Task Scheduler chạy sao-luu.ps1 hằng ngày
    (TS-001, AC-066).

.DESCRIPTION
    Tạo tác vụ "BlueMoon – Sao lưu hằng ngày" chạy sao-luu.ps1 lúc -Gio
    (mặc định 22:00) mỗi ngày bằng tài khoản Windows đang đăng nhập. Máy tắt
    hoặc ngủ đúng giờ thì tác vụ chạy ngay khi máy bật lại (StartWhenAvailable).
    Chạy lại script này để đổi giờ hay tham số: tác vụ cũ được thay.

    Tác vụ chạy bằng tài khoản Windows hiện tại vì login-path của
    mysql_config_editor lưu theo từng tài khoản. Mặc định chỉ chạy khi tài
    khoản đang đăng nhập; -ChayKhiChuaDangNhap cho chạy cả khi chưa đăng
    nhập (kiểu S4U, không lưu mật khẩu Windows).

    Người dùng tự chạy script này trên máy đặt MySQL; script không tự chạy
    ở đâu khác.

.EXAMPLE
    powershell -ExecutionPolicy Bypass -File scripts\dang-ky-lich-sao-luu.ps1
.EXAMPLE
    powershell -ExecutionPolicy Bypass -File scripts\dang-ky-lich-sao-luu.ps1 -Gio 21:30 -ThuMucDich D:\BlueMoon\sao-luu
.EXAMPLE
    powershell -ExecutionPolicy Bypass -File scripts\dang-ky-lich-sao-luu.ps1 -Xoa
#>
[CmdletBinding()]
param(
    [ValidatePattern('^([01]?\d|2[0-3]):[0-5]\d$')]
    [string]$Gio = '22:00',
    [string]$CoSoDuLieu,
    [string]$ThuMucDich,
    [int]$SoBanGiu,
    [string]$MysqlDump,
    [string]$LoginPath,
    [string]$TepTuyChon,
    [switch]$ChayKhiChuaDangNhap,
    [switch]$Xoa
)

$ErrorActionPreference = 'Stop'
$TenTacVu = 'BlueMoon – Sao lưu hằng ngày'

if ($PSVersionTable.PSEdition -eq 'Core' -and -not $IsWindows) {
    throw 'Task Scheduler chỉ có trên Windows.'
}

if ($Xoa) {
    if (Get-ScheduledTask -TaskName $TenTacVu -ErrorAction SilentlyContinue) {
        Unregister-ScheduledTask -TaskName $TenTacVu -Confirm:$false
        Write-Output "Đã gỡ tác vụ '$TenTacVu'. Các tệp sao lưu và sao-luu.log vẫn giữ nguyên."
    } else {
        Write-Output "Không có tác vụ '$TenTacVu'."
    }
    exit 0
}

$script = Join-Path $PSScriptRoot 'sao-luu.ps1'
if (-not (Test-Path -LiteralPath $script)) {
    throw "Không thấy $script"
}

function Format-DoiSo {
    param([string]$s)
    # Gạch chéo ngược cuối (vd D:\) phải nhân đôi để không nuốt dấu ngoặc kép.
    $s = $s -replace '(\\+)$', '$1$1'
    return '"' + $s.Replace('"', '\"') + '"'
}

# Chỉ chuyển tiếp tham số người dùng đã đặt; còn lại dùng mặc định của
# sao-luu.ps1. Không có tham số mật khẩu.
$doiSo = @('-NoProfile', '-NonInteractive', '-WindowStyle', 'Hidden',
    '-ExecutionPolicy', 'Bypass', '-File', (Format-DoiSo $script))
foreach ($ten in @('CoSoDuLieu', 'ThuMucDich', 'SoBanGiu', 'MysqlDump',
        'LoginPath', 'TepTuyChon')) {
    if ($PSBoundParameters.ContainsKey($ten)) {
        $doiSo += "-$ten"
        $doiSo += (Format-DoiSo ([string]$PSBoundParameters[$ten]))
    }
}

$hanhDong = New-ScheduledTaskAction -Execute 'powershell.exe' `
    -Argument ($doiSo -join ' ') -WorkingDirectory $PSScriptRoot
$lich = New-ScheduledTaskTrigger -Daily -At $Gio
$caiDat = New-ScheduledTaskSettingsSet -StartWhenAvailable `
    -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries `
    -ExecutionTimeLimit (New-TimeSpan -Hours 2) -MultipleInstances IgnoreNew
$nguoiDung = [Security.Principal.WindowsIdentity]::GetCurrent().Name
$kieuDangNhap = if ($ChayKhiChuaDangNhap) { 'S4U' } else { 'Interactive' }
$chuThe = New-ScheduledTaskPrincipal -UserId $nguoiDung -LogonType $kieuDangNhap `
    -RunLevel Limited

Register-ScheduledTask -TaskName $TenTacVu -Action $hanhDong -Trigger $lich `
    -Settings $caiDat -Principal $chuThe -Force `
    -Description 'Sao lưu CSDL BlueMoon bằng mysqldump (TS-001). Log: sao-luu.log trong thư mục sao lưu.' |
    Out-Null

Write-Output "Đã đăng ký '$TenTacVu' lúc $Gio hằng ngày, chạy bằng tài khoản $nguoiDung."
Write-Output "Lệnh: powershell.exe $($doiSo -join ' ')"
Write-Output "Chạy thử ngay: Start-ScheduledTask -TaskName '$TenTacVu'"
