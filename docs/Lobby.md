# Lobby

Lobby is the most basic object in Flier. It contains games (one or more), manages players and arenas. Each lobby is defined in _lobbies.yml_ file.

There's quite a lot of talk about arenas here, they are described in detail in the next chapter. All you need to know now is that the arena is like a map for the game. When the lobby creates a new game for players it will choose a free arena from a list and assign the game to that arena. Games specify which arenas they can be assigned to, so the lobby can match them correctly. If there are no more arenas, the lobby won't create new games.

```
lobby_name:
  type: [lobby type]
  name: [translatable text]
  spawn: [location]
  autojoin: [game ID]
  max_games: [non-negative integer]
  games:
  - [list of game IDs]
  arenas:
  - [list of arena IDs]
```

## Lobby settings

* `type` (**required**) is simply a type of the lobby. It's responsible for how the lobby looks (for example `physicalLobby` is made of blocks, like a room) and what additional settings are available (for example a room has to have spawn location). Available types are listed below.
* `name` (**default: `id`**) the display name of the lobby.
* `spawn` (**required**) the spawn location for players joining the lobby. They will also be teleported here when their game ends.
* `autojoin` (**optional**) when the ID of a game is specified here, players will join this game automatically upon entering the lobby.
* `max_games` (**default: 0**) if you want to have many arenas but your server can't run that many games you can limit the amount of running games with this setting. It will refuse to create new games above the limit even if there are some free arenas.
* `games` is the list of games available in this lobby. The first game in this list will be started when a player joins the lobby. You can change active game with _flier setgame {lobby} {game}_ command.
* `arenas` is the list of arenas to which this lobby can assign games. When a player tries to join a game but there is no more room in existing games, the lobby will create a new game of chosen type in the first free arena on which this game can be played (see `viable_arenas` in game settings).

## Lobby types

### Physical lobby

**`physicalLobby`**

This is the lobby represented by an actual structure made of blocks, like a special room. Players control joining, choosing games and leaving by clicking on specified blocks. There is a list of `join` blocks which can be set across your world to allow players join this lobby, `leave` block to leave the lobby, `start` block to start the game and a list of blocks with item sets.

```
lobby_name:
  type: physicalLobby
  [lobby settings]
  join:
  - [block locations]
  start: [block location]
  leave: [block location]
```

Each of these locations must follow the Unified Location Format:
`x;y;z;world`, where the first three variables are coordinates of the block, and the fourth is the name of the world, for example:

`100.5;200;-300;concrete`
