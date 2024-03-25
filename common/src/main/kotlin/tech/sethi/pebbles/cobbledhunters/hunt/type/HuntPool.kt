package tech.sethi.pebbles.cobbledhunters.hunt.type

data class HuntPool(
    val id: String, val name: String, val huntIds: List<String>
)

val spiderFeatures = listOf(
    HuntFeature(listOf("spinarak"), ball = HuntBalls.POKE_BALL),
    HuntFeature(listOf("ariados"), ball = HuntBalls.GREAT_BALL),
    HuntFeature(listOf("joltik"), gender = HuntGender.FEMALE, ball = HuntBalls.FRIEND_BALL),
    HuntFeature(listOf("galvantula"), gender = HuntGender.FEMALE, ball = HuntBalls.LOVE_BALL),
    HuntFeature(listOf("dewpider"), gender = HuntGender.MALE, ball = HuntBalls.ULTRA_BALL),
    HuntFeature(
        listOf("araquanid"), ball = HuntBalls.QUICK_BALL
    )
)

val spiderHuntList = listOf(
    GlobalHunt(
        id = "spinarak",
        name = "20x <light_purple>Spinarak <green>[Poké Ball]",
        huntFeature = spiderFeatures[0],
        guaranteedRewardId = listOf(),
        rewardPools = listOf(Pool(pebblesPool), Pool(pokeballPool), Pool(apricornPool), Pool(apricornPool)),
        extraRankingRewards = listOf(
            RankingReward(
                rank = 1,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool)),
                experience = 50
            ), RankingReward(
                rank = 2,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool)),
                experience = 30
            ), RankingReward(
                rank = 3,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool)),
                experience = 20
            )
        ),
        experience = 50,
        amount = 30,
        cost = 50,
        timeLimitMinutes = 60
    ), GlobalHunt(
        id = "ariados",
        name = "10x <light_purple>Ariados <green>[Great Ball]",
        huntFeature = spiderFeatures[1],
        guaranteedRewardId = listOf(),
        rewardPools = listOf(
            Pool(pokeballPool), Pool(pebblesPool), Pool(rareCandyPool), Pool(potionPool), Pool(apricornPool)
        ),
        extraRankingRewards = listOf(
            RankingReward(
                rank = 1,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool), Pool(vitaminPool1), Pool(vitaminPool1)),
                experience = 100
            ), RankingReward(
                rank = 2,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool), Pool(vitaminPool1)),
                experience = 80
            ), RankingReward(
                rank = 3,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool)),
                experience = 50
            )
        ),
        experience = 200,
        amount = 10,
        cost = 100,
        timeLimitMinutes = 80
    ), GlobalHunt(
        id = "joltik",
        name = "20x <light_purple>Female <yellow>Joltik</yellow> <green>[Friend Ball]",
        huntFeature = spiderFeatures[2],
        guaranteedRewardId = listOf(),
        rewardPools = listOf(
            Pool(pebblesPool), Pool(pokeballPool), Pool(rareCandyPool), Pool(potionPool), Pool(vitaminPool1)
        ),
        extraRankingRewards = listOf(
            RankingReward(
                rank = 1,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool), Pool(vitaminPool1), Pool(vitaminPool1)),
                experience = 200
            ), RankingReward(
                rank = 2,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool), Pool(vitaminPool1)),
                experience = 150
            ), RankingReward(
                rank = 3,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool)),
                experience = 100
            )
        ),
        amount = 20,
        experience = 300,
        cost = 200,
        timeLimitMinutes = 80
    ), GlobalHunt(
        id = "galvantula",
        name = "15x <light_purple>Female Galvantula <green>[Love Ball]",
        huntFeature = spiderFeatures[3],
        guaranteedRewardId = listOf(),
        rewardPools = listOf(
            Pool(pokeballPool),
            Pool(pebblesPool),
            Pool(rareCandyPool),
            Pool(potionPool),
            Pool(vitaminPool1),
            Pool(vitaminPool1),
            Pool(vitaminPool1)
        ),
        extraRankingRewards = listOf(
            RankingReward(
                rank = 1,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool), Pool(vitaminPool1), Pool(vitaminPool1)),
                experience = 300
            ), RankingReward(
                rank = 2,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool), Pool(vitaminPool1)),
                experience = 200
            ), RankingReward(
                rank = 3,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool)),
                experience = 150
            )
        ),
        amount = 15,
        experience = 400,
        cost = 300,
        timeLimitMinutes = 100
    ), GlobalHunt(
        id = "dewpider",
        name = "<aqua>Male <light_purple>Dewpider <green>[Ultra Ball]",
        huntFeature = spiderFeatures[4],
        guaranteedRewardId = listOf(),
        rewardPools = listOf(
            Pool(pokeballPool),
            Pool(pebblesPool),
            Pool(rareCandyPool),
            Pool(potionPool),
            Pool(vitaminPool2),
        ),
        extraRankingRewards = listOf(
            RankingReward(
                rank = 1,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool), Pool(vitaminPool2), Pool(vitaminPool2)),
                experience = 250
            ), RankingReward(
                rank = 2,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool), Pool(vitaminPool2)),
                experience = 150
            ), RankingReward(
                rank = 3,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool)),
                experience = 100
            )
        ),
        amount = 20,
        experience = 300,
        cost = 200,
        timeLimitMinutes = 90
    ), GlobalHunt(
        id = "araquanid",
        name = "40x <light_purple>Araquanid <green>[Quick Ball]",
        huntFeature = spiderFeatures[5],
        guaranteedRewardId = listOf(),
        rewardPools = listOf(
            Pool(pokeballPool),
            Pool(pebblesPool),
            Pool(pebblesPool),
            Pool(rareCandyPool),
            Pool(rareCandyPool),
            Pool(potionPool),
            Pool(potionPool),
            Pool(vitaminPool2),
            Pool(vitaminPool2),
            Pool(vitaminPool3)
        ),
        extraRankingRewards = listOf(
            RankingReward(
                rank = 1,
                guaranteedRewardId = listOf(pebblesReward3.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool), Pool(vitaminPool3), Pool(vitaminPool3)),
                experience = 500
            ), RankingReward(
                rank = 2,
                guaranteedRewardId = listOf(pebblesReward2.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool), Pool(vitaminPool3)),
                experience = 300
            ), RankingReward(
                rank = 3,
                guaranteedRewardId = listOf(pebblesReward1.id),
                rewardPools = listOf(Pool(pokeballPool), Pool(apricornPool)),
                experience = 200
            )
        ),
        amount = 40,
        experience = 600,
        cost = 500,
        timeLimitMinutes = 150
    )
)

val arachnidPool = HuntPool("arachnid_pool", "<light_purple>Arachnids Hunt", spiderHuntList.map { it.id })

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

val personalGeodudeDefeatEasy = Hunt(
    id = "geodude_defeat_easy",
    name = "<green>Defeat 5x <gray>Geodude</gray> in battle",
    difficulty = HuntDifficulties.EASY,
    huntFeature = HuntFeature(
        listOf("geodude"), goal = HuntGoals.DEFEAT
    ),
    guaranteedRewardId = listOf(
        pokeballReward1.id, pebblesReward1.id, rareCanReward1.id
    ),
    amount = 5,
    timeLimitMinutes = 30,
    rewardPools = listOf(
        Pool(apricornPool), Pool(vitaminPool1)
    ),
    description = listOf(
        "<white>Battle and defeat 5 Geodude!"
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

val personalCaterpieKillEasy = Hunt(
    id = "caterpie_kill_easy",
    name = "<yellow>Kill 5x <green>Caterpie",
    difficulty = HuntDifficulties.EASY,
    huntFeature = HuntFeature(
        listOf("caterpie"), goal = HuntGoals.KILL
    ),
    guaranteedRewardId = listOf(
        pokeballReward1.id, pebblesReward1.id, rareCanReward1.id
    ),
    rewardPools = listOf(Pool(apricornPool), Pool(vitaminPool1)),
    amount = 5,
    timeLimitMinutes = 30,
    description = listOf(
        "<white>Kill 5 Caterpie!"
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
    name = "<gold>Shiny <red>Charmander <green>[Ultra Ball]",
    difficulty = HuntDifficulties.GODLIKE,
    huntFeature = HuntFeature(
        listOf("charmander"), shiny = true, ball = HuntBalls.ULTRA_BALL
    ),
    guaranteedRewardId = listOf(
        pokeballReward3.id, pebblesReward5.id, pebblesReward4.id, rareCanReward3.id
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
    personalGeodudeDefeatEasy,
    personalZubatEasy,
    personalCaterpieKillEasy,
    personalPsyduckMedium,
    personalPikachuMedium,
    personalWoolooHard,
    personalZubatHard,
    personalStarterGenOneLegendary,
    personalStarterGenTwoLegendary,
    personalCharmanderGodlike,
    personalBulbasaurGodlike
)