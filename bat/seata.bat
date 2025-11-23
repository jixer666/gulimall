

@echo off
chcp 65001 >nul
title 启动Seata
color 0A

echo 正在启动Seata服务...
echo ===============================

:: 启动Seata服务
echo 1. 启动Seata服务...
if exist "D:\environment\seata\0.9.0\bin\seata-server.bat" (
    echo 找到Seata启动文件，正在启动...
    cd /d "D:\environment\seata\0.9.0\bin"
    call seata-server.bat
    timeout /t 3 >nul
) else (
    echo 错误：找不到Seata启动文件！
    echo 请检查路径：D:\environment\seata\0.9.0\bin\seata-server.bat
    pause
    exit /b 1
)

pause