# Game

Game is the most important object in Flier. It contains all rules like teams, game goal, what happens after death etc. Games are contained in lobbies. Every game has a type - available types are listed and described below. You define your games in _games.yml_ file.

Every game is run on an "arena" - a set of locations required by the game, like spawns and bonuses. If you define more than one arena, you can run the same game many times simultaneously - the lobby will take the first available arena and create new game there if needed.

```
game_name:
  type: [game type]
  bonuses:
  - some_bonus
  - other_bonus
  effects:
  - some_effect
  - other_effect
  height_limit: [positive integer]
  height_damage: [decimal]
  max_players: [non-negative integer]
  min_players: [positive integer]
  respawn_delay: [non-negative integer]
  start_delay: [non-negative integer]
  locking: [boolean]
  waiting_room: [location]
  center: [location]
  radius: [integer]
  viable_arenas:
  - [arena name]
  respawn_action: [action]
  default_class:
  - item_set_name
  money:
    enabled: false
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
  [type specific settings]
```

## Game settings

These settings apply to every game. Some of them have default values, so if you don't specify them explicitly these will be used. Some values however are required.

* `type` (**required**) type of the game. The value used here will determine what additional settings are available. Types are listed below.
* `bonuses` list of bonuses available in the game. You don't have to specify this if you don't want any bonuses.
* `effects` list of effects available in this game. You don't have to specify this if you don't want any effects.
* `height_limit` (**default: 0**) a height at which players will receive suffocation damage (kind of simulating low air pressure). If it's 0 or lower, it doesn't apply.
* `height_damage` (**default: 1**) the amount of suffocation damage per second. It can use fractions.
* `max_players` (**default: 0**) the maximum amount of players in this game. If it's reached the lobby will try to create another game.
* `min_players` (**default: 1**) the minimum amount of players required for this game to start. The players will sit in the waiting room until there are enough of them to start the game.
* `respawn_delay` (**default: 0**) the time which needs to pass until the players are respawned after death. The counter starts when the first player dies and at the end all waiting players will be spawned in one batch.
* `start_delay` (**default: 0**) the time which needs to pass before the game starts when all required players (`min_players`) are in the waiting room.
* `locking` (**default: false**) whenever the game will be locked after starting - new players can't join the locked game.
* `waiting_room` (**required**) the location of the waiting room, as defined in _arenas.yml_ file.
* `center` (**required**) the center location of the game arena, as defined in _arenas.yml_ file.
* `radius` (**required**) the radius around the center location players are allowed to fly.
* `viable_arenas` list of arenas on which this game can be played. New games will be created in the first free arena from this list.

***

* `respawn_action` specifies what happens with players' items after they respawns in the lobby.

    In order to understand it you need to know that each player class consists of two "stages": stored and current. Current stage reflects what's on the player. If he consumes an item, it will disappear from the current stage. Stored stage holds items which can be given to the player after respawn.

    Knowing this, the available actions are:

    * `clear` - remove all current items and give all stored ones,
    * `combine` - add all stored items to the current ones without removing anything,
    * `nothing` - don't modify anything.

    By default, stored stage contains the default class and all Item Sets are given in the current stage. If you want to add an item set to the stored stage, you need to set `saving` to `true` in the button settings which are described below.

***

* `default_class` is the list of item sets which will be applied to the player when he joins the lobby. All these sets are applied as `fill`, `replace`, saving and single (amount is 1). The sets specified here must be defined in the _items_ section (the one described above).

***

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

***

* `buttons` is a list of clickable, item set giving controls (for example a button in `physicalLobby` is a block in the room). Each button support two types of the click, called "buy" and "sell" (you don't have to actually sell anything with "sell" type, it's just a name). Additionally, a button can be locked before using it, so the player has to unlock it with money. You can specify the cost of each action in `buy_cost`, `sell_cost` and `unlock_cost` options. Negative values will give the money to the player, so you can use that for selling items.

    Each of the `on_buy`, `on_sell` and `on_unlock` categories holds so called _applier_, which is a set of rules on how to apply an item set. The `item_set` option is the name of the item set, as defined in _sets.yml_ file. The `add_type` specifies how exactly that item set will be added. Possible values are:

    * `increase` - simplest possible way, increases the amount of the items. If an item has a maximum value, it won't increase over that value.
    * `decrease` - removes the items. If an item has a minimum value, it won't decrease below that value.
    * `fill` - increases the amount of items to the amount specified in this applier, but not higher. This is useful for ammunition refills etc.

    Next is the `amount` which controls how many item sets will be applied. It defaults to 1, but you can set it higher.

    For example, if the item set has 3 rockets, you `fill` it to amount 2 and the player has already 5 rockets, this will add him 1 rocket, to the amount of 6. Because `(2 * 3) - 5 = 6 - 5 = 1`.

    Another example, if the player has 1 rocket, the item set has 2 rockets and you `increase` it to amount of 3, the player will receive 6 rockets, to the amount of 7. Because `1 + (2 * 3) = 1 + 6 = 7`.

    Next is the `conflict_action`. It specified what happened if you try to add a different item set in the same category.

    Imagine you have two different rocket types - fast, light one and a strong, slow missile. You set a limit of 6 for the light rockets and 2 for the heavy ones, and give them the same category, let's call it `rockets`. The player can either buy heavy rockets or light rockets, but not both at the same time - they both go to the same category, and you can have only one item set in the category. Now if the player has bought light rockets already and he clicks on the heavy rockets, these things can happen:

    * `skip` - nothing will happen, the old item stays,
    * `replace` - the old item is erased and replaced with the new one.

    (In case of that example you would probably go with `skip` and allow the player to sell his rockets, so he can switch without loosing money.)

    The last setting is `saving`. It can be either `true` or `false`. If it's true, the item set will be placed in the stored items (and then in the current ones). It it's false, it will be placed only in the current stage.

    You don't have to worry about this setting if you're using `nothing` respawn action, the stored stage isn't used in that case.

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
  * `spawn` (**required**) the spawn location of the team as defined in _arenas.yml_.
