package tech.sethi.pebbles.cobbledhunters.hunt.type

import net.minecraft.server.network.ServerPlayerEntity
import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.PM

data class HuntReward(
    val id: String,
    val name: String,
    var amount: Int? = 1,
    val splitable: Boolean? = true,
    val displayItem: ConfigHandler.SerializedItemStack,
    var commands: List<String>,
    var message: String? = "You collected {display_item.name}",
) {
    fun deepCopy(): HuntReward {
        return HuntReward(
            id, name, amount, splitable, displayItem.deepCopy(), commands, message
        )
    }

    fun executeCommands(player: ServerPlayerEntity) {
        for (command in commands) {
            val replacedCommand = command.replace("{player_name}", player.name.string)
            val replacedCommand2 = replacedCommand.replace("{amount}", amount.toString())
            PM.runCommand(replacedCommand2)
            val replacedMessage = displayItem.displayName?.let { message?.replace("{display_item.name}", it) }
            val replacedMessage2 = replacedMessage?.replace("{amount}", amount.toString())
            if (replacedMessage2 != null) {
                PM.sendText(player, replacedMessage2)
            }
        }
    }
}

val pokeballReward1 = HuntReward(
    "pokeball_16",
    "16x <red>Poke<white>ball",
    16,
    true,
    ConfigHandler.SerializedItemStack(
        "16x <red>Poke<white>ball",
        "cobblemon:poke_ball",
        16,
        null,
        lore = mutableListOf("A Pokeball that can be used to catch Pokemon")
    ),
    listOf(
        "give {player_name} cobblemon:poke_ball {amount}"
    ),
)

val pokeballReward2 = HuntReward(
    "pokeball_32",
    "32x <red>Poke<white>ball",
    32,
    true,
    ConfigHandler.SerializedItemStack(
        "32x <red>Poke<white>ball",
        "cobblemon:poke_ball",
        32,
        null,
        lore = mutableListOf("A Pokeball that can be used to catch Pokemon")
    ),
    listOf(
        "give {player_name} cobblemon:poke_ball {amount}"
    ),
)

val pokeballReward3 = HuntReward(
    "pokeball_64",
    "64x <red>Poke<white>ball",
    64,
    true,
    ConfigHandler.SerializedItemStack(
        "64x <red>Poke<white>ball",
        "cobblemon:poke_ball",
        64,
        null,
        mutableListOf("A Pokeball that can be used to catch Pokemon"),
    ),
    listOf(
        "give {player_name} cobblemon:poke_ball {amount}"
    ),
)

val pebblesReward1 = HuntReward(
    "pebbles_300",
    "300 <gold>Pebbles",
    300,
    true,
    ConfigHandler.SerializedItemStack(
        "300 <gold>Pebbles",
        "minecraft:feather",
        1,
        "{CustomModelData:8}",
        lore = mutableListOf("Get rich or die trying!")
    ),
    listOf(
        "padmin eco deposit {player_name} {amount}"
    ),
)

val pebblesReward2 = HuntReward(
    "pebbles_1000",
    "1000 <gold>Pebbles",
    1000,
    true,
    ConfigHandler.SerializedItemStack(
        "1000 <gold>Pebbles", "minecraft:feather", 1, "{CustomModelData:8}", mutableListOf("Get rich or die trying!")
    ),
    listOf(
        "padmin eco deposit {player_name} {amount}"
    ),
)

val pebblesReward3 = HuntReward(
    "pebbles_2000",
    "2000 <gold>Pebbles",
    2000,
    true,
    ConfigHandler.SerializedItemStack(
        "2000 <gold>Pebbles", "minecraft:feather", 1, "{CustomModelData:8}", mutableListOf("Get rich or die trying!")
    ),
    listOf(
        "padmin eco deposit {player_name} {amount}"
    ),
)

val rareCanReward1 = HuntReward(
    "rare_candy_8",
    "8x <blue>Rare Candy",
    8,
    true,
    ConfigHandler.SerializedItemStack(
        "8x <blue>Rare Candy",
        "cobblemon:rare_candy",
        8,
        null,
        mutableListOf("A Rare Candy that can be used to level up your Pokemon"),
    ),
    listOf(
        "give {player_name} cobblemon:rare_candy {amount}"
    ),
)

val rareCanReward2 = HuntReward(
    "rare_candy_16",
    "16x <blue>Rare Candy",
    16,
    true,
    ConfigHandler.SerializedItemStack(
        "16x <blue>Rare Candy",
        "cobblemon:rare_candy",
        16,
        null,
        mutableListOf("A Rare Candy that can be used to level up your Pokemon")
    ),
    listOf(
        "give {player_name} cobblemon:rare_candy {amount}"
    ),
)

val rareCanReward3 = HuntReward(
    "rare_candy_32",
    "32x <blue>Rare Candy",
    32,
    true,
    ConfigHandler.SerializedItemStack(
        "32x <blue>Rare Candy",
        "cobblemon:rare_candy",
        32,
        null,
        mutableListOf("A Rare Candy that can be used to level up your Pokemon")
    ),
    listOf(
        "give {player_name} cobblemon:rare_candy {amount}"
    ),
)


val rewardList = listOf(
    pokeballReward1,
    pokeballReward2,
    pokeballReward3,
    pebblesReward1,
    pebblesReward2,
    pebblesReward3,
    rareCanReward1,
    rareCanReward2,
    rareCanReward3
)