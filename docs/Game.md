# Game

A game is the most important object in Flier. It contains all settings like teams, game goal, what happens after death etc. Games are contained in lobbies. Every game has a type - available types are listed and described below. You define your games in _games.yml_ file.

Every game is run on an "arena" - a set of locations required by the game, like spawns and bonuses. If you define more than one arena, you can run the same game many times simultaneously (as in "the same ruleset", not exactly "the same instance") - the lobby will take the first available arena and create new game there if needed.

```
game_name:
  type: [game type]
  name: [translatable text]
  rounds: [true/false]
  bonuses:
  - [bonus names]
  effects:
  - [effect names]
  rounds: [true/false]
  max_players: [non-negative integer]
  min_players: [positive integer]
  respawn_delay: [non-negative integer]
  start_delay: [non-negative integer]
  locking: [true/false]
  waiting_room: [arena location (single)]
  center: [arena location (single)]
  radius: [integer]
  leave_blocks: [arena locations (multiple)]
  viable_arenas:
  - [arena name]
  respawn_action: [respawn action]
  default_kit:
  - [item set names]
  money:
    enabled: [true/false]
    enemy_kill: [integer]
    enemy_hit: [integer]
    friendly_kill: [integer]
    friendly_hit: [integer]
    by_enemy_death: [integer]
    by_enemy_hit: [integer]
    by_friendly_death: [integer]
    by_friendly_hit: [integer]
    suicide: [integer]
  buttons:
    button_name:
      buy_cost: [integer]
      on_buy:
        item_set: [item set name]
        add_type: [add type]
        amount: [non-negative integer]
        conflict_action: [conflict action]
        saving: [true/false]
      sell_cost: [integer]
      on_sell:
        [the same as in on_buy]
      unlock_cost: [integer]
      on_unlock:
        [the same as in on_buy]
      blocks: [arena locations (multiple)]
  [type specific settings]
```

## Game settings

These settings apply to every game. Some of them have default values, so if you don't specify them explicitly these will be used. Some values however are required.

* `type` (**required**) type of the game. The value used here will determine what additional settings are available. Types are listed below.
* `name` (**default: `id`**) the display name of this game.
* `rounds` (**required**) whenever the game uses rounds or not. Rounds are managed by each game differently, so they are described in the _Game types_ section below. Generally in continuous games players will respawn immediately (delay may apply though) and in games with rounds players will have to wait until the round is finished to respawn.
* `bonuses` (**optional**) a list of bonuses available in the game. You don't have to specify this if you don't want any bonuses.
* `effects` (**optional**) a list of effects available in this game. You don't have to specify this if you don't want any effects.
* `rounds` (**required**) whenever the game has rounds or is continuous. Games with rounds will make dead players wait until only one winner remains and then increase that winner's score. Continuous games will keep respawning players and give points on a regular basis.
* `max_players` (**default: 0**) the maximum amount of players in this game. If it's reached, the lobby will try to create another game.
* `min_players` (**default: 1**) the minimum amount of players required for this game to start. The players will sit in the waiting room until there are enough of them to start the game.
* `respawn_delay` (**default: 0**) the time which needs to pass until the players are respawned after death. The counter starts when the first player dies and at the end all waiting players will be spawned in one batch.
* `start_delay` (**default: 0**) the time which needs to pass before the game starts when all required players (`min_players`) are in the waiting room.
* `locking` (**default: false**) whenever the game will be locked after starting - new players can't join the locked game.
* `waiting_room` (**required**) the location of the waiting room, as defined in _arenas.yml_ file.
* `center` (**required**) the center location of the game arena, as defined in _arenas.yml_ file.
* `radius` (**required**) the radius around the center location players are allowed to fly.
* `leave_blocks` (**optional**) locations of blocks which will move players out of the game when clicked.
* `viable_arenas` (**required at least one**) list of arenas on which this game can be played. New games will be created in the first free arena from this list.

***

* `respawn_action` specifies what happens with players' items after they respawn in the lobby.

    In order to understand it you need to know that each player class consists of two "stages": stored and current. Current stage reflects what's on the player. If he consumes an item, it will disappear from the current stage. Stored stage on the other hands holds items which were "saved" to that stage - the default kit and the item sets that are specifically told to be "saved".

    Knowing this, the available actions upon the player's respawn are:

    * `clear` - removes all current items and gives all stored ones,
    * `combine` - adds all stored items to the current ones without removing anything,
    * `nothing` - doesn't modify anything.

    By default all Item Sets are placed in the current stage. If you want to add an item set to the stored stage, you need to set `saving` to `true` in the button settings which are described below.

***

* `default_kit` is the list of item sets which will be applied to the player when he joins the lobby. All these sets are applied as `fill`, `replace`, `saving: true` and `amount: 1` (these settings will mean more when you read the _Item Set_ chapter). The IDs here come from the _sets.yml_ file.

***

* `money` settings are responsible for money in the game. If you don't want to use this system you can simply skip (or delete) this section.
  * `enabled` (**default: false**) whenever the money system is enabled. If it's _true_, money will be displayed on the sidebar.
  * `enemy_kill` (**default: 0**) amount of money the player receives for killing an enemy. It can be negative.
  * `enemy_hit` (**default: 0**) amount of money the player receives for hitting an enemy. It can be negative.
  * `friendly_kill` (**default: 0**) amount of money the player receives for killing an ally. It can be negative.
  * `friendly_hit` (**default: 0**) amount of money the player receives for hitting an ally. It can be negative.
  * `by_enemy_death` (**default: 0**) amount of money the player receives for being killed by an enemy. It can be negative.
  * `by_enemy_hit` (**default: 0**) amount of money the player receives for being hit by an enemy. It can be negative.
  * `by_friendly_death` (**default: 0**) amount of money the player receives for being killed by an ally. It can be negative.
  * `by_friendly_hit` (**default: 0**) amount of money the player receives for being hit by an ally. It can be negative.
  * `suicide` (**default: 0**) amount of money the player receives for killing himself. It can be negative.

***

* `buttons` is a list of clickable blocks on the map. By clicking on these blocks the player can modify his items. Each button supports two types of the click, called "buy" and "sell" (you don't have to actually sell anything with "sell" type, it's just a name). Additionally a button can be locked, so the player has to unlock it with money before using it. You can specify the cost of each action in `buy_cost`, `sell_cost` and `unlock_cost` options. Negative values will give the money to the player, so you can use that for selling items.

    Each button specifies a location from the arena in the `blocks` setting. It can have multiple locations if you want to have more of these buttons.

    Each of the `on_buy`, `on_sell` and `on_unlock` categories holds so called _applier_, which is a set of rules on how to apply an item set. The `item_set` option is the name of the item set, as defined in _sets.yml_ file. The `add_type` specifies how exactly that item set will be added. Possible values are:

    * `increase` - simplest possible way, increases the amount of the items. If an item has a maximum value, it won't increase over that value.
    * `decrease` - removes the items. If an item has a minimum value, it won't decrease below that value.
    * `fill` - increases the amount of items to the amount specified in this applier, but not higher. This is useful for ammunition refills etc.

    Next is the `amount` which controls how many item sets will be applied. It defaults to 1, but you can set it higher.

    For example, if the item set has 3 rockets, you `fill` it to the amount of 2 item sets and the player has already 5 rockets, this will add him 1 rocket, to the amount of 6 rockets. That's because `(2 * 3) - 5 = 6 - 5 = 1`.

    Another example, if the player has 1 rocket, the item set has 2 rockets and you `increase` it by the amount of 3 item sets, the player will receive 6 rockets, to the amount of 7. That's because `1 + (2 * 3) = 1 + 6 = 7`.

    Next is the `conflict_action`. It specifies what happened if you try to add a different item set in the same category.

    Imagine that you have two different rocket types - a fast, light one and a strong, slow missile. You set a limit of 6 for the light rockets and 2 for the heavy ones, and give them the same category, let's call it `rockets`. The player can either buy heavy rockets or light rockets, but not both at the same time - they both go to the same category, and you can have only one item set in the category. Now if the player has bought light rockets already and he clicks on the heavy rockets, these things can happen:

    * `skip` - nothing will happen, the old item stays (light rockets in this example,)
    * `replace` - the old item is erased and replaced with the new one (light rockets are gone and heavy rockets take their place.)

    (In case of the example above you would probably go with `skip` and allow the player to sell his rockets first, so he can switch without loosing money.)

    The last setting is `saving`. It can be either `true` or `false`. If it's true, the item set will be placed in the stored items (and then in the current ones). It it's false, it will be placed only in the current stage.

    You don't have to worry about this setting if you're using `nothing` respawn action, the stored stage isn't used in that case.

## Game types

Each game type adds additional settings to configure game-specific rules, for example spawn points and teams.

### Team DeathMatch

**`teamDeathMatch`**

This game divides players into teams. Each team's objective is to kill players from other teams. Every team has a separate spawn location, color and name.

If the game is continuous, points will be given for each kill. Otherwise the last team alive scores a point.

```
game_name:
  type: teamDeathMatch
  [default game settings]
  suicide_score: [integer]
  friendly_kill_score: [integer]
  enemy_kill_score: [integer]
  points_to_win: [positive integer]
  equal_teams: [true/false]
  teams:
    team_id:
      name: [translatable text]
      color: [color]
      spawns: [locations]
    another_team_id:
      [...]
```

* `suicide_score` (**default: 0**) points given to a team if one of its members commits a suicide.
* `friendly_kill_score` (**default: 0**) points given to a team if one of its members kills an ally.
* `enemy_kill_score` (**default: 1**) points given to a team if one of its members kills an enemy. These three settings matter only in continuous games.
* `points_to_win` (**default: 0**) points required for the team to win the game. 0 means infinite game.
* `equal_teams` (**default: false**) if this setting is set to true, the game will respawn players in such way that the team sizes in-game are always equal. The players who exceed the equality check will have to wait until the next respawn batch. When choosing, the game will prioritize the players who have already waited.
* `teams` list of teams in the game.
  * `name` (**required**) the name of the team.
  * `color` (**required**) the color of the team (from [this list](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html)).
  * `spawns` (**required**) the list of spawn locations of the team as defined in _arenas.yml_.

### DeathMatch

**`deathMatch`**

In this game everyone fight with everyone. There are no friendly players, only you. Spawn locations and colors will be assigned randomly. Wins the player who has the most kills.

If the game is continuous, points will be given for each kill. Otherwise only the last player alive in the round will receive points.

```
game_name:
  type: deathMatch
  [default game settings]
  suicide_score: [integer]
  kill_score: [integer]
  points_to_win: [positive integer]
  spawns: [locations]
  colors:
  - [colors]
```

* `suicide_score` (**default: 0**) points given to a player if he commits a suicide.
* `kill_score` (**default: 1**) points given to a player if he kills someone. These two settings matter only in continuous games.
* `points_to_win` (**default: 0**) points required for the player to win the game. 0 means infinite game.
* `spawns` (**required**) list of spawn names (as defined in _arenas.yml_ file.)
* `colors` (**optional**) list of [colors](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/ChatColor.html) used in the game. Leave it empty to use the whole palette.
