# Commmands

Flier uses only one command, `/flier`. If you want to see available subcommands type it without any additional arguments. Only subcommands to which you have access will be displayed, i.e. it won't display `join` argument if you have used this from the console.

The aliases for `/flier` are `/f` and `/fl`. The permission to use this command is `flier.command` and it's default for all players.

When you see something in a different font in a command, it's meant to be replaced by a single word without spaces.

## Subcommands

| Command | Aliases | Permission | Users | Description |
|---------------|--------|---------------|--------|---------------------------|
| _/flier join `lobby`_ | `j` | `flier.player.join` | players | Moves you to a Lobby with specified name. |
| _/flier leave_ | `l` | `flier.player.leave` | players | Moves you out of the current Lobby. |
| _/flier setgame `lobby` `game`_ | `s` | `flier.admin.setgame` | ops | Sets the current Game in specified Lobby. |
| _/flier money `player` `amount`_ | `m` | `flier.admin.setmoney` | ops | Sets the current money of a specified player. |
| _/flier reload_ | none | `flier.admin.reload` | ops | Reloads the configuration files. |

The development subcommands like `save` or `load` are not listed here.
