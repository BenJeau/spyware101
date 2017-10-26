@echo off

REG ADD "HKEY_CURRENT_USER\Control Panel\Desktop" /V Wallpaper /T REG_SZ /D "" /F
REG ADD "HKEY_CURRENT_USER\Control Panel\Desktop" /V Wallpaper /T REG_SZ /D "C:\Users\wallpaper.bmp" /F
REG ADD "HKEY_CURRENT_USER\Control Panel\Desktop" /V WallpaperStyle /T REG_SZ /F /D 0
REG ADD "HKEY_CURRENT_USER\Control Panel\Desktop" /V TileWallpaper /T REG_SZ /F /D 2
RUNDLL32.EXE user32.dll,UpdatePerUserSystemParameters
