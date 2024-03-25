package tech.sethi.pebbles.cobbledhunters.hunt.type

data class HuntPool(
    val id: String, val name: String, val huntIds: List<String>
)

val spiderFeatures = listOf(
    HuntFeature(listOf("spinarak")),
    HuntFeature(listOf("ariados")),
    HuntFeature(listOf("joltik")),
    HuntFeature(listOf("galvantula")),
    HuntFeature(listOf("dewpider")),
    HuntFeature(listOf("araquanid"))
)

val spiderHuntList = listOf(
    Hunt(
        id = "spinarak",
        name = "<light_purple>Spinarak",
        huntFeature = spiderFeatures[0],
        guaranteedRewardId = listOf(),
        rewardPools = listOf(
            Pool(
                listOf(
                    PoolReward(pokeballReward1.id, 5),
                    PoolReward(pokeballReward2.id, 3),
                    PoolReward(pokeballReward3.id, 1)
                )
            ), Pool(
                listOf(
                    PoolReward(pebblesReward1.id, 5), PoolReward(pebblesReward2.id, 3), PoolReward(pebblesReward3.id, 1)
                )
            ), Pool(
                listOf(
                    PoolReward(rareCanReward1.id, 5), PoolReward(rareCanReward2.id, 3), PoolReward(rareCanReward3.id, 1)
                )
            )
        ),
        experience = 100
    ),
    Hunt(
        id = "ariados",
        name = "<light_purple>Ariados",
        huntFeature = spiderFeatures[1],
        guaranteedRewardId = listOf(),
        rewardPools = listOf(
            Pool(
                listOf(
                    PoolReward(pokeballReward1.id, 5),
                    PoolReward(pokeballReward2.id, 3),
                    PoolReward(pokeballReward3.id, 1)
                )
            ), Pool(
                listOf(
                    PoolReward(pebblesReward1.id, 5), PoolReward(pebblesReward2.id, 3), PoolReward(pebblesReward3.id, 1)
                )
            ), Pool(
                listOf(
                    PoolReward(rareCanReward1.id, 5), PoolReward(rareCanReward2.id, 3), PoolReward(rareCanReward3.id, 1)
                )
            )
        ),
        experience = 200
    ),
    Hunt(
        id = "joltik",
        name = "<light_purple>Joltik",
        huntFeature = spiderFeatures[2],
        guaranteedRewardId = listOf(),
        rewardPools = listOf(
            Pool(
                listOf(
                    PoolReward(pokeballReward1.id, 5),
                    PoolReward(pokeballReward2.id, 3),
                    PoolReward(pokeballReward3.id, 1)
                )
            ), Pool(
                listOf(
                    PoolReward(pebblesReward1.id, 5), PoolReward(pebblesReward2.id, 3), PoolReward(pebblesReward3.id, 1)
                )
            ), Pool(
                listOf(
                    PoolReward(rareCanReward1.id, 5), PoolReward(rareCanReward2.id, 3), PoolReward(rareCanReward3.id, 1)
                )
            )
        ),
        experience = 300
    ),
    Hunt(
        id = "galvantula",
        name = "<light_purple>Galvantula",
        huntFeature = spiderFeatures[3],
        guaranteedRewardId = listOf(),
        rewardPools = listOf(
            Pool(
                listOf(
                    PoolReward(pokeballReward1.id, 5),
                    PoolReward(pokeballReward2.id, 3),
                    PoolReward(pokeballReward3.id, 1)
                )
            ), Pool(
                listOf(
                    PoolReward(pebblesReward1.id, 5), PoolReward(pebblesReward2.id, 3), PoolReward(pebblesReward3.id, 1)
                )
            ), Pool(
                listOf(
                    PoolReward(rareCanReward1.id, 5), PoolReward(rareCanReward2.id, 3), PoolReward(rareCanReward3.id, 1)
                )
            )
        ),
        experience = 300
    ),
    Hunt(
        id = "dewpider",
        name = "<light_purple>Dewpider",
        huntFeature = spiderFeatures[4],
        guaranteedRewardId = listOf(),
        rewardPools = listOf(
            Pool(
                listOf(
                    PoolReward(pokeballReward1.id, 5),
                    PoolReward(pokeballReward2.id, 3),
                    PoolReward(pokeballReward3.id, 1)
                )
            ), Pool(
                listOf(
                    PoolReward(pebblesReward1.id, 5), PoolReward(pebblesReward2.id, 3), PoolReward(pebblesReward3.id, 1)
                )
            ), Pool(
                listOf(
                    PoolReward(rareCanReward1.id, 5), PoolReward(rareCanReward2.id, 3), PoolReward(rareCanReward3.id, 1)
                )
            )
        ),
        experience = 300
    ),
    Hunt(
        id = "araquanid",
        name = "<light_purple>Araquanid",
        huntFeature = spiderFeatures[5],
        guaranteedRewardId = listOf(),
        rewardPools = listOf(
            Pool(
                listOf(
                    PoolReward(pokeballReward1.id, 5),
                    PoolReward(pokeballReward2.id, 3),
                    PoolReward(pokeballReward3.id, 1)
                )
            ), Pool(
                listOf(
                    PoolReward(pebblesReward1.id, 5), PoolReward(pebblesReward2.id, 3), PoolReward(pebblesReward3.id, 1)
                )
            ), Pool(
                listOf(
                    PoolReward(rareCanReward1.id, 5), PoolReward(rareCanReward2.id, 3), PoolReward(rareCanReward3.id, 1)
                )
            )
        )
    ),
)

val arachnidPool = HuntPool("arachnid_pool", "<light_purple>Arachnids", spiderHuntList.map { it.id })

val personalWoolooEasy = Hunt(
    id = "wooloo_easy",
    name = "<light_purple>Female <white>Wooloo",
    difficulty = HuntDifficulties.EASY,
    huntFeature = HuntFeature(
        listOf("wooloo"), gender = HuntGender.FEMALE
    ),
    guaranteedRewardId = listOf(
        pokeballReward1.id, pebblesReward1.id, rareCanReward1.id
    ),
    amount = 1,
    timeLimitMinutes = 30,
    rewardPools = listOf(
        Pool(apricornPool), Pool(vitaminPool1)
    ),
    description = listOf(
        "<white>Find and capture a female Wooloo!"
    ),
    experience = 50,
    cost = 100
)

val personalZubatEasy = Hunt(
    id = "zubat_easy",
    name = "<light_purple>Zubat",
    difficulty = HuntDifficulties.EASY,
    huntFeature = HuntFeature(
        listOf("zubat")
    ),
    guaranteedRewardId = listOf(
        pokeballReward1.id, pebblesReward1.id, rareCanReward1.id
    ),
    rewardPools = listOf(Pool(apricornPool), Pool(vitaminPool1)),
    amount = 1,
    timeLimitMinutes = 30,
    description = listOf(
        "<white>Find and capture a Zubat!"
    ),
    experience = 50,
    cost = 100
)

val personalPsyduckMedium = Hunt(
    id = "psyduck_medium",
    name = "<light_purple>Psyduck",
    difficulty = HuntDifficulties.MEDIUM,
    huntFeature = HuntFeature(
        listOf("psyduck")
    ),
    guaranteedRewardId = listOf(
        pokeballReward2.id, pebblesReward2.id, rareCanReward1.id
    ),
    rewardPools = listOf(
        Pool(apricornPool), Pool(apricornPool), Pool(vitaminPool1), Pool(vitaminPool1)
    ),
    amount = 1,
    timeLimitMinutes = 60,
    description = listOf(
        "<white>Find and capture a Psyduck!"
    ),
    experience = 100,
    cost = 250
)

val personalPikachuMedium = Hunt(
    id = "pikachu_medium",
    name = "<light_purple>Pikachu",
    difficulty = HuntDifficulties.MEDIUM,
    huntFeature = HuntFeature(
        listOf("pikachu")
    ),
    guaranteedRewardId = listOf(
        pokeballReward2.id, pebblesReward2.id, rareCanReward1.id
    ),
    rewardPools = listOf(
        Pool(apricornPool), Pool(apricornPool), Pool(vitaminPool1), Pool(vitaminPool1)
    ),
    amount = 1,
    timeLimitMinutes = 60,
    description = listOf(
        "<white>Find and capture a Pikachu!"
    ),
    experience = 100,
    cost = 250
)

val personalWoolooHard = Hunt(
    id = "wooloo_hard",
    name = "3x Calm <light_purple>Female <white>Wooloo <green>[Friend Ball]",
    difficulty = HuntDifficulties.HARD,
    huntFeature = HuntFeature(
        listOf("wooloo"), nature = PokemonNatures.CALM, gender = HuntGender.FEMALE, ball = HuntBalls.FRIEND_BALL
    ),
    guaranteedRewardId = listOf(
        pokeballReward2.id, pebblesReward3.id, rareCanReward2.id
    ),
    rewardPools = listOf(Pool(apricornPool), Pool(vitaminPool2), Pool(vitaminPool2)),
    amount = 3,
    timeLimitMinutes = 90,
    description = listOf(
        "<white>Find and capture 10 Female Wooloo!",
        "<white>Must be caught in a <light_purple>Friend Ball<white> and have a <light_purple>Calm<white> nature!"
    ),
    experience = 250,
    cost = 500
)

val personalZubatHard = Hunt(
    id = "zubat_hard",
    name = "3x Jolly <light_purple>Zubat <green>[Ultra Ball]",
    difficulty = HuntDifficulties.HARD,
    huntFeature = HuntFeature(
        listOf("zubat"), nature = PokemonNatures.JOLLY, gender = HuntGender.ANY, ball = HuntBalls.ULTRA_BALL
    ),
    guaranteedRewardId = listOf(
        pokeballReward2.id, pebblesReward3.id, rareCanReward2.id
    ),
    rewardPools = listOf(Pool(apricornPool), Pool(vitaminPool2), Pool(vitaminPool2)),
    amount = 3,
    timeLimitMinutes = 90,
    description = listOf(
        "<white>Find and capture 10 Zubat!",
        "<white>Must be caught in a <light_purple>Ultra Ball<white> and have a <light_purple>Jolly<white> nature!"
    ),
    experience = 250,
    cost = 500
)


val personalStarterGenOneLegendary = Hunt(
    id = "starter_legendary",
    name = "<light_purple>Starter <white>Legendary",
    difficulty = HuntDifficulties.LEGENDARY,
    amount = 3,
    huntFeature = HuntFeature(
        listOf("bulbasaur", "charmander", "squirtle"), shiny = false, ball = HuntBalls.POKE_BALL
    ),
    guaranteedRewardId = listOf(
        pokeballReward3.id, pebblesReward4.id, rareCanReward3.id
    ),
    rewardPools = listOf(Pool(apricornPool), Pool(apricornPool), Pool(vitaminPool3), Pool(vitaminPool3)),
    timeLimitMinutes = 120,
    description = listOf(
        "<white>Find and capture any 3 Gen 1 <light_purple>Starter <white>Pokemon!",
        "<white>Must be caught in a <red>Poke<white>Ball!"
    ),
    experience = 500,
    cost = 1000
)

val personalStarterGenTwoLegendary = Hunt(
    id = "starter_legendary_gen_two",
    name = "<light_purple>Starter <white>Legendary",
    difficulty = HuntDifficulties.LEGENDARY,
    amount = 3,
    huntFeature = HuntFeature(
        listOf("chikorita", "cyndaquil", "totodile"), shiny = false, ball = HuntBalls.POKE_BALL
    ),
    guaranteedRewardId = listOf(
        pokeballReward3.id, pebblesReward4.id, rareCanReward3.id
    ),
    rewardPools = listOf(Pool(apricornPool), Pool(apricornPool), Pool(vitaminPool3), Pool(vitaminPool3)),
    timeLimitMinutes = 120,
    description = listOf(
        "<white>Find and capture any 3 Gen 2 <light_purple>Starter <white>Pokemon!",
        "<white>Must be caught in a <red>Poke<white>Ball!"
    ),
    experience = 500,
    cost = 1000
)

val personalCharmanderGodlike = Hunt(
    id = "charmander_godlike",
    name = "<light_purple>Charmander",
    difficulty = HuntDifficulties.GODLIKE,
    huntFeature = HuntFeature(
        listOf("charmander"), shiny = true, ball = HuntBalls.ULTRA_BALL
    ),
    guaranteedRewardId = listOf(
        pokeballReward3.id, pebblesReward5.id, pebblesReward3.id, rareCanReward3.id
    ),
    rewardPools = listOf(
        Pool(apricornPool),
        Pool(apricornPool),
        Pool(apricornPool),
        Pool(vitaminPool3),
        Pool(vitaminPool3),
        Pool(vitaminPool3),
        Pool(vitaminPool3)
    ),
    timeLimitMinutes = 120,
    description = listOf(
        "<white>Find and capture a <light_purple>Shiny <white>Charmander!",
        "<white>Must be caught in a <light_purple>Ultra Ball!"
    ),
    experience = 1000,
    cost = 2000
)

val personalBulbasaurGodlike = Hunt(
    id = "bulbasaur_godlike",
    name = "<light_purple>Bulbasaur",
    difficulty = HuntDifficulties.GODLIKE,
    huntFeature = HuntFeature(
        listOf("bulbasaur"), shiny = true, ball = HuntBalls.ULTRA_BALL
    ),
    guaranteedRewardId = listOf(
        pokeballReward3.id, pebblesReward5.id, pebblesReward3.id, rareCanReward3.id
    ),
    rewardPools = listOf(
        Pool(apricornPool),
        Pool(apricornPool),
        Pool(apricornPool),
        Pool(vitaminPool3),
        Pool(vitaminPool3),
        Pool(vitaminPool3),
        Pool(vitaminPool3)
    ),
    timeLimitMinutes = 120,
    description = listOf(
        "<white>Find and capture a <light_purple>Shiny <white>Bulbasaur!",
        "<white>Must be caught in a <light_purple>Ultra Ball!"
    ),
    experience = 1000,
    cost = 2000
)

val personalHuntList = listOf(
    personalWoolooEasy,
    personalZubatEasy,
    personalPsyduckMedium,
    personalPikachuMedium,
    personalWoolooHard,
    personalZubatHard,
    personalStarterGenOneLegendary,
    personalStarterGenTwoLegendary,
    personalCharmanderGodlike,
    personalBulbasaurGodlike
)