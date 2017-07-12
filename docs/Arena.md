# Arena

Arenas in Flier can be viewed as maps on which games are played. Each lobby has a list of arenas it can use, and when it's starting a new game it will choose a currently unused arena for it. The game will then look for locations it needs in the given arena.

Every location specified in the _arenas.yml_ file follows the Unified Location Format: `x;y;z;world;yaw;pitch`, where yaw and pitch values are optional and default to 0. So an example of a valid location in the world "void" would be `54.2;17;-689.41;void` or `54.2;17;-689.41;void;90;0` if you want to include yaw and pitch too. Additionally, every location in the arena can be specified as a single location or as a list of locations:

```
single: 100;200;300;world
multiple:
- 400;500;600;world
- 700;800;900;world
```

When the plugin tries to get a single location and encounters a list, it will take the first location. In case the plugin can use multiple locations and finds only one, it will use only one. For example if you specify multiple locations for the center of the map, only the first one will be used, but if you give only one location for the player spawns, all players will spawn in the same place. So don't worry, both forms are valid in every situation, lists just give you more control sometimes.

***

The arena system is designed in such a way that you can build a single map on which many types of games can be run. Imagine that you have a map with 10 spawns - you can run a deathmatch game on this map, but you can also split the spawns into two teams and run a team game. Arena system will ensure that only one of these games is running on this map at the same time, but players will be able to choose which game to play before it starts.

Let's dive into workings of this system now. Each arena is a set of named locations - places for spawns, bonuses, buttons etc. You can define locations with whatever names you like, and then make your games use locations with those names. The example above could look like this (we're using location lists because game spawns can use more than one location):

```
team_or_deathmatch_arena:
  red_team_spawns:
  - 1;1;1;world
  - 2;2;2;world
  blue_team_spawns:
  - 3;3;3;world
  - 4;4;4;world
  all_spawns:
  - 1;1;1;world
  - 2;2;2;world
  - 3;3;3;world
  - 4;4;4;world
```

Both games (team and deathmatch) can use this arena. Team game will specify `red_team_spawns` location for the red spawn and `blue_team_spawns` for blue spawn, and deathmatch game will use `all_spawns` as a list of spawns for all players. Note that the locations are repeated here - this is correct, since we will use either `red_team_spawns` and `blue_team_spawns`, or `all_spawns`. Never both at the same time, because two games cannot run simultaneously on the same arena.
