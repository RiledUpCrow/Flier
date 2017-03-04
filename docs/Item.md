# Item

Flier Items are represented by regular Minecraft items. Most settings of an Item specify how the representing Minecraft item looks like: material, name, lore etc.

Items in the game don't have their default behavior. Instead, they can specify so called "Usages". Each Usage consists of a set of Activators and a set of Actions. When all Activators are activated, all Actions will run. More about these in _Actions_ and _Activators_ chapters.

Some items can have ammunition. If they do, every Usage consumes some amount of that ammunition. An Usage can't be activated if there is not enough ammunition. When it runs out, the item will either be unusable or consumed (based on `consumable` option). You can have Usages with negative ammo use, these will restore ammunition.

```
item_name:
  material: [material type]
  name: Some name
  lore:
  - Some lines of the lore
  - More lines...
  weight: [decimal]]
  slot: [integer]
  consumable: [boolean]
  ammo: [non-negative integer]
  usages:
    first_usage:
      cooldown: [non-negative integer]
      ammo_use: [integer]
      where: [where option]
      activators:
      - some_activator
      actions:
      - some_action
```

## Item settings

* `material` (**required**) is the type ([material](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)) of an item.
* `name` (**required**) is the name of the item. It accepts color codes (`&`).
* `lore` is a list of lore lines. Lore is that text under item's name. It's italic purple by default.
* `weight` (**default: 0**) is the weight of an item. The more your items weight, the more you're pulled to the ground when flying.
* `slot` (**default: -1**) is the slot in which the item will be placed. Leave it out or set to negative number if you want this item to be placed automatically (not on the hotbar).
* `consumable` (**default: false**) specifies whenever the item will be consumed after use (or after the ammo runs out).
* `ammo` (**default: 0**) is the maximum amount of ammunition in this item.
* `usages` is a list of Usages.
  * `cooldown` (**default: 0**) is a cooldown time (in ticks) after activating this Usage, while no other Usages cannot be activated.
  * `ammo_use` (**default: 0**) is the amount of ammunition removed with every use. Negative values will add ammunition.
  * `where` (**default: everywhere**) is the position in which the player has to be in order to activate this Usage. Acceptable values are:
    * `everywhere`
    * `air`
    * `ground`
    * `fall`
    * `no air` (ground or fall)
    * `no ground` (air or fall)
    * `no fall` (air or ground)
  * `activators` is a list of Activators, as defined in _activators.yml_
  * `actions` is a list of Actions, as defined in _actions.yml_.
