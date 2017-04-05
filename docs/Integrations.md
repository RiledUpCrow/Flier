# Integrations

Flier is fully standalone and doesn't need any additional plugins to work. You can however install these nice plugins to enable additional functionality:

## [BountifulAPI](https://www.spigotmc.org/resources/bountifulapi-1-8-1-9-1-10.1394/)

Installing this plugin will remove command messages from the console when displaying titles and will also display ammunition on the action bar instead of the sidebar.

## [BetonQuest](https://www.spigotmc.org/resources/betonquest.2117/)

Flier adds a few conditions and objectives to the BetonQuest plugin so you can create better games. An example use of this is to create a tutorial for player playing Flier for the first time.

### Conditions

#### Activator: `activator`

This condition checks if the specified activator is active for the player. The first argument is the name of the activator. Keep in mind that activators can only be active during the game.

**Example**: `activator hasAmmo`

#### Engine: `flierengine`

This condition checks if the player is accelerating with his engine. There are no additional arguments.

**Example**: `flierengine`

#### Game: `ingame`

This condition checks if the player is in specified game. It will be true even if he's waiting in the waiting room. The first argument is the game's name.

**Example**: `ingame default`

#### Lobby: `inlobby`

This condition checks if the player is in specified lobby. It will be true event if the player was moved to the game. The first argument is the lobby's name.

**Example**: `inlobby default`

### Objectives

#### Join lobby: `joinlobby`

This objective gets completed when the player joins a correct lobby. The first argument is the lobby's name.

**Example**: `joinlobby default`

#### Join game: `joingame`

This objective gets completed when the player joins a correct game. The first argument is the game's name.

**Example**: `joingame default`

#### Respawn: `flierrespawn`

This objective will be completed upon player's respawn in the game. This includes first time spawn when the game is starting.

**Example**: `flierrespawn`

#### Use item: `flieruse`

This objective requires the player to use an item. You can optionally specify which item or usage must be used. For example if you want the player to use a weapon and all your weapons have _shoot_ usage, you can require that usage on any item instead of creating many objectives, one per weapon. The two optional arguments start with `item:` and `usage:` followed by the name of an item/usage. You can use none, one of them or both.

**Example**: `flieruse usage:shoot`
**Example**: `flieruse item:gun usage:reload`

#### Click button: `flierbutton`

This objective will be completed when you click specified button. The first argument is the button's name.

**Example**: `flierbutton fast_class`

#### Kill: `flierkill`

This objective gets completed when you kill specified amount of other players. The first argument is the amount of players to kill. You can additionally specify a list of BetonQuest conditions these players need to meet in order to be accepted (`required:` optional argument) and if you want to target specific player, you can use `name:` optional argument.

**Example**: `flierkill 5 required:flying`

#### Hit: `flierhit`

This objective is essentially the same as `flierkill`, except you need to hit players with your weapon instead of killing them.

**Example**: `flierhit 10 required:!flying`

#### Death: `flierdeath`

To complete this objective you need to die.

**Example**: `flierdeath`

#### Get hit: `fliergethit`

To complete this objective you need to get hit.

**Example**: `fliergethit`

#### 

## [BetonLangAPI](https://github.com/Co0sh/BetonLangAPI)

This plugin is a language library which allows displaying translated messages based on player's chosen language. It supports MySQL database too, so you can use it to sync multiple server or a website.

Item names, lore, team and class names can be translated into multiple languages by replacing their text with `$something` tags and putting actual text in the _messages.yml_ under `something` keys. Just make sure your tags don't collide with existing messages used by the plugin. An example of an item set (from _sets.yml_ file) which has translated class name:

```
translated_set:
  name: '$className'
  category: main
  [...]
```

And in the _messages.yml_:

```
en:
  className: Fast
  [...]
pl:
  className: Szybki
  [...]
```
