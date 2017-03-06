@echo off
echo Checking requirements...

where git >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: git is missing
  goto :eof
)
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: mvn is missing
  goto :eof
)
where pandoc >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: pandoc is missing
  goto :eof
)
where 7z >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: 7-zip is missing
  goto :eof
)

echo Compiling plugin...
call mvn package >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: could not compile the plugin
  goto :eof
)

echo Generating documentation...
pandoc^
 docs\Home.md^
 docs\Installation.md^
 docs\Commands.md^
 docs\Lobby.md^
 docs\Game.md^
 docs\ItemSet.md^
 docs\Item.md^
 docs\Engine.md^
 docs\Wings.md^
 docs\Action.md^
 docs\Activator.md^
 docs\Bonus.md^
 docs\Modifications.md^
 -f markdown_github -t html5^
 -V margin-top=2cm -V margin-bottom=2cm -V margin-left=2cm -V margin-right=2cm^
 --css style.css^
 -o target\Documentation.pdf >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: could not generate documentation
  goto :eof
)

echo Zipping source code...
git archive -o target\SourceCode.zip HEAD docs src pom.xml >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: could not zip the source code
  goto :eof
)

echo Bundling files...
copy LICENSE target >nul 2>nul
copy README.md target >nul 2>nul
cd target
7z a Flier.zip Flier.jar Documentation.pdf SourceCode.zip LICENSE README.md >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo Error: could not bundle all items together
  goto :eof
)
del Flier.jar Documentation.pdf SourceCode.zip LICENSE README.md >nul 2>nul
cd ..

echo Flier package created!
