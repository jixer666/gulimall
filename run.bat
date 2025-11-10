@echo off
chcp 65001 >nul
title 启动Nacos和Nginx服务
color 0A

echo 正在启动Nacos和Nginx服务...
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

:: 启动Nacos服务
echo 1. 启动Nacos服务...
if exist "D:\environment\nacos\2.2.1\bin\startup.cmd" (
    echo 找到Nacos启动文件，正在启动...
    cd /d "D:\environment\nacos\2.2.1\bin"
    call startup.cmd
    timeout /t 3 >nul
) else (
    echo 错误：找不到Nacos启动文件！
    echo 请检查路径：D:\environment\nacos\2.2.1\bin\startup.cmd
    pause
    exit /b 1
)

echo.
echo ===============================
echo 服务启动完成！
echo Nacos控制台：http://localhost:8848/nacos
echo Nginx默认端口：80
echo.
pause