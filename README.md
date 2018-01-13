# Flier

Flier is a Spigot minigame plugin. It lets players fly with engine-powered Elytras and shoot each other. It doesn't allow Minecraft's regular PvP mechanics, instead featuring a wide array of custom gun-like weapons, rockets and bombs.

## Getting started

If you prefer already compiled builds, please head to the [Spigot page](https://www.spigotmc.org/resources/flier.47712/) of the plugin. This section will cover setting up Flier for development.

### Prerequisites

First of all you need to have [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (version 8 or later) installed on your system. This will let you compile Java programs. Next you have to install [Maven](https://maven.apache.org/download.cgi), the build automation tool. Just grab a binary zip archive and unpack it somewhere on your system. Make sure the `bin` directory is [added to the PATH](https://www.google.com/search?q=add+directory+to+path).

If you want to be able to use the build script to create a nice zipped package you also need following programs installed:

* [Git](https://git-scm.com) (grabbing the source code)
* [Pandoc](https://pandoc.org) (generating the documentation)
* [wkhtmltopdf](https://wkhtmltopdf.org) (converting documentation to PDF)
* [7zip](http://www.7-zip.org) (packaging the bundle)
* [Windows](https://www.microsoft.com/en-us/windows/) (build script is a .bat file)

### Building

Compiling the plugin alone is quite easy. Simply open the command line in the plugin's directory and issue this command:

```
mvn package
```

Maven will build and package the plugin. If you see a _Build successful_ message then the compiled _jar_ file was placed inside `target` directory. You can copy it into your server's `plugins` directory now.

### Using the build.bat script

This script is meant to automate the release process and provide a consistent way of packaging bundles under Windows. It will compile the plugin, its documentation and other important resources and output a _zip_ bundle. It's not really important for development, just for the official release process. In any case, you can run it on Windows simply by double-clicking it in Explorer. It will tell you if it finds any issues.

## Documentation

The plugin is thoroughly documented in the `docs` directory. All files are written in Markdown. When adding new features they should be immediately documented there. The order of the files in the build script is as follows:

* [Home](docs/Home.md)
* [Installation](docs/Installation.md)
* [Commands](docs/Commands.md)
* [Integrations](docs/Integrations.md)
* [Lobby](docs/Lobby.md)
* [Arena](docs/Arena.md)
* [Game](docs/Game.md)
* [ItemSet](docs/ItemSet.md)
* [Item](docs/Item.md)
* [Engine](docs/Engine.md)
* [Wings](docs/Wings.md)
* [Action](docs/Action.md)
* [Activator](docs/Activator.md)
* [Bonus](docs/Bonus.md)
* [Modifications](docs/Modifications.md)
* [Effects](docs/Effects.md)

## API

The stable programming interface is not designed yet. Once version `1.0` is developed it will be available at a separate repository. The currently existing API is not stable yet and may change without warning.

## Versioning

Flier uses [SemVer](https://semver.org) for versioning. For the official available versions see the [tags of this repository](https://github.com/Co0sh/Flier/tags).

## License

The project is licensed under MIT license - see the [LICENSE](LICENSE) file for more details.
