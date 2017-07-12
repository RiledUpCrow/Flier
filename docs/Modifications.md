# Modifications

Modifications change various properties of player's items - they can increase weapon damage, ammunition capacity, engine speed, rocket maneuverability etc. You can define your modifications in the _modifications.yml_ file. Each modification must target a single type of an object - engine, wings, usable item, action or activator. It must also specify a list of object names to which the modifiers will be applied. The last thing a modification defines is a list of modifiers - each modifier is a single property to change in a matching object.

```
stronger_weapon:
  target: [target type]
  names:
  - [object names]
  modifiers:
  - [modifiers]
```

In the example above, when applied to a player who has `balancedWeapon`, it will increase wing damage by 35% and add 5 to physical damage of that weapon.

## Modification settings

* `target` is a target of the modification. Available values are:
    * `engine`
    * `wings`
    * `usable item`
    * `action`
    * `activator`
* `names` is a list of names of objects which can be modified by this modification (as defined in the appropriate file, like _activators.yml_ for activators.)
* `modifiers` is a list of property modifiers. Each modifier consists of the key and the value (`key: value`). Check the correctness of these modifiers twice, since there won't be any errors in the console - they will silently fail. Available values are:
    * number - it will replace the property with this number (i.e. `4`)
    * boolean - it will replace the property with this boolean (i.e. `true`)
    * text - it will replace the property with this text (i.e. `arrow`)

## Advanced numeric modifiers

Let's consider these modifiers (as part of a modification):

```
modifiers:
  damage: "*(0.35)"
  physical_damage: "+(5)"
  wings_off: true
```

In the example above you can see some strange syntax: `*(0.35)` and `+(5)` (they are wrapped in quotation marks because YAML doesn't accept strings starting with an asterisk.) These are the multiplier and the bonus respectively. The multiplier will take the current value of the property, multiply it by the number you specified and add it to that property. For example `0.35` increases the property by 35%, while `-0.5` decreases it by 50%. If you have multiple modifiers with these multipliers, they will be summed before applying. For example `*(0.35)` and then `*(-0.1)` will increase your property by 25%.

The second type, bonus, adds the number you specified. This adding happens always after multiplying. If you want to subtract the property you will write it like `+(-10)`. The syntax may seem strange but it makes the plugin fast at parsing this value. If you want a single modifier to both multiply and add, you can use a comma to separate these two, for example: `+(20), *(0.5)` (the order here doesn't matter, the multiplier will be applied first).
