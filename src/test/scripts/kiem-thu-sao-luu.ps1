<#
.SYNOPSIS
    Kiểm thử scripts/sao-luu.ps1 bằng mysqldump giả, không cần MySQL (TS-001).

.DESCRIPTION
    Thay mysqldump bằng một script giả (.cmd trên Windows, sh trên Linux) để
    tạo các tình huống khó gặp với MySQL thật: mysqldump lỗi giữa chừng,
    mysqldump trả mã 0 nhưng tệp bị cắt. Kiểm tra mã thoát, dòng log, tệp
    dở bị xóa và chỉ xóa bản cũ khi sao lưu thành công.

    Chạy: powershell -ExecutionPolicy Bypass -File src\test\scripts\kiem-thu-sao-luu.ps1
          pwsh src/test/scripts/kiem-thu-sao-luu.ps1
    Thoát mã 0 nếu mọi kiểm tra đạt.
#>
$ErrorActionPreference = 'Stop'
Set-StrictMode -Version 2.0

$repo = Split-Path (Split-Path (Split-Path $PSScriptRoot -Parent) -Parent) -Parent
$saoLuu = Join-Path (Join-Path $repo 'scripts') 'sao-luu.ps1'
$shell = (Get-Process -Id $PID).Path
$laWindows = [IO.Path]::DirectorySeparatorChar -eq '\'
$MauTen = '^bluemoon-\d{8}-\d{6}\.sql$'

$goc = Join-Path ([IO.Path]::GetTempPath()) ("bluemoon-kt-" + [Guid]::NewGuid().ToString('N'))
[void](New-Item -ItemType Directory -Path $goc)
$soLoi = 0

function Assert-Dung {
    param([bool]$DieuKien, [string]$MoTa)
    if ($DieuKien) {
        Write-Output "  đạt: $MoTa"
    } else {
        Write-Output "  TRƯỢT: $MoTa"
        $script:soLoi++
    }
}

# ---- mysqldump giả ----
$tepDoiSo = Join-Path $goc 'doi-so.txt'
if ($laWindows) {
    $gia = Join-Path $goc 'mysqldump-gia.cmd'
    $noiDung = @(
        '@echo off'
        'echo %*> "%FAKE_ARGS%"'
        'if "%FAKE_MODE%"=="loi" goto loi'
        'echo -- MySQL dump 10.13'
        'echo CREATE TABLE t (id int);'
        'if "%FAKE_MODE%"=="cat" exit /b 0'
        'echo -- Dump completed on 2026-11-13 22:00:00'
        'exit /b 0'
        ':loi'
        'echo -- MySQL dump 10.13'
        'echo INSERT INTO t VALUES (1'
        'echo mysqldump: Got error: 2013: Lost connection to MySQL server during query 1>&2'
        'exit /b 2'
    ) -join "`r`n"
} else {
    $gia = Join-Path $goc 'mysqldump-gia.sh'
    $noiDung = @(
        '#!/bin/sh'
        'echo "$@" > "$FAKE_ARGS"'
        'if [ "$FAKE_MODE" = loi ]; then'
        '  echo "-- MySQL dump 10.13"; echo "INSERT INTO t VALUES (1"'
        '  echo "mysqldump: Got error: 2013: Lost connection to MySQL server during query" >&2'
        '  exit 2'
        'fi'
        'echo "-- MySQL dump 10.13"'
        'echo "CREATE TABLE t (id int);"'
        '[ "$FAKE_MODE" = cat ] && exit 0'
        'echo "-- Dump completed on 2026-11-13 22:00:00"'
        'exit 0'
    ) -join "`n"
}
[IO.File]::WriteAllText($gia, $noiDung + "`n", (New-Object System.Text.UTF8Encoding($false)))
if (-not $laWindows) { & chmod +x $gia }

$tepTuyChon = Join-Path $goc 'gia.cnf'
[IO.File]::WriteAllText($tepTuyChon, "[client]`nuser=khong_dung`n")

function Invoke-SaoLuu {
    param([string]$Thu, [string]$CheDo, [int]$Giu, [string]$TuyChon = $tepTuyChon)
    $ErrorActionPreference = 'Continue'
    $env:FAKE_MODE = $CheDo
    $env:FAKE_ARGS = $tepDoiSo
    $ra = & $shell -NoProfile -ExecutionPolicy Bypass -File $saoLuu `
        -CoSoDuLieu bluemoon_kt -ThuMucDich $Thu -SoBanGiu $Giu `
        -MysqlDump $gia -TepTuyChon $TuyChon 2>&1
    $ma = $LASTEXITCODE
    $ra | ForEach-Object { Write-Host "    > $_" }
    return $ma
}

function New-ThuMucCoBanCu {
    param([string]$Ten)
    $thu = Join-Path $goc $Ten
    [void](New-Item -ItemType Directory -Path $thu)
    foreach ($t in @('bluemoon-20260101-220000.sql', 'bluemoon-20260102-220000.sql',
            'bluemoon-20260103-220000.sql', 'bluemoon-20260104-220000.sql',
            'ghi-chu.txt', 'bluemoon-khac.sql', 'bluemoon-20260105-220000.sql.bak')) {
        [IO.File]::WriteAllText((Join-Path $thu $t), "cu`n")
    }
    return $thu
}

function Get-Ten {
    param([string]$Thu)
    return @(Get-ChildItem -LiteralPath $Thu -File | ForEach-Object { $_.Name } | Sort-Object)
}

function Get-DongLogCuoi {
    param([string]$Thu)
    $log = Join-Path $Thu 'sao-luu.log'
    if (-not (Test-Path -LiteralPath $log)) { return '' }
    return @([IO.File]::ReadAllLines($log, [Text.Encoding]::UTF8))[-1]
}

function Test-ThatBai {
    param([string]$CheDo, [string]$MoTa)
    Write-Output "$MoTa"
    $thu = New-ThuMucCoBanCu $CheDo
    $truoc = Get-Ten $thu
    $ma = Invoke-SaoLuu -Thu $thu -CheDo $CheDo -Giu 1
    Assert-Dung ($ma -ne 0) "mã thoát khác 0 (là $ma)"
    $dong = Get-DongLogCuoi $thu
    Assert-Dung ($dong -match '^\d{4}-\d\d-\d\d \d\d:\d\d:\d\d \| LỖI \| bluemoon-\d{8}-\d{6}\.sql \|') "log có dòng LỖI kèm thời điểm và tên tệp: $dong"
    $sau = @(Get-Ten $thu | Where-Object { $_ -ne 'sao-luu.log' })
    Assert-Dung (($sau -join ',') -eq ($truoc -join ',')) 'không còn tệp dở, không xóa bản cũ nào dù -SoBanGiu 1'
}

try {
    # 1. Thành công: tệp đủ, log, chỉ giữ N bản mới nhất khớp mẫu tên.
    Write-Output 'Sao lưu thành công giữ đúng 3 bản mới nhất'
    $thu = New-ThuMucCoBanCu 'thanh-cong'
    $ma = Invoke-SaoLuu -Thu $thu -CheDo 'du' -Giu 3
    Assert-Dung ($ma -eq 0) "mã thoát 0 (là $ma)"
    $ban = @(Get-Ten $thu | Where-Object { $_ -cmatch $MauTen })
    Assert-Dung ($ban.Count -eq 3) "còn 3 bản khớp mẫu tên (có $($ban.Count): $($ban -join ', '))"
    $moi = @($ban | Where-Object { $_ -notlike 'bluemoon-202601*' })
    Assert-Dung ($moi.Count -eq 1) 'có đúng một bản mới'
    Assert-Dung (($ban -contains 'bluemoon-20260104-220000.sql') -and
        ($ban -contains 'bluemoon-20260103-220000.sql')) 'giữ hai bản cũ mới nhất'
    foreach ($khac in @('ghi-chu.txt', 'bluemoon-khac.sql', 'bluemoon-20260105-220000.sql.bak')) {
        Assert-Dung (Test-Path -LiteralPath (Join-Path $thu $khac)) "không đụng tệp khác mẫu tên: $khac"
    }
    Assert-Dung (@(Get-ChildItem -LiteralPath $thu -Filter '*.tmp').Count -eq 0) 'không còn tệp tạm'
    if ($moi.Count -eq 1) {
        $noi = [IO.File]::ReadAllText((Join-Path $thu $moi[0]))
        Assert-Dung ($noi.TrimEnd().EndsWith('-- Dump completed on 2026-11-13 22:00:00')) 'tệp có đủ nội dung mysqldump ghi ra'
        $dong = Get-DongLogCuoi $thu
        Assert-Dung ($dong -match ('^\d{4}-\d\d-\d\d \d\d:\d\d:\d\d \| THÀNH CÔNG \| ' +
            [regex]::Escape($moi[0]) + ' \| \d+(,\d)? (B|KB|MB) \| \d+,\d s')) "log có dòng THÀNH CÔNG kèm tên tệp, dung lượng, thời gian: $dong"
    }
    $doiSo = ([IO.File]::ReadAllText($tepDoiSo)).Trim()
    Assert-Dung ($doiSo.StartsWith('--defaults-extra-file=')) "tệp tùy chọn là đối số đầu: $doiSo"
    Assert-Dung ($doiSo.EndsWith('bluemoon_kt')) 'tên CSDL là đối số cuối'
    Assert-Dung ($doiSo -notmatch '(^|\s)(--databases|-B|--password|-p)') 'không có --databases, không có mật khẩu trên dòng lệnh'

    # 2. mysqldump lỗi giữa chừng.
    Test-ThatBai -CheDo 'loi' -MoTa 'mysqldump trả mã lỗi sau khi đã ghi một phần'
    # 3. mysqldump trả 0 nhưng tệp không có dòng "-- Dump completed".
    Test-ThatBai -CheDo 'cat' -MoTa 'tệp bị cắt dù mysqldump trả mã 0'

    # 4. Tệp tùy chọn trong repo bị từ chối (dễ lỡ commit mật khẩu).
    Write-Output 'Tệp tùy chọn nằm trong repo'
    $thu = Join-Path $goc 'trong-repo'
    $ma = Invoke-SaoLuu -Thu $thu -CheDo 'du' -Giu 3 -TuyChon (Join-Path $repo 'pom.xml')
    Assert-Dung ($ma -ne 0) "mã thoát khác 0 (là $ma)"
    Assert-Dung ((Get-DongLogCuoi $thu) -match '\| LỖI \|.*nằm trong repo') 'log ghi lý do'
} finally {
    Remove-Item -LiteralPath $goc -Recurse -Force -ErrorAction SilentlyContinue
    Remove-Item Env:\FAKE_MODE, Env:\FAKE_ARGS -ErrorAction SilentlyContinue
}

if ($soLoi -gt 0) {
    Write-Output "KẾT QUẢ: $soLoi kiểm tra trượt"
    exit 1
}
Write-Output 'KẾT QUẢ: mọi kiểm tra đạt'
exit 0
