# Action

Action is something that happens in a game. It can be simple like restoring fuel in the Engine or more complex like shooting a homing missile. Actions are defined in _actions.yml_ file. Each one has a `type` which specifies what it does and additional options which specify how it does that. All available types are listed below.

```
some_action:
  type: [action type]
  [type specific settings]
```

## Attack

Actions which can damage another player share the same set of settings responsible for specifying how that damage is done. An example would be machine gun and homing missile: they both have options like `damage`.

```
some_attack_action
  type: [attack action type]
  damage: [non-negative decimal]
  physical_damage: [non-negative decimal]
  wings_off: [boolean]
  midair_physical_damage: [boolean]
  suicidal: [boolean]
  friendly_fire: [boolean]
  exploding: [boolean]
```

* `damage` (**required**) is amount of damage dealt to Wings' health. It will only happen if the player is flying.
* `physical_damage` (**required**) is amount of damage dealt to player's health. It will only happen on the ground or if `midair_physical_damage` is set to `true`.
* `wings_off` (**default: false**) whenever Wings will be taken off on hit. It prevents further Wings damage but makes the player fall. They can quickly put them on to keep flying.
* `midair_physical_damage` (**default: false**) whenever the physical damage will be dealt to flying targets too.
* `suicidal` (**default: false**) whenever you can hit yourself with this weapon.
* `friendly_fire` (**default: true**) whenever you can hit friendly players with this weapon.
* `exploding` (**default: false**) in case the projectiles of this weapon are explosive (like ghast fireballs), it controls whenever they will explode.

## Action types

### Effect

**`effect`**

This action runs other Actions 20 times a second for a `duration` measured in ticks. For example if you want to increase Wings regeneration by 1 for a minute, you would run `wingsHealth` action with `1` amount for `duration` 1200 (20 ticks/second times 60 seconds).

```
effect_action:
  type: effect
  actions:
  - some_action
  duration: [positive integer]
```

* `actions` is a list of Action that will be applied on every tick.
* `duration` (**required**) is amount of ticks for which these Actions will be run.

### Fuel

**`fuel`**

This action modifies fuel amount of the player's Engine.

```
fuel_action:
  type: fuel
  amount: [decimal]
```

* `amount` (**required**) the amount of fuel to add (negative values will remove fuel).

### Launch

**`launch`**

This action launches you in the direction you're looking. If you're standing on the ground, it will first throw you a few meters up and enable flying.

```
launch_action:
  type: launch
  speed: [positive decimal]
```

* `speed` (**required**) is the speed with which you will be launched.

### Sprint starting

**`sprintStarting`**

This action allows the player to take-off when sprinting on a runway. It needs to be constantly used/activated in order to work (for example when having no activators in an usage). The player needs to be sprinting in a straight line on a flat surface.

The player's sprinting speed will increase during `time` seconds up to the `max` speed, at which the player will be slightly thrown into the air. Gliding will be enabled and the engine will be turned on (as if the player was sneaking). To turn the engine off you need to press sneaking key once.

```
takeoff:
  type: sprintStarting
  max: [0-1 decimal]
  time: [non-negative decimal]
```

* `max` (**default: 0.8**) the speed at which the player will take-off.
* `time` (**default: 5**) the time in seconds it takes to reach `max` speed when sprinting.

### Money

**`money`**

This action gives (or removes) money from the player.

```
money_action:
  type: money
  money: [integer]
```

* `money` (**required**) is the amount of money to add. Negative values will remove money.

### Target compass

**`targetCompass`**

This action points player's compass at the closest target of specified type. It's best to run this Action repeatedly, since targets (players) are moving.

```
target_action:
  type: targetCompass
  target: [target type]
```

* `target` (**defult: hostile**) is the type of the target. Available types are:
  * `hostile`
  * `friendly`
  * `neutral`

### Wings Health

**`wingsHealth`**

This action modifies the health of Wings. It will break the Wings if their health drops to 0 and restore them if their health goes above 0.

```
wings_health_action:
  type: wingsHealth
  amount: [decimal]
```

* `amount` (**required**) is the amount of health added to the Wings. Negative values remove health.

### Health

**`health`**

This action modifies the player's health. If the amount is negative it will damage the player (won't change cause of death though, so other players can still get points/money for the kill) and if it's positive, it will heal them.

```
health_action:
  type: health
  amount: [decimal]
```

* `amount` (**required**) is the amount of health to modify. Negative values damage the player.

### Suicide

**`suicide`**

This action simply kills the player. It works only when the player is not falling due to game mechanics.

```
self_destruction:
  type: suicide
```

### Item set

**`itemSet`**

This action will apply an item set according to the specified rules. The settings available here are exactly the same as in a lobby button. Refer to the _Lobby_ chapter for more details.

```
apply_set:
  type: itemSet
  item_set: [item set name]
  add_type: [add type]
  amount: [non-negative integer]
  conflict_action: [conflict action]
  saving: [true/false]
```

### Consume

**`consume`**

This action will consume a single item from player's inventory, just like using a consumable item does. It will not update all class items, as opposed to `itemSet` with a single item decreasing.

```
consume_item:
  type: consume
  item: [item name]
```

* `item` is the name of the item, as in _items.yml_ file.

### Homing missile

**`homingMissile`**

This action is an attack which shoots a homing projectile. The missile will fly straight at first, and when it finds a target it will fly towards it. If it looses its target for some reason, it will fly in circles until its lifetime passes or it finds another target.

```
rocket_action:
  type: homingMissile
  [attack specific settings]
  entity: [projectile type]
  search_range: [positive integer]
  search_radius: [positive decimal]
  speed: [positive decimal]
  lifetime: [positive integer]
  maneuverability: [positive decimal]
```

* `entity` (**required**) is the [entity](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html) (must be a projectile, can't use a sheep) used by the attack.
* `search_range` is the range in which the missile looks for targets (technically it's a diameter of a sphere directly before the missile).
* `search_radius` is the maneuverability when searching for a target (after loosing one). The greater this number is, the smaller circles the rocket will do.
* `speed` is the speed of the missile.
* `lifetime` is the amount of ticks the missile will live after launching. If it does not hit anything for this time, it will disappear.
* `maneuverability` is the ability to turn when targeting someone. The greater this number is, the better the rocket is at following its target.

### Projectile Gun

**`projectileGun`**

This action is an attack which shoots a burst of straight-flying projectile-based bullets in the direction the player is looking.

This weapon type is very limited because of how the server handles projectile collisions and there are visual bugs in client with fast projectiles. It's recommended that you use Particle Weapon described below as your main weapon and leave this one for simple pistols and such.

```
gun_action:
  type: projectileGun
  [attack specific settings]
  entity: [projectile type]
  burst_amount: [positive integer]
  burst_ticks: [positive integer]
  projectile_speed: [positive decimal]
```

* `entity` (**required**) is the [entity](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html) (must be a projectile, can't use a sheep) used by the attack.
* `burst_amount` (**required**) is the amount of bullets in a burst.
* `burst_ticks` (**required**) is the amount of ticks between shooting bullets.
* `projectile_speed` (**required**) is the speed of the bullets.

### Particle Gun

**`particleGun`**

This action is an attack which shoots a burst of straight-flying particle-based bullets in the direction the player is looking.

```
gun_action:
  type: particleGun
  [attack specific settings]
  particle: [particle type]
  amount: [non-negative integer]
  offset: [non-negative decimal]
  speed: [non-negative decimal]
  density: [positive decimal]
  burst_amount: [positive integer]
  burst_ticks: [positive integer]
  spread: [non-negative integer]
  projectile_speed: [positive decimal]
  range: [positive decimal]
```

* `particle` (**required**) is the type of [the particle](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html).
* `amount` (**default: 0**) is the amount of particles. Set it to 0 to enable alternative particle mode. High values won't make the server lag (the client on the other side is a different story).
* `offset` (**default: 0**) is distance from the player in which the particles will spawn randomly. If it's 0 the particles will appear exactly at the player's location. It may have a different meaning in alternative mode.
* `offset_x`, `offset_y` and `offset_z` (**default: 0**) is the override for particular axis offset, useful in the alternative mode, for example for setting colors.
* `speed` (**default: 0**) is the speed of particles. This may have a different meaning in alternative mode.
* `density` (**default: 0.5**) is the amount of particles per block of bullet's path. Setting it to high values will lag the server.
* `burst_amount` (**required**) is the amount of bullets in a burst.
* `burst_ticks` (**required**) is the total time of the burst.
* `spread` (**default: 0**) is the precision of the bullets, where 0 means perfect.
* `projectile_speed` (**required**) is the speed of the bullets.
* `range` (**default: 256**) the range of bullets - they will disappear after traveling that distance in blocks.

### Bomb

This action is an attack which creates an exploding TNT. The `exploding` option should be set to `true` or it won't work.

```
bomb_action:
  type: autoDestruction
  [attack specific settings]
  power: [positive decimal]
  fuse: [non-negative integer]
```

* `power` (**required**) is the power of the explosion. It doesn't control damage, only the radius of the explosion.
* `fuse` (**default: 80**) is the amount of ticks before the explosion.
