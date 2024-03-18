package tech.sethi.pebbles.cobbledhunters.hunt.type

import com.cobblemon.mod.common.pokemon.Pokemon
import tech.sethi.pebbles.cobbledhunters.CobbledHunters

data class HuntFeature(
    val species: List<String>? = listOf(),
    val type: PokemonTypes? = null,
    val levelRange: LevelRange = LevelRange(),
    val shiny: Boolean = false,
    val nature: PokemonNatures? = null,
    val gender: HuntGender = HuntGender.ANY,
    val ability: String? = null,
    val ball: HuntBalls? = HuntBalls.ANY,
    val form: String? = null,
) {
    fun checkRequirement(pokemon: Pokemon): Boolean {
        val species = pokemon.species.resourceIdentifier.path
//        CobbledHunters.LOGGER.info("Checking requirement for $species and required species: ${this.species}")
        if (this.species != null && !this.species.contains(species)) {
            return false
        }
//        CobbledHunters.LOGGER.info("Passed species check")
//        CobbledHunters.LOGGER.info("--------------------------")


        val pokemonTypes = listOf(pokemon.primaryType.name, pokemon.secondaryType?.name)
//        CobbledHunters.LOGGER.info("Pokemon types: ${pokemonTypes}" + " and required type: ${this.type}")
        if (this.type != null && !pokemonTypes.contains(this.type.name.toUpperCase())) {
            return false
        }
//        CobbledHunters.LOGGER.info("Passed type check")


//        CobbledHunters.LOGGER.info("Pokemon level: ${pokemon.level} and required level: ${this.levelRange}")
        if (pokemon.level !in this.levelRange.min..this.levelRange.max) {
            return false
        }

//        CobbledHunters.LOGGER.info("Passed level check")
        if (this.shiny && !pokemon.shiny) {
            return false
        }
//        CobbledHunters.LOGGER.info("Passed shiny check")

        val pokemonNature = pokemon.nature.name.path.toUpperCase()
//        CobbledHunters.LOGGER.info("Pokemon nature: $pokemonNature and required nature: ${this.nature?.name}")
        if (this.nature != null && this.nature.name.toUpperCase() != pokemonNature) {
            return false
        }
//        CobbledHunters.LOGGER.info("Passed nature check")

        val gender = pokemon.gender.name.toUpperCase()
//        CobbledHunters.LOGGER.info("Pokemon Gender: $gender and required gender: ${this.gender.name}")
        if (this.gender != HuntGender.ANY && this.gender.name != gender) {
            return false
        }

        val ability = pokemon.ability.name
//        CobbledHunters.LOGGER.info("Pokemon Ability: $ability and required ability: ${this.ability}")
        if (this.ability != null && this.ability != ability) {
            return false
        }

        val caughtBall = pokemon.caughtBall.item().pokeBall.name.toString()
        val requiredBall = pokeballMap[this.ball]
//        CobbledHunters.LOGGER.info("Pokemon Ball: $caughtBall and required ball: $requiredBall")
        if (this.ball != null && requiredBall != null && caughtBall != requiredBall) {
            return false
        }

        val form = pokemon.form.name
//        CobbledHunters.LOGGER.info("Pokemon Form: $form and required form: ${this.form}")
        if (this.form != null && this.form != form) {
            return false
        }

//        CobbledHunters.LOGGER.info("Passed all checks")

        return true
    }
}

data class LevelRange(
    val min: Int = 1, val max: Int = 100
)

enum class HuntGender {
    MALE, FEMALE, ANY
}

enum class PokemonTypes {
    NORMAL, FIRE, WATER, GRASS, ELECTRIC, ICE, FIGHTING, POISON, GROUND, FLYING, PSYCHIC, BUG, ROCK, GHOST, DARK, DRAGON, STEEL, FAIRY
}

enum class PokemonNatures {
    HARDY, LONELY, BRAVE, ADAMANT, NAUGHTY, BOLD, DOCILE, RELAXED, IMPISH, LAX, TIMID, HASTY, SERIOUS, JOLLY, NAIVE, MODEST, MILD, QUIET, BASHFUL, RASH, CALM, GENTLE, SASSY, CAREFUL, QUIRKY
}

val grassTypeFeature = HuntFeature(
    type = PokemonTypes.GRASS, levelRange = LevelRange(1, 100), shiny = false, gender = HuntGender.MALE, ball = HuntBalls.GREAT_BALL
)

enum class HuntBalls {
    ANY, POKE_BALL, CITRINE_BALL, VERDANT_BALL, AZURE_BALL, ROSEATE_BALL, SLATE_BALL, PREMIER_BALL, GREAT_BALL, ULTRA_BALL, SAFARI_BALL, FAST_BALL, LEVEL_BALL, LURE_BALL, HEAVY_BALL, LOVE_BALL, FRIEND_BALL, MOON_BALL, SPORT_BALL, PARK_BALL, NET_BALL, DIVE_BALL, NEST_BALL, REPEAT_BALL, TIMER_BALL, LUXURY_BALL, DUSK_BALL, HEAL_BALL, QUICK_BALL, DREAM_BALL, CHERISH_BALL, BEAST_BALL, MASTER_BALL
}

val pokeballMap = mapOf(
    HuntBalls.POKE_BALL to "cobblemon:poke_ball",
    HuntBalls.CITRINE_BALL to "cobblemon:citrine_ball",
    HuntBalls.VERDANT_BALL to "cobblemon:verdant_ball",
    HuntBalls.AZURE_BALL to "cobblemon:azure_ball",
    HuntBalls.ROSEATE_BALL to "cobblemon:roseate_ball",
    HuntBalls.SLATE_BALL to "cobblemon:slate_ball",
    HuntBalls.PREMIER_BALL to "cobblemon:premier_ball",
    HuntBalls.GREAT_BALL to "cobblemon:great_ball",
    HuntBalls.ULTRA_BALL to "cobblemon:ultra_ball",
    HuntBalls.SAFARI_BALL to "cobblemon:safari_ball",
    HuntBalls.FAST_BALL to "cobblemon:fast_ball",
    HuntBalls.LEVEL_BALL to "cobblemon:level_ball",
    HuntBalls.LURE_BALL to "cobblemon:lure_ball",
    HuntBalls.HEAVY_BALL to "cobblemon:heavy_ball",
    HuntBalls.LOVE_BALL to "cobblemon:love_ball",
    HuntBalls.FRIEND_BALL to "cobblemon:friend_ball",
    HuntBalls.MOON_BALL to "cobblemon:moon_ball",
    HuntBalls.SPORT_BALL to "cobblemon:sport_ball",
    HuntBalls.PARK_BALL to "cobblemon:park_ball",
    HuntBalls.NET_BALL to "cobblemon:net_ball",
    HuntBalls.DIVE_BALL to "cobblemon:dive_ball",
    HuntBalls.NEST_BALL to "cobblemon:nest_ball",
    HuntBalls.REPEAT_BALL to "cobblemon:repeat_ball",
    HuntBalls.TIMER_BALL to "cobblemon:timer_ball",
    HuntBalls.LUXURY_BALL to "cobblemon:luxury_ball",
    HuntBalls.DUSK_BALL to "cobblemon:dusk_ball",
    HuntBalls.HEAL_BALL to "cobblemon:health_ball",
    HuntBalls.QUICK_BALL to "cobblemon:quick_ball",
    HuntBalls.DREAM_BALL to "cobblemon:dream_ball",
    HuntBalls.CHERISH_BALL to "cobblemon:cherish_ball",
    HuntBalls.BEAST_BALL to "cobblemon:beast_ball",
    HuntBalls.MASTER_BALL to "cobblemon:master_ball"
)