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

# Action types

## Effect

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

## Fuel

**`fuel`**

This action modifies fuel amount of the player's Engine.

```
fuel_action:
  type: fuel
  amount: [decimal]
```

* `amount` (**required**) the amount of fuel to add (negative values will remove fuel).

## Launch

**`launch`**

This action launches you in the direction you're looking. If you're standing on the ground, it will first throw you a few meters up and enable flying.

```
launch_action:
  type: launch
  speed: [positive decimal]
```

* `speed` (**required**) is the speed with which you will be launched.

## Money

**`money`**

This action gives (or removes) money from the player.

```
money_action:
  type: money
  money: [integer]
```

* `money` (**required**) is the amount of money to add. Negative values will remove money.

## Target compass

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

## Wings Health

**`wingsHealth`***

This action modifies the health of Wings. It will break the Wings if their health drops to 0 and restore them if their health goes above 0.

```
wings_health_action:
  type: wingsHealth
  amount: [decimal]
```

* `amount` (**required**) is the amount of health added to the Wings. Negative values remove health.

## Suicide

**`suicide`**

This action simply kills the player. It works only when the player is not falling due to game mechanics.

```
self_destruction:
  type: suicide
```

## Homing missile

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

## Machine Gun

**`machineGun`**

This action is an attack which shoots a burst of straight-flying projectiles in the direction the player is looking.

```
gun_action:
  type: machineGun
  [attack specific settings]
  entity: [projectile type]
  burst_amount: [positive integer]
  burst_ticks: [positive integer]
  projectile_speed: [positive decimal]
```

* `entity` (**required**) is the [entity](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html) (must be a projectile, can't use a sheep) used by the attack.
* `burst_amount` (**required**) is the amount of projectiles in a burst.
* `burst_ticks` (**required**) is the amount of ticks between shooting projectiles.
* `projectile_speed` (**required**) is the speed of the projectiles.

## Bomb

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
