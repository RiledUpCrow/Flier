# Lobby

Lobby is the most basic object in Flier. It contains games (one or more), manages players, their classes and item sets. Each lobby is defined in _lobbies.yml_ file. Every lobby has properties like type, a list of choosable item sets, default class, respawn action and games.

```
lobby_name:
  type: [lobby type]
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
  default_class:
  - item_set_name
  respawn_action: [action]
  games:
  - game_name
  - other_game
```

## Lobby settings

_This description is large, so the main settings are separated with lines._

***

* `type` is simply a type of the lobby. It's responsible for how the lobby looks (for example `physicalLobby` is made of blocks, like a room) and what additional settings are available (for example a room has to have spawn location). Available types are listed below.

***

* `respawn_action` specifies what happens with players' items after they respawns in the lobby.

    In order to understand it you need to know that each player class consists of two "stages": stored and current. Current stage reflects what's on the player. If he consumes an item, it will disappear from the current stage. Stored stage holds items which can be given to the player after respawn.

    Knowing this, the available actions are:

    * `clear` - remove all current items and give all stored ones,
    * `combine` - add all stored items to the current ones without removing anything,
    * `nothing` - don't modify anything.

    By default, stored stage contains the default class and all Item Sets are given in the current stage. If you want to add an item set to the stored stage, you need to set `saving` to `true` in the button settings which are described below.

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


***

* `default_class` is the list of item sets which will be applied to the player when he joins the lobby. All these sets are applied as `fill`, `replace`, saving and single (amount is 1). The sets specified here must be defined in the _items_ section (the one described above).

***

* `games` is the list of games available in this lobby. The first game in this list will be started when a player joins the lobby. You can change active game with _flier setgame {lobby} {game}_ command.

# Lobby types

## Physical lobby

**`physicalLobby`**

This is the lobby represented by actual structure made of blocks, like a special room. Players control joining, choosing items and starting the game by clicking on specified blocks. There is a list of `join` blocks which can be set across your world to allow players join this lobby, `spawn` location inside the lobby where all players will spawn and respawn, `leave` block to leave the lobby, `start` block to start the game and a list of blocks with item sets.

The `buttons` list gets an additional option, `block`, which specifies where is the block representing this button.

```
lobby_name:
  type: physicalLobby
  buttons:
    button_name:
      [button specific settings]
      block: [location]
  [lobby settings]
  spawn: [location]
  join:
  - [block location]
  - [another block location]
  start: [block location]
  leave: [block location]
```
