# Engine

Engines allow players to speed up while flying. Each engine is an item held in the offhand, thus many item specific settings like `material` are needed to define it. Each engine is defined in _engines.yml_ file. Every engine has a `type` that defines how it behaves and what additional settings are available. The types are listed below.

```
engine_name:
  type: [engine type]
  [item specific settings]
  max_fuel: [positive decimal]
  consumption: [non-negative decimal]
  regeneration: [non-negative decimal]
  [type specific settings]
```

## Engine settings

* `max_fuel` (**required**) is the amount of fuel the engine has.
* `consumption` (**required**) is the amount of fuel consumed every tick while accelerating (20 times per second). Set it to 0 if you want your fuel to be unlimited.
* `regeneration` (**required**) is the amount of fuel regenerated every tick while _not_ accelerating. Set it to 0 if you want to restore fuel some other way than via regeneration.

## Engine types

Each engine type has a different acceleration model.

### Multiplying engine

**`multiplyingEngine`**

This engine multiplies your current speed by `acceleration`. This means that the faster you fly the faster you will accelerate, until you reach `max_speed`. If you fly slower than `min_speed` you'll accelerate as if you were flying at that speed. With these three settings you can create powerful engines with low speed or weak engines capable of getting really fast after a long accelerating, whatever you need.

The speed displayed on the sidebar is multiplied by 10, so the players are not confused with small numbers (speed of 3 is actually very fast). Acceleration is applied 20 times per second, so it needs to be a very small value (like 0.025) unless you want instant max speed.

```
engine_name:
  type: multiplyingEngine
  [item specific settings]
  [default engine settings]
  acceleration: [positive decimal]
  min_speed: [non-negative decimal]
  max_speed: [positive decimal]
```
