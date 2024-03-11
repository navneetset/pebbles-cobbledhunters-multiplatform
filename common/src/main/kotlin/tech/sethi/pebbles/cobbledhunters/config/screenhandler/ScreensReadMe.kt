package tech.sethi.pebbles.cobbledhunters.config.screenhandler

import java.io.File

object ScreensReadMe {
    private val readmeFile = File("config/pebbles-cobbledhunters/screens/readme.md")

    private val instructions = """
# Pebble's Screen Configuration Guide

This guide explains how to configure the screens for Pebble's Global Trade Menu. Customize the appearance of your trade menu by altering titles, item stacks, and their corresponding slots.

## Configurable Elements

- **title**: The title of the trade screen. Supports custom UI font bitmaps.
- **stacks**: Configuration for itemstack to be displayed in the allocated slots. Can be configured to use CustomModelData.
- **slots**: The slots in the trade menu that correspond to the itemstacks.

## Configuration Options

### Title
- `title`: Customize the title that appears on the trade screen.
  - Example: `"title": "<aqua>Pebble's Global Trade Menu"`
  You may also use a custom font by specifying the font bitmap in the title.

## Example Configuration

```json
{
  "title": "<blue>Global Hunts",
  "slots": [
    {
      "slot": 0,
      "huntPoolId": "arachnid_pool",
      "itemStack": {
        "displayName": "<light_purple>Arachnids",
        "material": "minecraft:spider_spawn_egg",
        "amount": 1,
        "lore": [
          "<gray>Difficulty: <light_purple>Easy",
          "<gray>Features: <light_purple>Arachnids Pokémon",
          "<gray>Time Limit: <light_purple>2 Hours",
          "<gray>Start Time: <light_purple>Every 2 Hours",
          "<aqua>Click to view rewards!",
          "",
          "{ongoing_hunt}"
        ]
      }
    }
  ],
  "emptySlotItemStack": {
    "displayName": "<gray>",
    "material": "minecraft:gray_stained_glass_pane",
    "amount": 1,
    "lore": [
      "<gray>There is no global hunt in this slot.",
      "<gray>Check back later!"
    ]
  }
}
```
    """.trimMargin()

    init {
        if (!readmeFile.exists()) {
            readmeFile.parentFile.mkdirs()
            readmeFile.createNewFile()
            readmeFile.writeText(instructions)
        }
    }
}