package ru.skillbranch.gameofthrones.data.remote.res
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.utils.lastUrlSegment

data class HouseRes(
    val url: String,
    val name: String,
    val region: String,
    val coatOfArms: String,
    val words: String,
    val titles: List<String> = listOf(),
    val seats: List<String> = listOf(),
    val currentLord: String,
    val heir: String,
    val overlord: String,
    val founded: String,
    val founder: String,
    val diedOut: String,
    val ancestralWeapons: List<String> = listOf(),
    val cadetBranches: List<Any> = listOf(),
    val swornMembers: List<String> = listOf()
) {
    val id
        get() = name.split(" ").dropLastWhile { it != "of" }.dropLast(1).last()
    val currentLordId
        get() = lastUrlSegment(currentLord)
    val heirId
        get() = lastUrlSegment(heir)
    val founderId
        get() = lastUrlSegment(founder)

    fun toHouse() =
        House(
            id = id,
            name = name,
            region = region,
            coatOfArms = coatOfArms,
            words = words,
            titles = titles,
            seats = seats,
            currentLord = currentLordId, //rel
            heir = heirId, //rel
            overlord = overlord,
            founded = founded,
            founder = founderId, //rel
            diedOut = diedOut,
            ancestralWeapons = ancestralWeapons
        )
}