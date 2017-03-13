# Integrations

Flier is fully standalone and doesn't need any additional plugins to work. You can however install these nice plugins to enable additional functionality:

## [BetonLangAPI](https://github.com/Co0sh/BetonLangAPI)

This plugin is a language library which allows displaying translated messages based on player's chosen language. It supports MySQL database too, so you can use it to sync multiple server or a website.

Item names, lore, team and class names can be translated into multiple languages by replacing their text with `$something` tags and putting actual text in the _messages.yml_ under `something` keys. Just make sure your tags don't collide with existing messages used by the plugin. An example of an item set (from _sets.yml_ file) which has translated class name:

```
translated_set:
  name: '$className'
  category: main
  [...]
```

And in the _messages.yml_:

```
en:
  className: Fast
  [...]
pl:
  className: Szybki
  [...]
```
