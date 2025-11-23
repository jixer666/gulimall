@echo off
chcp 65001 >nul
title 启动Nginx
color 0A

echo 正在启动Nginx服务...
echo ===============================

:: 启动Nginx服务
echo.
echo 2. 启动Nginx服务...
if exist "D:\environment\nginx\nginx-1.23.1\nginx.exe" (
    echo 找到Nginx，正在启动...
    cd /d "D:\environment\nginx\nginx-1.23.1"
    start "" "nginx.exe"
    echo Nginx启动完成！
) else (
    echo 错误：找不到Nginx可执行文件！
    echo 请检查路径：D:\environment\nginx\nginx-1.23.1\nginx.exe
    pause
    exit /b 1
)

pause