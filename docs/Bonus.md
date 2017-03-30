# Bonus

Bonuses are objects on the Game map which can be collected by players in order to run some Actions. Each bonus specifies stuff like location, distance where it can be collected, cooldown before collecting it again, respawn time and list of actions. Different Bonus types specify how the bonus is represented in the world, for example as an Entity (like a sheep).

Note that if the `cooldown` is 0 and the Bonus is not consumable, all Actions will run every tick (20 times per second) while the player is in the Bonus radius. You can use it to create healing or refueling locations.

```
some_bonus:
  type: [bonus type]
  location: [location]
  distance: [positive decimal]
  consumable: [boolean]
  cooldown: [non-negative integer]
  respawn: [non-negative integer]
  actions:
  - some_action
  - other_action
  [type specific settings]
```

## Bonus settings

* `location` (**required**) is the location at which the Bonus is.
* `distance` (**required**) is the minimum distance from the Bonus where the player will collect it.
* `consumable` (**required**) whenever the bonus will disappear after collecting
* `cooldown` (**required**) is the amount of ticks before the player can collect the bonus again.
* `respawn` (**required**) is the amount of ticks before the bonus is respawned after being collected.

## Bonus types

### Entity

**`entity`**

This Bonus type is represented by a rotating, floating Entity (like a sheep).

```
entity_bonus:
  type: entity
  [default bonus settings]
  entity: [entity type]
```

* `entity` (**required**) is the [type of the Entity](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html).
