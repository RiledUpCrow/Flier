# Item Set

Item set is a list of items with specified method of adding them to the player. Each set can specify an engine, wings, a list of usable items and a list of modifications. This is pretty straightforward once you know what these are (described in next chapters). Item sets are defined in _sets.yml_ file.

An item set can optionally have `name` value, which will be used as class name if this set is applied to the player. If more than one item set with a name is applied, the last one will be used.

Each specified item can have `max` value which limits the maximum amount of this item a player can have and `min` value which is the minimum limit below which selling is not possible.

```
items:
  item_set_name:
    name: The name
    engine: some_engine
    wings: some_wings
    items:
    - item: some_item
      max: [non-negative integer]
      min: [non-negative integer]
    - [other items...]
    modifications:
    - some_modification
    - other_modification
```

## Item set settings

* `name` is the name of the class; the player will have this name if he chooses this item set
* `engine` is the name of the Engine, as defined in _engines.yml_.
* `wings` is the name of the Wings, as defined in _wings.yml_.
* `items` is the list of items in this Item Set
  * `item` is the name of the Item, as defined in _items.yml_.
  * `max` (**default: 0**) is the maximum amount of this items that the player can have. `0` means unlimited.
  * `min` (**default: 0**) is the amount of this items that the player won't be able to sell if selling is possible.
* `modifications` is the list of Modifications, as defined in _modifications.yml_.
