package tech.sethi.pebbles.cobbledhunters.config.economy

import java.io.File

object EconomyReadMe {
    private val readmeFile = File("config/pebbles-cobbledhunters/economy/readme.md")

    private val instructions = """
# Economy Configuration Guide

This configuration guide helps you set up the economy system for your server. You can choose between different economy backends based on your preference and setup.

## Available Economy Systems

- **PEBBLES**: Utilizes the Pebbles internal economy system.
- **VAULT**: Integrates with the Vault API, requiring an additional economy plugin.
- **IMPACTOR**: Utilizes the Impactor economy system.

## Configuration Details

### PEBBLES
- Custom economy system, request on Pebble's Discord for more information.

### VAULT
- Requires an economy plugin like EssentialsX or CMI.
- Ensure you have Pebble's ForgeVaultBridge installed, available on Pebble's Discord.

### IMPACTOR
- Requires the Impactor mod, available [here](https://modrinth.com/mod/impactor). Most commonly used amongst other Cobblemon sidemods.

## Configuration Example

```json
{
    "economy": "IMPACTOR",
    "currencySymbol": "₽",
    "currencyName": "Pebbles"
}
    """.trimMargin()

    init {
        if (!readmeFile.exists()) {
            readmeFile.parentFile.mkdirs()
            readmeFile.createNewFile()
            readmeFile.writeText(instructions)
        }
    }

}