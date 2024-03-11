package tech.sethi.pebbles.cobbledhunters.config.screenhandler

import java.io.File

object ScreensReadMe {
    private val readmeFile = File("config/pebbles-cobbledhunters/screens/readme.md")

    private val instructions = """
# Pebble's Global Trade Screen Configuration Guide

This guide explains how to configure the screens for Pebble's Global Trade Menu. Customize the appearance of your trade menu by altering titles, item stacks, and their corresponding slots.

## Configurable Elements

- **title**: The title of the trade screen. Supports custom UI font bitmaps.
- **stacks**: Configuration for itemstack to be displayed in the allocated slots. Can be configured to use CustomModelData.
- **slots**: The slots in the trade menu that correspond to the itemstacks.

## Configuration Options

### Title
- `title`: Customize the title that appears on the trade screen.
  - Example: `"title": "<aqua>Pebble's Global Trade Menu"`

### Marketplace and Warehouse Stack
Define the appearance and placement of the stacks in the trade menu.

- `displayName`: The name displayed for the stack.
- `material`: The item used for the stack. Supports custom items.
- `amount`: Number of items in the stack.
- `nbt`: Additional NBT data for the stack.

### Slots
Array of integers indicating the positions of the stacks in the menu interface.

- Example: 
  - `"pokemonMarketplaceSlots": [0, 1, 2, ...]`
  - Slot numbers correspond to the inventory slot positions.

## Example Configuration

```json
{
  "title": "<aqua>Pebble's Global Trade Menu",
  "pokemonMarketplaceStack": {
    "displayName": "<aqua>Pokemon Marketplace",
    "material": "cobblemon:poke_ball",
    "amount": 1,
    "nbt": ""
  },
  "pokemonMarketplaceSlots": [0, 1, 2, 9, 10, 11, 18, 19, 20],
  "itemMarketplaceStack": {
    "displayName": "<aqua>Item Marketplace",
    "material": "minecraft:chest",
    "amount": 1,
    "nbt": ""
  },
  // ... other configurations ...
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