# Effects

Effects are non-game-changing actions which can happen during the game. Non-game-changing means they don't actually alter you as a player - effects don't restore wings, don't drain fuel and don't attack other players. Instead they can play a sound, display a title or launch a firework. Effects are assigned to specific events - this way you can control when they fire. There are multiple types of both events and effects and you can combine them with only one rule: non-player event can't fire a player effect (example of non-player event is when the game starts and example of player effect is sending a title to a single player).

Effects are defined in _effects.yml_ file and you attach them to games in the `games` section of _games.yml_ file. Each effect has an effect type (available types are described below) and an event type (also below). You can also add so called "matchers", which can further narrow down which event exactly should fire an effect (for example you want to play a sound only if player shoots a weapon - matcher would match `use` events for "weapon" `usage`).

```
effect_name:
  type: [effect type]
  event_type: [event type]
  matchers:
    name: [value]
    other_name: [value]
  [effect specific settings]
```

To match an effect to a specific event you need to define matchers. Each matcher has a key and a match. The key is the name of a value in the event (they are described in _Event types_ section below). The match defines what values are accepted. There are 5 types of matches:

* text will match if the value is the same as specified text
* list of texts will match if the value is on this list
* number will match if the value is an equal number
* range of numbers will match if the value is a number in that range
* boolean will match if the value is the same

Here's an example of each type:

```
matchers:
  text: something
  list:
  - first
  - second
  number: 10
  range: "<(10), >(20)"
  boolean: false
```

All are self-explanatory except for range. It has a special syntax (`>(10)`), where the first character is "less than" or "greater than" and next is a number in parentheses. It will match all numbers smaller or greater than specified number. You can combine these two types with a coma, just like in the example, or specify a single bound.

## Effect settings

* `type` (**required**) is the type of the effect
* `event_type` (**required**) is the type of the event which will fire this effect
* `matchers` is a list of matchers which the event must match in order to fire the effect

## Effect types

### Sound effect

All sound effects play a sound, but to a different targets. These settings are available in all of them:

* `sound` (**required**) is the type of [the sound](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html).
* `volume` (**default: 1**) is the volume of the sound.
* `pitch` (**default: 1**) is the pitch of the sound.

```
effect_name
  type: [type]
  [event settings]
  sound: [sound type]
  volume: [positive decimal ]
  pitch: [positive decimal]
```

#### Types:

**`privateSound`** (player effect)

This effect will play a sound only to this player.

**`publicSound`** (player effect)

This effect will play a sound at this player's location to everyone else who can hear it.

**`gameSound`** (player effect)

This effect will play a sound to every player in the Game at their locations.

### Particle effect

**`particle`**

This effect spawns a particle at player's location. Particles in Minecraft are generally weird to handle.

* `particle` (**required**) is the type of [the particle](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html).
* `amount` (**default: 0**) is the amount of particles. Set it to 0 to enable alternative particle mode. High values won't make the server lag (the client on the other side is a different story).
* `offset` (**default: 0**) is distance from the player in which the particles will spawn randomly. If it's 0 the particles will appear exactly at the player's location. It may have a different meaning in alternative mode.
* `offset_x`, `offset_y` and `offset_z` (**default: 0**) is the override for particular axis offset, useful in the alternative mode, for example for setting colors.
* `speed` (**default: 0**) is the speed of particles. This may have a different meaning in alternative mode.
* `count` (**default: 1**) controls how many times the server will spawn the particle. This differs from `amount` because multiple particles are spawned server-side, which means very high values (like a few hundreds) spawned every tick can lag the server. Use this if you need the alternative `amount` mode but still want to spawn multiple particles.
* `manual_offset` (**default: 0**) the same as offset, but used in server-side spawning described above.
* `manual_offset_x`, `manual_offset_y` and `manual_offset_z` (**default: 0**) is the override for particular axis offset in server-side spawning, this time with no special meaning.

```
effect_name:
  type: particle
  [event settings]
  particle: [particle type]
  amount: [non-negative integer]
  offset: [non-negative decimal]
  speed: [non-negative decimal]
  count: [non-negative integer]
  manual_offset: [non-negative decimal]
```

### Glow effect

**`glow`**

This effect makes the player glow for a specified time.

```
shiny:
  type: glow
  [event settings]
  time: [positive integer]
```

* `time` (**required**) is the glowing time in ticks.

## Event types

Every player event has available these matchers:

* `class` _(text)_ the name of the player's class.
* `color` _(text)_ the color used by the player (for example team color).
* `money` _(number)_ the amount of money the player has
* `engine` _(text)_ the ID of the player's engine
* `fuel` _(number)_ the amount of fuel in the engine
* `fuel_ratio` _(number)_ the ratio fuel/max as a number between 0 and 1
* `wings` _(text)_ the ID of the player's wings
* `wings_health` _(number)_ the amount of health of wings
* `wings_health_ratio` _(number)_ the ratio wings_health/max as a number between 0 and 1

If the event involves another player (like `hit` event - the shooter is main player, the victim is another player), these matchers are also available for them, with a prefix specified by the event type. There's also a special matcher:

* `attitude` _(text)_ the attitude between the two players. Available values are: `friendly`, `neutral` and `hostile`.

Non-player events don't have any additional matchers.

### Use event

**`use`** (player event)

This event fires whenever some player uses an item with a usage.

* `item` _(text)_ the ID of the item used in this event
* `ammo` _(number)_ the amount of ammunition _before_ using this item
* `amount` _(number)_ the amount of these items the player has
* `usage` _(text)_ the ID of the usage used in this event

### Hit event

**`hit`** (two players event, second player has `target_` prefix)

This event fires when one player hits another one with a weapon. Effects will be fired for the shooter. Matchers for the player who got hit are prefixed with `target_`, for example `target_class`.

* `hit` _(true/false)_ whenever the hit actually happened (hits when falling don't happen)
* `wings_off` _(true/false)_ whenever the hit causes the wings to fall of
* `wings_damage` _(true/false)_ whenever the hit causes any wings damage
* `regular_damage` _(true/false)_ whenever the hit causes any physical damage
* `damage_to_wings` _(number)_ the amount of damage the weapon deals to the wings (not necessarily dealt)
* `damage_to_health` _(number)_ the amount of damage the weapon deals to the player (not necessarily dealt)

### Get hit event

**`get hit`** (two players event, second player has `shooter_` prefix)

This event fires when one player gets hit by another one with a weapon. Effects will be fired for the victim. Matchers for the player who fired the weapon are prefixed with `shooter_`, for example `shooter_money`.

All other matchers like `wings_off` and `damage_to_health` are exactly the same as in `hit` event described above.

### Kill event

**`kill`** (two players event, second player has `killed_` prefix)

This event fires when the main player kills the other one. Effects will be fired for the killer. Matches for the player who was killed are prefixed with `killed_`, for example `killed_engine`. The killer and killed are the same player in case of a suicide.

* `suicide` _(true/false)_ whenever this event was fired for a suicide
* `shot_down` _(true/false)_ whenever the player died because of falling from the sky
* `killed` _(true/false)_ whenever the player was killed on the ground (not by falling)

### Killed event

**`killed`** (two players event, second player has `killer_` prefix)

This event fires when the main player is killed by the other one. Effects will be fired for the killed. Matches for the player who killed are prefixed with `killer_`, for example `killer_wings`. The killer and killed are the same player in case of a suicide.

All other matches like  `suicide` and `shot_down` are exactly the same as in `kill` event described above.

### Engine use event

**`engine`** (player event)

This event fires every tick while the player is using the engine. It has no special matchers.

### Spawn event

**`spawn`** (player event)

This event fires when the player spawns in the game. It has no special matchers.

### Bonus collect event

**`bonus`** (player event)

This event fires when the player collects a bonus.

* `bonus` _(text)_ the ID of the bonus, as defined in _bonuses.yml_ file.
