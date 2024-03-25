package tech.sethi.pebbles.cobbledhunters.hunt.type

import net.minecraft.server.network.ServerPlayerEntity
import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.PM

data class HuntReward(
    val id: String,
    var name: String,
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

val pebblesReward4 = HuntReward(
    "pebbles_5000",
    "5000 <gold>Pebbles",
    5000,
    true,
    ConfigHandler.SerializedItemStack(
        "5000 <gold>Pebbles", "minecraft:feather", 1, "{CustomModelData:8}", mutableListOf("Get rich or die trying!")
    ),
    listOf(
        "padmin eco deposit {player_name} {amount}"
    ),
)

val pebblesReward5 = HuntReward(
    "pebbles_10000",
    "10000 <gold>Pebbles",
    10000,
    true,
    ConfigHandler.SerializedItemStack(
        "10000 <gold>Pebbles", "minecraft:feather", 1, "{CustomModelData:8}", mutableListOf("Get rich or die trying!")
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

val redApricornReward = HuntReward(
    "red_apricorn",
    "Red Apricorn",
    16,
    true,
    ConfigHandler.SerializedItemStack(
        "<red>Red Apricorn",
        "cobblemon:red_apricorn",
        16,
        null,
        mutableListOf("A red apricorn that can be used to craft Pokeballs")
    ),
    listOf(
        "give {player_name} cobblemon:red_apricorn {amount}"
    ),
)

val blueApricornReward = HuntReward(
    "blue_apricorn", "Blue Apricorn", 16, true, ConfigHandler.SerializedItemStack(
        "<blue>Blue Apricorn",
        "cobblemon:blue_apricorn",
        16,
        null,
        mutableListOf("A blue apricorn that can be used to craft Pokeballs")
    ), listOf(
        "give {player_name} cobblemon:blue_apricorn {amount}"
    )
)

val yellowApricornReward = HuntReward(
    "yellow_apricorn", "Yellow Apricorn", 16, true, ConfigHandler.SerializedItemStack(
        "<yellow>Yellow Apricorn",
        "cobblemon:yellow_apricorn",
        16,
        null,
        mutableListOf("A yellow apricorn that can be used to craft Pokeballs")
    ), listOf(
        "give {player_name} cobblemon:yellow_apricorn {amount}"
    )
)

val potionReward = HuntReward(
    "potion", "Potion", 16, true, ConfigHandler.SerializedItemStack(
        "<blue>Potion", "cobblemon:potion", 16, null
    ), listOf(
        "give {player_name} cobblemon:potion {amount}"
    )
)

val superPotionReward = HuntReward(
    "super_potion", "Super Potion", 8, true, ConfigHandler.SerializedItemStack(
        "<red>Super Potion", "cobblemon:super_potion", 8, null
    ), listOf(
        "give {player_name} cobblemon:super_potion {amount}"
    )
)

val hyperPotionReward = HuntReward(
    "hyper_potion", "Hyper Potion", 4, true, ConfigHandler.SerializedItemStack(
        "<light_purple>Hyper Potion", "cobblemon:hyper_potion", 4, null
    ), listOf(
        "give {player_name} cobblemon:hyper_potion {amount}"
    )
)

val maxPotionReward = HuntReward(
    "max_potion", "Max Potion", 4, true, ConfigHandler.SerializedItemStack(
        "<green>Max Potion", "cobblemon:max_potion", 4, null
    ), listOf(
        "give {player_name} cobblemon:max_potion {amount}"
    )
)

val hpUpReward1 = HuntReward(
    "hp_up", "HP Up", 4, true, ConfigHandler.SerializedItemStack(
        "<red>HP Up", "cobblemon:hp_up", 4, null
    ), listOf(
        "give {player_name} cobblemon:hp_up {amount}"
    )
)

val hpUpReward2 = HuntReward(
    "hp_up", "HP Up", 8, true, ConfigHandler.SerializedItemStack(
        "<red>HP Up", "cobblemon:hp_up", 8, null
    ), listOf(
        "give {player_name} cobblemon:hp_up {amount}"
    )
)

val hpUpReward3 = HuntReward(
    "hp_up", "HP Up", 16, true, ConfigHandler.SerializedItemStack(
        "<red>HP Up", "cobblemon:hp_up", 16, null
    ), listOf(
        "give {player_name} cobblemon:hp_up {amount}"
    )
)

val proteinReward = HuntReward(
    "protein", "Protein", 4, true, ConfigHandler.SerializedItemStack(
        "<yellow>Protein", "cobblemon:protein", 4, null
    ), listOf(
        "give {player_name} cobblemon:protein {amount}"
    )
)

val proteinReward2 = HuntReward(
    "protein", "Protein", 8, true, ConfigHandler.SerializedItemStack(
        "<yellow>Protein", "cobblemon:protein", 8, null
    ), listOf(
        "give {player_name} cobblemon:protein {amount}"
    )
)

val proteinReward3 = HuntReward(
    "protein", "Protein", 16, true, ConfigHandler.SerializedItemStack(
        "<yellow>Protein", "cobblemon:protein", 16, null
    ), listOf(
        "give {player_name} cobblemon:protein {amount}"
    )
)

val ironReward1 = HuntReward(
    "iron", "Iron", 4, true, ConfigHandler.SerializedItemStack(
        "<gray>Iron", "cobblemon:iron", 4, null
    ), listOf(
        "give {player_name} cobblemon:iron {amount}"
    )
)

val ironReward2 = HuntReward(
    "iron", "Iron", 8, true, ConfigHandler.SerializedItemStack(
        "<gray>Iron", "cobblemon:iron", 8, null
    ), listOf(
        "give {player_name} cobblemon:iron {amount}"
    )
)

val ironReward3 = HuntReward(
    "iron", "Iron", 16, true, ConfigHandler.SerializedItemStack(
        "<gray>Iron", "cobblemon:iron", 16, null
    ), listOf(
        "give {player_name} cobblemon:iron {amount}"
    )
)

val calciumReward1 = HuntReward(
    "calcium", "Calcium", 4, true, ConfigHandler.SerializedItemStack(
        "<white>Calcium", "cobblemon:calcium", 4, null
    ), listOf(
        "give {player_name} cobblemon:calcium {amount}"
    )
)

val calciumReward2 = HuntReward(
    "calcium", "Calcium", 8, true, ConfigHandler.SerializedItemStack(
        "<white>Calcium", "cobblemon:calcium", 8, null
    ), listOf(
        "give {player_name} cobblemon:calcium {amount}"
    )
)

val calciumReward3 = HuntReward(
    "calcium", "Calcium", 16, true, ConfigHandler.SerializedItemStack(
        "<white>Calcium", "cobblemon:calcium", 16, null
    ), listOf(
        "give {player_name} cobblemon:calcium {amount}"
    )
)

val zincReward1 = HuntReward(
    "zinc", "Zinc", 4, true, ConfigHandler.SerializedItemStack(
        "<gray>Zinc", "cobblemon:zinc", 4, null
    ), listOf(
        "give {player_name} cobblemon:zinc {amount}"
    )
)

val zincReward2 = HuntReward(
    "zinc", "Zinc", 8, true, ConfigHandler.SerializedItemStack(
        "<gray>Zinc", "cobblemon:zinc", 8, null
    ), listOf(
        "give {player_name} cobblemon:zinc {amount}"
    )
)

val zincReward3 = HuntReward(
    "zinc", "Zinc", 16, true, ConfigHandler.SerializedItemStack(
        "<gray>Zinc", "cobblemon:zinc", 16, null
    ), listOf(
        "give {player_name} cobblemon:zinc {amount}"
    )
)

val carbosReward1 = HuntReward(
    "carbos", "Carbos", 4, true, ConfigHandler.SerializedItemStack(
        "<white>Carbos", "cobblemon:carbos", 4, null
    ), listOf(
        "give {player_name} cobblemon:carbos {amount}"
    )
)

val carbosReward2 = HuntReward(
    "carbos", "Carbos", 8, true, ConfigHandler.SerializedItemStack(
        "<white>Carbos", "cobblemon:carbos", 8, null
    ), listOf(
        "give {player_name} cobblemon:carbos {amount}"
    )
)

val carbosReward3 = HuntReward(
    "carbos", "Carbos", 16, true, ConfigHandler.SerializedItemStack(
        "<white>Carbos", "cobblemon:carbos", 16, null
    ), listOf(
        "give {player_name} cobblemon:carbos {amount}"
    )
)

val ppUpReward1 = HuntReward(
    "pp_up", "PP Up", 4, true, ConfigHandler.SerializedItemStack(
        "<green>PP Up", "cobblemon:pp_up", 4, null
    ), listOf(
        "give {player_name} cobblemon:pp_up {amount}"
    )
)

val ppUpReward2 = HuntReward(
    "pp_up", "PP Up", 8, true, ConfigHandler.SerializedItemStack(
        "<green>PP Up", "cobblemon:pp_up", 8, null
    ), listOf(
        "give {player_name} cobblemon:pp_up {amount}"
    )
)

val ppUpReward3 = HuntReward(
    "pp_up", "PP Up", 16, true, ConfigHandler.SerializedItemStack(
        "<green>PP Up", "cobblemon:pp_up", 16, null
    ), listOf(
        "give {player_name} cobblemon:pp_up {amount}"
    )
)


val rewardList = listOf(
    pokeballReward1,
    pokeballReward2,
    pokeballReward3,
    pebblesReward1,
    pebblesReward2,
    pebblesReward3,
    pebblesReward4,
    pebblesReward5,
    rareCanReward1,
    rareCanReward2,
    rareCanReward3,
    redApricornReward,
    blueApricornReward,
    yellowApricornReward,
    potionReward,
    superPotionReward,
    hyperPotionReward,
    maxPotionReward,
    hpUpReward1,
    hpUpReward2,
    hpUpReward3,
    proteinReward,
    proteinReward2,
    proteinReward3,
    ironReward1,
    ironReward2,
    ironReward3,
    calciumReward1,
    calciumReward2,
    calciumReward3,
    zincReward1,
    zincReward2,
    zincReward3,
    carbosReward1,
    carbosReward2,
    carbosReward3,
    ppUpReward1,
    ppUpReward2,
    ppUpReward3
)

val apricornPool: List<PoolReward> = listOf(
    PoolReward(redApricornReward.id, 5),
    PoolReward(blueApricornReward.id, 3),
    PoolReward(yellowApricornReward.id, 1)
)

val vitaminPool1: List<PoolReward> = listOf(
    PoolReward(hpUpReward1.id, 1),
    PoolReward(proteinReward.id, 1),
    PoolReward(ironReward1.id, 1),
    PoolReward(calciumReward1.id, 1),
    PoolReward(zincReward1.id, 1),
    PoolReward(carbosReward1.id, 1),
    PoolReward(ppUpReward1.id, 1)
)

val vitaminPool2: List<PoolReward> = listOf(
    PoolReward(hpUpReward2.id, 1),
    PoolReward(proteinReward2.id, 1),
    PoolReward(ironReward2.id, 1),
    PoolReward(calciumReward2.id, 1),
    PoolReward(zincReward2.id, 1),
    PoolReward(carbosReward2.id, 1),
    PoolReward(ppUpReward2.id, 1)
)

val vitaminPool3: List<PoolReward> = listOf(
    PoolReward(hpUpReward3.id, 1),
    PoolReward(proteinReward3.id, 1),
    PoolReward(ironReward3.id, 1),
    PoolReward(calciumReward3.id, 1),
    PoolReward(zincReward3.id, 1),
    PoolReward(carbosReward3.id, 1),
    PoolReward(ppUpReward3.id, 1)
)