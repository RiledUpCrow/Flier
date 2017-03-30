# Game

Game is the most important object in Flier. It contains all rules like teams, game goal, what happens after death etc. Games are contained in Lobbies, but only one can be active per Lobby at any time. Every game has a type. Available types are listed and described below. You define your games in _games.yml_ file:

```
game_name:
  type: [game type]
  bonuses:
  - some_bonus
  - other_bonus
  effects:
  - some_effect
  - other_effect
  height_limit: 0
  height_damage: 1
  money:
    enabled: false
    enemy_kill: 0
    enemy_hit: 0
    friendly_kill: 0
    friendly_hit: 0
    by_enemy_death: 0
    by_enemy_hit: 0
    by_friendly_death: 0
    by_friendly_hit: 0
    suicide: 0
  [type specific settings]
```

## Game settings

These settings apply to every game. Some of them have default values, so if you don't specify them explicitly these will be used. Some values however are required.

* `type` (**required**) type of the game. The value used here will determine what additional settings are available. Types are listed below.
* `bonuses` list of bonuses available in the game. You don't have to specify this if you don't want any bonuses.
* `effects` list of effects available in this game. You don't have to specify this if you don't want any effects.
* `height_limit` (**default: 0**) a height at which players will receive suffocation damage (kind of simulating low air pressure). If it's 0 or lower, it doesn't apply.
* `height_damage` (**default: 1**) the amount of suffocation damage per second. It can use fractions.
* `money` settings responsible for money in the game. If you don't want to use this system you can simply omit this section.
  * `enabled` (**default: false**) whenever the money system is enabled. If it's _true_, money will be displayed on the sidebar.
  * `enemy_kill` (**default: 0**) amount of money the player receives for killing an enemy.
  * `enemy_hit` (**default: 0**) amount of money the player receives for hitting an enemy. It can be negative.
  * `friendly_kill` (**default: 0**) amount of money the player receives for killing an ally. It can be negative.
  * `friendly_hit` (**default: 0**) amount of money the player receives for hitting an ally. It can be negative.
  * `by_enemy_death` (**default: 0**) amount of money the player receives for being killed by an enemy. It can be negative.
  * `by_enemy_hit` (**default: 0**) amount of money the player receives for being hit by an enemy. It can be negative.
  * `by_friendly_death` (**default: 0**) amount of money the player receives for being killed by an ally. It can be negative.
  * `by_friendly_hit` (**default: 0**) amount of money the player receives for being hit by an ally. It can be negative.
  * `suicide` (**default: 0**) amount of money the player receives for killing himself. It can be negative.

## Game types

Each game type adds additional settings to configure game-specific rules, for example spawn points and teams.

### Team DeathMatch

**`teamDeathMatch`**

This game divides players into teams. Each team's objective is to kill players from other teams. Every team has a separate spawn location, color and name.

```
game_name:
  type: teamDeathMatch
  [default game settings]
  suicide_score: 0
  friendly_kill_score: 0
  enemy_kill_score: 1
  teams:
    team_id:
      name: [team name]
      color: [color]
      spawn: [location]
    another_team_id:
      [...]
```

* `suicide_score` (**default: 0**) points given to a team if one of its members commits a suicide.
* `friendly_kill_score` (**default: 0**) points given to a team if one of its members kills an ally.
* `enemy_kill_score` (**default: 1**) points given to a team if one of its members kills an enemy.
* `teams` list of teams in the game.
  * `name` (**required**) the name of the team.
  * `color` (**required**) the color of the team (from [this list](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html)).
  * `spawn` (**required**) the spawn location of the team, for example `100;200;300;world;90;0`, where first three numbers are XYZ coordinates, next is the world name, and last two numbers are yaw and pitch (head rotation).
