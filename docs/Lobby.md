# Lobby

Lobby is the most basic object in Flier. It contains games (one or more), manages players and games. Each lobby is defined in _lobbies.yml_ file.

```
lobby_name:
  type: [lobby type]
  autojoin: [game name]
  games:
  - game_name
  - other_game
  arenas:
  - default
```

## Lobby settings

* `type` is simply a type of the lobby. It's responsible for how the lobby looks (for example `physicalLobby` is made of blocks, like a room) and what additional settings are available (for example a room has to have spawn location). Available types are listed below.
* `autojoin` (**optional**) when the name of a game is specified here, players will join this game automatically upon entering the lobby.
* `max_games` (**default: 0**) if you want to have many arenas but your server can't run that many games you can limit amount of running games with this setting. It will refuse to create new games above the limit even if there are some free arenas.
* `games` is the list of games available in this lobby. The first game in this list will be started when a player joins the lobby. You can change active game with _flier setgame {lobby} {game}_ command.
* `arenas` is the list of arenas on which this lobby can assign games. When a player tries to join a game but there is no more room in existing games, the lobby will create a new game of chosen type in the first free arena on which this game can be played (see `viable_arenas` in game settings).

## Lobby types

### Physical lobby

**`physicalLobby`**

This is the lobby represented by actual structure made of blocks, like a special room. Players control joining, choosing starting the game and leaving by clicking on specified blocks. There is a list of `join` blocks which can be set across your world to allow players join this lobby, `spawn` location inside the lobby where all players will spawn and respawn, `leave` block to leave the lobby, `start` block to start the game and a list of blocks with item sets.

```
lobby_name:
  type: physicalLobby
  [lobby settings]
  spawn: [location]
  join:
  - [block location]
  - [another block location]
  start: [block location]
  leave: [block location]
```
