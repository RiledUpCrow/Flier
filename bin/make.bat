@echo off
echo Checking requirements...

where git >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: git is missing
  pause >nul
  goto :eof
)
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: mvn is missing
  pause >nul
  goto :eof
)
where pandoc >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: pandoc is missing
  pause >nul
  goto :eof
)
where 7z >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: 7-zip is missing
  pause >nul
  goto :eof
)

echo Compiling plugin...
echo Flier-${project.version}.zip > src\main\resources\version.txt
call mvn clean package >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  del src\main\resources\version.txt
  echo Error: could not compile the plugin
  pause >nul
  goto :eof
)
7z d target\Flier.jar version.txt README.md >nul 2>nul
del src\main\resources\version.txt
for /F "tokens=* USEBACKQ" %%F in (`type target\classes\version.txt`) do (
  set flier=%%F
)

echo Generating documentation...
pandoc^
 docs\Home.md^
 docs\Installation.md^
 docs\Commands.md^
 docs\Integrations.md^
 docs\Lobby.md^
 docs\Arena.md^
 docs\Game.md^
 docs\ItemSet.md^
 docs\Item.md^
 docs\Engine.md^
 docs\Wings.md^
 docs\Action.md^
 docs\Activator.md^
 docs\Bonus.md^
 docs\Modifications.md^
 docs\Effects.md^
 -f markdown_github -t html5^
 -V margin-top=2cm -V margin-bottom=2cm -V margin-left=2cm -V margin-right=2cm^
 --css style.css^
 -o target\Documentation.pdf >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: could not generate documentation
  pause >nul
  goto :eof
)

echo Zipping source code...
git archive -o target\SourceCode.zip HEAD src pom.xml LICENSE >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: could not zip the source code
  pause >nul
  goto :eof
)

echo Bundling files...
type changelog.txt > target\ChangeLog.txt
type LICENSE > target\License.txt
cd target
7z a %flier% Flier.jar Documentation.pdf SourceCode.zip ChangeLog.txt License.txt >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: could not bundle all items together
  pause >nul
  goto :eof
)
del Documentation.pdf SourceCode.zip ChangeLog.txt License.txt >nul 2>nul
cd ..

echo Flier package created!
