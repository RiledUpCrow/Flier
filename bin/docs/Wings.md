# Wings

Wings allow players to fly. Every wings are an Elytra item worn on the chestplate slot of the inventory. Since it's an item, the `material` option must be specified, but it will be always overridden to `elytra` type. All wings are defined in _wings.yml_ file. Every wing has a `type` that defines how it behaves and what additional settings are available. The types are listed below.

```
wings_name:
  type: [wings type]
  [item specific settings]
  max_health: [positive decimal]
  regeneration: [non-negative decimal]
  [type specific settings]
```

## Wings settings

* `max_health` (**required**) is the health of wings.
* `regeneration` (**required**) is the amount of health regenerated every tick.

## Wing types

Each wing type has a different flight model.

### Simple wings

**`simpleWings`**

These wings have two properties: `aerodynamics` and `liftingforce`. They default to 0 and behave like vanilla Elytra. There's also `max_lift`, which is a limit of lifting force - useful if you want to counter player's weight without making the wings overpowered.

By modifying aerodynamics you modify the air resistance force, which slows you (or accelerates, you can have it positive if you want) the faster you fly. Note that it's added on top of Minecraft's own air resistance.

By modifying lifting force you make your wings better at flying. The faster you fly, the less altitude you will loose due to gravity. This force is countered by weight which pulls you down.

```
wings_name:
  type: simpleWings
  [item specific settings]
  [default wings settings]
  aerodynamics: [decimal]
  liftingforce: [decimal]
  max_lift: [decimal]
```
