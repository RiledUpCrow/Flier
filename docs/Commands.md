# Commmands

Flier uses only one command, `/flier`. If you want to see available subcommands type it without any additional arguments. Only subcommands to which you have access will be displayed, i.e. it won't display `join` argument if you have used this from the console.

The aliases for `/flier` are `/f` and `/fl`. The permission to use this command is `flier.command` and it's default for all players.

When you see something in a different font in a command, it's meant to be replaced by a single word without spaces. If that word is in square brackets, it's optional.

## Subcommands

| Command | Aliases | Permission | Users | Description |
|---------------|--------|---------------|--------|---------------------------|
| _/flier lobby join `lobby` `[player]`_ | `l j` | `flier.player.join` / `flier.admin.join` | players/ops | Moves you to a lobby with specified name or forces another player to move to the lobby. |
| _/flier lobby leave `[player]`_ | `l l` | `flier.player.leave` / `flier.admin.leave` | players/ops | Moves you out of the current lobby or forces another player out of his lobby. |
| _/flier lobby item `sell/buy` `item` `[player]`_ | `l i` | `flier.player.item` / `flier.admin.item` | players/ops | Buys/sells you specified items (as defined in _lobbies.yml_, `buttons` section) or forces another player to buy/sell these items. It can fail if the player doesn't have enough money. |
| _/flier lobby start `[player]`_ | `l s` | `flier.player.start` / `flier.admin.start` | players/ops | Moves you into the current game or forces another player to move into the game. |
| _/flier setgame `lobby` `game`_ | `s` | `flier.admin.setgame` | ops | Sets the current Game in specified Lobby. |
| _/flier money `player` `amount`_ | `m` | `flier.admin.setmoney` | ops | Sets the current money of a specified player. |
| _/flier reload_ | none | `flier.admin.reload` | ops | Reloads the configuration files. |

The development subcommands like `save` or `load` are not listed here.
