# Bonus

Bonuses are objects on the game map which can be collected by players in order to run some actions. Each bonus specifies stuff like cooldown before collecting it again, respawn time and list of actions. Different bonus types specify how the bonus is represented in the world, for example as an entity (like a sheep).

Note that if the bonus is collected by being in its radius, `cooldown` is 0 and the bonus is not consumable, all actions will run every tick (20 times per second) while the player is near that bonus. You can use it to create healing or refueling bonuses.

```
some_bonus:
  type: [bonus type]
  consumable: [boolean]
  cooldown: [non-negative integer]
  respawn: [non-negative integer]
  actions:
  - some_action
  - other_action
  [type specific settings]
```

## Bonus settings

* `consumable` (**required**) whenever the bonus will disappear after collecting
* `cooldown` (**required**) is the amount of ticks before the player can collect the bonus again.
* `respawn` (**required**) is the amount of ticks before the bonus is respawned after being collected.

## Bonus types

### Invisible

**`invisible`**

This bonus type is invisible and can be collected by being at specified location.

```
invisible_bonus:
  type: invisible
  [default bonus settings]
  location: [location]
  distance: [positive decimal]
```

* `location` (**required**) is the location at which the Bonus is.
* `distance` (**required**) is the minimum distance from the Bonus where the player will collect it.

### Entity

**`entity`**

This bonus type is represented by a rotating, floating Entity (like a sheep). It's also collected by being at specified location.

```
entity_bonus:
  type: entity
  [default bonus settings]
  entity: [entity type]
  location: [location]
  distance: [positive decimal]
```

* `entity` (**required**) is the [type of the Entity](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html).
* `location` (**required**) is the location at which the Bonus is.
* `distance` (**required**) is the minimum distance from the Bonus where the player will collect it.
