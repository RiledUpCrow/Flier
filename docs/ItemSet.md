# Item Set

Item set is a list of items with specified method of adding them to the player. Each set can specify an engine, wings, a list of usable items and a list of modifications. This is pretty straightforward once you know what these are (described in next chapters). Item sets are defined in _sets.yml_ file.

An item set can optionally have `class_name` value, which will be used as class name if this set is applied to the player. If more than one item set with a name is applied, the last one will be used.

Each specified item can have `max` value which limits the maximum amount of this item a player can have and `min` value which is the minimum limit below which selling is not possible.

```
items:
  item_set_name:
    class_name: [text]
    engine: [engine name]
    wings: [wings name]
    items:
    - [item name]
    modifications:
    - [modification name]
```

## Item set settings

* `class_name` (**optional**) is the name of the class; the player's class will have this name if he chooses this item set. This setting is generally optional, but must be specified in at least one item set in the kit - the player cannot have only sets without a name.
* `engine` (**optional**) is the name of the Engine, as defined in _engines.yml_.
* `wings` (**optional**) is the name of the Wings, as defined in _wings.yml_.
* `items` (**optional**) is the list of items in this set, as defined in _items.yml_.
* `modifications` (**optional**) is the list of Modifications, as defined in _modifications.yml_.
