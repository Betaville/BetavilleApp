REM delete_cache.bat - Batch file to delete Betaville cache directory
REM This file has almost as many comments as commands!
REM by Skye Book <skye.book@gmail.com>

chdir %USERPROFILE%\.betaville
rd cache /s /q
@echo "Your Betaville cache files have been deleted!"
pause