@echo off

REG ADD HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Policies\System /f /v DisableTaskMgr /t REG_DWORD /d 1
