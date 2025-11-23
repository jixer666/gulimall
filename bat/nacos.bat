@echo off
chcp 65001 >nul
title 启动Nacos
color 0A

echo 正在启动Nacos服务...
echo ===============================

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

pause