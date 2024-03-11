package tech.sethi.pebbles.cobbledhunters.hunt.type

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler

data class HuntReward(
    val id: String,
    val name: String,
    val amount: Int? = 1,
    val splitable: Boolean? = true,
    val displayItem: ConfigHandler.SerializedItemStack,
    var commands: List<String>,
    var message: String? = "You collected {display_item.name}",
)

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
        lore = listOf("A Pokeball that can be used to catch Pokemon")
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
        lore = listOf("A Pokeball that can be used to catch Pokemon")
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
        listOf("A Pokeball that can be used to catch Pokemon"),
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
        "300 <gold>Pebbles", "minecraft:feather", 1, "{CustomModelData:8}", lore = listOf("Get rich or die trying!")
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
        "1000 <gold>Pebbles", "minecraft:feather", 1, "{CustomModelData:8}", listOf("Get rich or die trying!")
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
        "2000 <gold>Pebbles", "minecraft:feather", 1, "{CustomModelData:8}", listOf("Get rich or die trying!")
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
        listOf("A Rare Candy that can be used to level up your Pokemon"),
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
        listOf("A Rare Candy that can be used to level up your Pokemon")
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
        listOf("A Rare Candy that can be used to level up your Pokemon")
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