# Activator

Activators simply check conditions. They are used in usages. When all activators in a usage are active, the usage will be used (an will run its actions). The `type` of the activator specifies what it needs to be active and additional settings specify when exactly it activates.

Technically speaking, activators in usages are checked every tick (20 times per second). If there are no activators in a usage, it will run 20 times a second, all the time. Be careful with this. There is a special `trigger` activator which can be configured to activate only after the player has clicked with the appropriate mouse button. There's also an `interval` activator, which is active only once in _n_ ticks. Use this to run the usage less often. Note that `trigger` and `interval` activators (in this order) together will make only _n_th trigger work (in the opposite order you will have _1/n_ chance of the trigger working.)

## Activator types

### Trigger

**`trigger`**

This activator is activated when a specified trigger occurs. Currently there are these triggers:

* `left_click`
* `right_click`

```
leftClick:
  type: trigger
  trigger: [trigger type]
```

### Interval

**`interval`**

This activator is activated once in _n_ checks. Since activators are checked every tick in the order they were defined and the check fails as soon as some activator fails, placing `interval` correctly is essential. For example when it's _after_ the `trigger` activator, it will pass every _n_ times that trigger happens. However, placing it _before_ makes the behavior undefined because you never know if the `trigger` will happen at the _n_th tick.

```
slow:
  type: interval
  interval: [positive integer]
```

### Wings health

**`wingsHealth`**

This activator is activated when the Wings health is between `min` and `max` value. It can be either in absolute values or percentage of maximum health.

```
wing_health:
  type: wingsHealth
  min: [non-negative decimal]
  max: [non-negative decimal]
  number_type: [type of a number]
```

* `min` (**required**) is the lower bound of acceptable health level.
* `max` (**default: `min` value**) is the upper bound of acceptable health level.
* `number_type` (**default: `absolute`**) is the type of the number. Available values are:
    * `absolute`
    * `percentage`

### Item

**`item`**

This activator is activated when the player has specified item in the inventory.

```
has_item:
  type: item
  item: [item name]
```

* `item` the name of the item, as in _items.yml_ file.

### Standing on a block

**`blockStanding`**

This activator is activated when the player is standing on a specified block.

```
on_a_block:
  type: blockStanding
  block: [block]
```

* `block` is the [type of a block](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html).
