Set WShell = CreateObject("WScript.Shell")
' 0 にすることで、黒いコマンドプロンプト画面を完全に非表示にして実行します
WShell.Run "cmd.exe /c ""C:\dev\LOL_App\lolMatchApp\run.bat""", 0, False
Set WShell = Nothing