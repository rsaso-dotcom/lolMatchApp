@echo off
cd /d "C:\dev\LOL_App\lolMatchApp"

:: JavaFX Mavenプラグインを正式な名前で実行する
C:\apache-maven-3.10.0\bin\mvn.cmd org.openjfx:javafx-maven-plugin:0.0.8:run

pausedir