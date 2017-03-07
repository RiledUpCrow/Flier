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

# Effect types

## Sound effect

**`sound`** (player effect)

This effect will play a sound to a single player. You can control the volume and pitch.

```
effect_name
  type: sound
  [event settings]
  sound: [sound type]
  volume: [positive decimal ]
  pitch: [positive decimal]
```

* `sound` (**required**) is the type of [the sound](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html).
* `volume` (**default: 1**) is the volume of the sound.
* `pitch` (**default: 1**) is the pitch of the sound.

# Event types

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

If the event involves another player (like `hit` event - the shooter is main player, the victim is another player), these settings are also available for them, with a prefix specified by the event type. Non-player events don't have any additional matchers.

## Use event

**`use`** (player event)

This event fires whenever some player uses an item with a usage.

* `item` _(text)_ the ID of the item used in this event
* `ammo` _(number)_ the amount of ammunition _before_ using this item
* `amount` _(number)_ the amount of these items the player has
* `usage` _(text)_ the ID of the usage used in this event

## Hit event

**`hit`** (two players event, second player has `target_` prefix)

This event fires when one player hits another one with a weapon. Effects will be fired for the shooter. Matchers for the player who got hit are prefixed with `target_`, for example `target_class`.

* `attitude` _(text)_ the attitude between the two players. Available values are: `friendly`, `neutral` and `hostile`.
* `hit` _(true/false)_ whenever the hit actually happened (hits when falling don't happen)
* `wings_off` _(true/false)_ whenever the hit causes the wings to fall of
* `wings_damage` _(true/false)_ whenever the hit causes any wings damage
* `regular_damage` _(true/false)_ whenever the hit causes any physical damage
* `damage_to_wings` _(number)_ the amount of damage the weapon deals to the wings (not necessarily dealt)
* `damage_to_health` _(number)_ the amount of damage the weapon deals to the player (not necessarily dealt)

## Get hit event

**`get hit`** (two players event, second player has `shooter_` prefix)

This event fires when one player gets hit by another one with a weapon. Effects will be fired for the victim. Matchers for the player who fired the weapon are prefixed with `shooter_`, for example `shooter_money`.

All other matchers like `attitude` and `damage_to_health` are exactly the same as in `hit` event described above.
