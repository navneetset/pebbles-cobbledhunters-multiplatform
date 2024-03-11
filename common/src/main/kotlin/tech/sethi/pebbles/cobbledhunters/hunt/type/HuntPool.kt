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
        )
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
        )
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
        )
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
        )
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

