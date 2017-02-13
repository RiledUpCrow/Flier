# Activator

Activators simply check conditions. They are used in Usages, in Items. When all Activators in a Usage are active, the Usage will be used (an will run its Actions). The `type` of the Activator specifies what it needs to be active and additional settings specify when exactly it activates.

Technically speaking, Activators in Usages are checked every tick (20 times per second). If there are no Activators in some Usage, it will run 20 times a second, all the time. Be careful with this. There are special `leftClick` and `rightClick` Activators which activate only after the player clicked with the appropriate mouse button. There's also a `slowTick` Activator, which is active only once in four ticks. Use this to run the Usage 5 times per second. Note that clicking and `slowTick` activators are not compatible - you have only 25% chances that the click will happen on a slow tick.

# Activator types

## Left Click

**`leftClick`**

This Activator is activated by the player clicking with a left mouse button while holding an Item. It doesn't have any additional settings.

```
left:
  type: leftClick
```

## Right click

**`rightClick`**

This Activator is activated by the player clicking with a right mouse button while holding an Item. It doesn't have any additional settings.

```
right:
  type: rightClick
```

## Slow Tick

**`slowTick`**

This Activator is activated once in four ticks. It doesn't have any additional settings.

```
slow:
  type: slowTick
```

## Wings health

**`wingsHealth`**

This Activator is activated when the Wings health is between `min` and `max` value. It can be either in absolute values or percentage of maximum health.

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
