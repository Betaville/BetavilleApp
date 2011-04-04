REM delete_cache.bat - Batch file to delete Betaville cache directory
REM This file has almost as many comments as commands!
REM by Skye Book <skye.book@gmail.com>

rd "%USERPROFILE%\.betaville\cache" /s /q
@echo "Your Betaville cache files have been deleted!"
pause