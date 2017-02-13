# Item Set

Item set is a list of items with specified method of adding them to the player. Each set can specify an engine, wings and list of usable items. This is pretty straightforward. The interesting part is `type`, which specifies _how_ these items will be added. These are valid values:

* `clear`
* `replace`
* `add`
* `take`
* `reset`

Clear will wipe all player's items and give the ones specified in the set. Replace will give the items and replace items on conflicting slots. Add will try to add the items but it will fail if the items on one of the slots are conflicting, and take will remove the specified items from the player. Reset is a special case - it won't give any items, it will simply restore the default class (the one player receives when entering the lobby).

An item set can optionally have `name` value, which will be used as class name if this set is applied to the player. If more than one item set with a name is applied, the last one will be used.

There's also `saving`, which defaults to `true` and controls whenever the item set will be saved in the "stored" stage of player's class (more info in _Lobby_ chapter).

Each specified item can have "max" value, which limits the maximum amount of this item a player can have. It only prevents using `add` type item sets.

```
items:
  item_set_name:
    type: [set type]
    saving: [boolean]
    engine: some_engine
    wings: some_wings
    items:
    - item: some_item
      max: [positive integer]
    - [other items...]
```

* `saving` (**default: true**) whenever the set will be applied to "stored" (when `true`) or "current" (when `false`) items.
* `engine` is the name of the Engine, as defined in _engines.yml_.
* `wings` is the name of the Wings, as defined in _wings.yml_.
* `items` is the list of items in this Item Set
  * `item` is the name of the Item, as defined in _items.yml_.
  * `max` (**default: 0**) is the maximum amount of this item that the player can have. `0` means unlimited.
