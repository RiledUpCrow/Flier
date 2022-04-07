#!/bin/sh
echo Checking requirements...

if ! [ -x "$(command -v git)" ]; then
  echo Error: git is not installed
  exit 1
fi

if ! [ -x "$(command -v mvn)" ]; then
  echo Error: mvn is not installed
  exit 1
fi

if ! [ -x "$(command -v pandoc)" ]; then
  echo Error: pandoc is not installed
  exit 1
fi

if ! [ -x "$(command -v wkhtmltopdf)" ]; then
  echo Error: wkhtmltopdf is not installed
  exit 1
fi

if ! [ -x "$(command -v 7z)" ]; then
  echo Error: Z-zip is not installed
  exit 1
fi

echo Compiling plugin...
echo Flier.zip >> src/main/resources/version.txt

mvn clean package --batch-mode
if [ $? -eq 0 ]
then
  echo "Successfully compiled"
else
  echo "Error: could not compile the plugin"
  rm src/main/resources/version.txt
  exit 1
fi

7z d target/Flier.jar version.txt README.md
rm src/main/resources/version.txt
# for /F "tokens=* USEBACKQ" %%F in (`type target\classes\version.txt`) do (
#   set flier=%%F
# )

echo Generating documentation...
pandoc \
 docs/Home.md \
 docs/Installation.md \
 docs/Commands.md \
 docs/Integrations.md \
 docs/Lobby.md \
 docs/Arena.md \
 docs/Game.md \
 docs/ItemSet.md \
 docs/Item.md \
 docs/Engine.md \
 docs/Wings.md \
 docs/Action.md \
 docs/Activator.md \
 docs/Bonus.md \
 docs/Modifications.md \
 docs/Effects.md \
 -f gfm -t html5 \
 -V margin-top=2cm -V margin-bottom=2cm -V margin-left=2cm -V margin-right=2cm \
 --css style.css \
 -o target/Documentation.pdf
if [ $? -eq 0 ]
then
  echo "Documentation generated"
else
  echo "Error: could not generate documentation"
  exit 1
fi


echo Zipping source code...
git archive -o target/SourceCode.zip HEAD src pom.xml LICENSE
if [ $? -eq 0 ]
then
  echo "Source code zipped"
else
  echo "Error: could not zip the source code"
  exit 1
fi

echo Bundling files...
cd target
7z a Flier Flier.jar Documentation.pdf

if [ $? -eq 0 ]
then
  echo "OK"
else
  echo "Error: could not bundle all items together"
  exit 1
fi

rm Documentation.pdf
cd ..

echo Flier package created!
