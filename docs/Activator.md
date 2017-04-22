# Activator

Activators simply check conditions. They are used in Usages, in Items. When all Activators in a Usage are active, the Usage will be used (an will run its Actions). The `type` of the activator specifies what it needs to be active and additional settings specify when exactly it activates.

Technically speaking, Activators in Usages are checked every tick (20 times per second). If there are no Activators in some Usage, it will run 20 times a second, all the time. Be careful with this. There are special `leftClick` and `rightClick` Activators which activate only after the player clicked with the appropriate mouse button. There's also a `slowTick` activator, which is active only once in four ticks. Use this to run the Usage 5 times per second. Note that clicking and `slowTick` activators are not compatible - you have only 25% chances that the click will happen on a slow tick.

## Activator types

### Left Click

**`leftClick`**

This activator is activated by the player clicking with a left mouse button while holding an Item. It doesn't have any additional settings.

```
left:
  type: leftClick
```

### Right click

**`rightClick`**

This activator is activated by the player clicking with a right mouse button while holding an Item. It doesn't have any additional settings.

```
right:
  type: rightClick
```

### Slow Tick

**`slowTick`**

This activator is activated once in four ticks. It doesn't have any additional settings.

```
slow:
  type: slowTick
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
* `max` (**default: the same as `min` value**) is the upper bound of acceptable health level.
* `number_type` (**default: absolute**) is the type of the number. Available values are:
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
onSand:
  type: blockStanding
  block: sand
```

* `block` is the [type of a block](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html).
