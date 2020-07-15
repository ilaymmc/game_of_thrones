package ru.skillbranch.gameofthrones.data.remote.res
import ru.skillbranch.gameofthrones.utils.lastUrlSegment


data class CharacterRes(
    val url: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    val titles: List<String> = listOf(),
    val aliases: List<String> = listOf(),
    val father: String,
    val mother: String,
    val spouse: String,
    val allegiances: List<String> = listOf(),
    val books: List<String> = listOf(),
    val povBooks: List<Any> = listOf(),
    val tvSeries: List<String> = listOf(),
    val playedBy: List<String> = listOf()
) {
    lateinit var houseId: String
    val id
        get() = lastUrlSegment(url)
    val motherId
        get() = lastUrlSegment(mother)
    val fatherId
        get() = lastUrlSegment(father)

}