# Lobby

Lobby is the most basic object in Flier. It contains games (one or more), manages players, their classes and item sets. Each lobby is defined in _lobbies.yml_ file. Every lobby has properties like type, a list of chooseable item sets, default class, respawn action and games.

Type is simply a type of the lobby. It's responsible for how the lobby looks (for example `physicalLobby` is made of blocks, like a room) and what additional settings are available (for example a room has to have spawn location). Available types are listed below.

Item sets (described in _Item Set_ chapter) are responsible for what items are available to players. Item sets in lobbies have additional `buy_cost` and `unlock_cost` values which control how much money is needed to buy and unlock the item set. Both default to 0.

Default class is a list of item sets defined in this lobby which will be given to the player when he joins the lobby. It is also what the player will have if he uses an item set with `reset` type (more info in _Item Set_ chapter).

Respawn action specifies what happens with players' items after they respawns in the lobby. Available values are:

* `reset`
* `load`
* `save`

To explain these you need to understand that each class (set of items) consists of three "stages": default, stored and current. Default stage is immutable - it's created when the player joins the lobby and it can't be changed. It will be loaded after respawn if the `reset` action is used. Stored stage can be changed, but these changes are not reflected immediately in player's items. "Current" stage is simply what's inside player's inventory. `load` action copies all items from "stored" to "current" stage while `save` action saves "current" into "stored" stage.

Games is the list of games available in this lobby. The first game in this list will be started when a player joins the lobby. You can change active game with _flier setgame {lobby} {game}_ command.

```
lobby_name:
  type: [lobby type]
  items:
    item_set_name:
      [item set settings]
      buy_cost: [integer]
      unlock_cost: [integer]
  default_class:
  - item_set_name
  respawn_action: [action]
  games:
  - game_name
  - other_game
```

# Lobby types

## Physical lobby

**`physicalLobby`**

This is the lobby represented by actual structure made of blocks, like a special room. Players control joining, choosing items and starting the game by clicking on specified blocks. There is a list of `join` blocks which can be set across your world to allow players join this lobby, `spawn` location inside the lobby where all players will spawn and respawn, `leave` block to leave the lobby, `start` block to start the game and a list of blocks with item sets.

The `blocks` list defines blocks which give item sets upon being clicked. Each block has `item` option which specifies the ID of the item set (as defined in `items` section of the Lobby) and `block` option which specifies a location of this block.

```
lobby_name:
  type: physicalLobby
  blocks:
    block_name:
      item: item_set_name
      block: [location]
  [lobby settings]
  spawn: [location]
  join:
  - [block location]
  - [another block location]
  start: [block location]
  leave: [block location]
```
