# Item

Flier items are represented by regular Minecraft items. Most settings of an item specify how the representing Minecraft item looks like: material, name, lore etc.

Items in the game don't have their default behavior. Instead, they can specify so called "usages". Each usage consists of a set of activators and a set of actions. When all activators are activated, all actions will run. More about these in _Actions_ and _Activators_ chapters.

Some items can have ammunition. If they do, every usage can consume some amount of that ammunition. A usage can't be activated if there's not enough ammunition. When it runs out, the item will either be unusable or consumed (based on `consumable` option). You can have usages with negative ammo use, these will restore ammunition.

```
item_name:
  material: [material type]
  name: [translatable text]
  lore:
  - [translatable texts]
  weight: [decimal]
  slot: [integer]
  amount: [positive integer]
  max_amount: [non-negative integer]
  min_amount: [non-negative integer]
  consumable: [true/false]
  ammo: [non-negative integer]
  usages:
    first_usage:
      cooldown: [non-negative integer]
      ammo_use: [integer]
      where: [where option]
      activators:
      - [activator names]
      actions:
      - [action names]
```

## Item settings

The settings described to the first horizontal line apply to all items - engines and wings too. Settings between the lines apply only to usable items. Usages described after the second line are used not only in usable items, but in some actions and bonuses too.

* `material` (**required**) is the type ([material](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)) of an item.
* `name` (**required**) is the name of the item. It accepts color codes (`&`).
* `lore` (**optional**) is a list of lore lines. Lore is that text under item's name. It's italic purple by default.
* `weight` (**default: 0**) is the weight of an item. The more your items weight, the more you're pulled to the ground when flying.
* `slot` (**default: -1**) is the slot in which the item will be placed. Leave it out or set to negative number if you want this item to be placed automatically (not on the hotbar). Items cannot be moved between slots by players. The only exception are wings, which are described in _Wings_ chapter.

***

* `consumable` (**default: false**) specifies whenever the item will be consumed after use (or after the ammo runs out).
* `amount` (**default: 1**) is the amount of these items given to the player.
* `max_amount` (**default: 0**) is the maximum amount of these items that the player can have. `0` means unlimited.
* `min_amount` (**default: 0**) is the amount of these items that the player won't be able to sell if selling is possible.
* `ammo` (**default: 0**) is the maximum amount of ammunition in this item.

***

* `usages` is a list of usages.
  * `cooldown` (**default: 0**) is a cooldown time (in ticks) after activating this usage, while no other usages can be activated.
  * `ammo_use` (**default: 0**) is the amount of ammunition removed with every use. Negative values will add ammunition.
  * `where` (**default: everywhere**) is the position in which the player has to be in order to activate this usage. Acceptable values are:
    * `everywhere` (should be "anywhere", I know)
    * `air`
    * `ground`
    * `fall`
    * `no air` (ground or fall)
    * `no ground` (air or fall)
    * `no fall` (air or ground)
  * `activators` is a list of activators, as defined in _activators.yml_
  * `actions` is a list of actions, as defined in _actions.yml_.
