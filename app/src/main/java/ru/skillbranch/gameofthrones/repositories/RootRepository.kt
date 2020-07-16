package ru.skillbranch.gameofthrones.repositories

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.entities.*
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.utils.DatabaseService
import ru.skillbranch.gameofthrones.utils.NetworkService
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


const val PAGE_SIZE = 50

object RootRepository {

    private val scope = CoroutineScope(Dispatchers.Default)
    /**
     * Получение данных о всех домах из сети
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(result : (houses : List<HouseRes>) -> Unit) {
        var totalRes = mutableListOf<HouseRes>()
        fun loadPage(page : Int) {
            NetworkService.getJSONApi()
                .getAllHouses(page, PAGE_SIZE)
                .enqueue(object : Callback<List<HouseRes>> {
                    override fun onResponse(call: Call<List<HouseRes>>, response: Response<List<HouseRes>>) {
                        val res = response.body()
                        res?.let {
                            if (res.isEmpty()) {
                                result(totalRes)
                            } else {
                                totalRes.addAll(res)
                                loadPage(page + 1)
                            }
                        }
                    }
                    override fun onFailure(call: Call<List<HouseRes>>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
        }

        loadPage(1)
    }

    /**
     * Получение данных о требуемых домах по их полным именам из сети 
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouses(vararg houseNames: String, result : (houses : List<HouseRes>) -> Unit) {
        getAllHouses { res ->
            result(res.filter{ it.name in houseNames })
        }
    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouseWithCharacters(vararg houseNames: String, result : (houses : List<Pair<HouseRes, List<CharacterRes>>>) -> Unit) {
        val totalRes = mutableListOf<Pair<HouseRes, List<CharacterRes>>>()
        var neededCharacters = 0
        getAllHouses { res ->
            res.filter{ it.name in houseNames }.map { house ->
                val characters = mutableListOf<CharacterRes>()
                totalRes.add(house to characters)
                neededCharacters += house.swornMembers.size
                house.swornMembers.map { characterUrl ->
                NetworkService
                    .getJSONApi()
                    .getCharacter(characterUrl.split("/").last().toInt())
                    .enqueue(object : Callback<CharacterRes> {
                        override fun onResponse(call: Call<CharacterRes>, response: Response<CharacterRes>) {
                            val res = response.body()
                            res?.apply { houseId = house.id }?.let {
                                characters.add(it)
                            }
                            if ( --neededCharacters == 0)
                                result(totalRes)
                        }
                        override fun onFailure(call: Call<CharacterRes>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
            } }
        }
    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertHouses(houses : List<HouseRes>, complete: () -> Unit) = scope.launch {
        try {
            DatabaseService.db.houseDao().insertAll(
                *houses.map {
                    House(
                        id = it.id,
                        name = it.name,
                        region = it.region,
                        coatOfArms = it.coatOfArms,
                        words = it.words,
                        titles = it.titles,
                        seats = it.seats,
                        currentLord = it.currentLordId, //rel
                        heir = it.heirId, //rel
                        overlord = it.overlord,
                        founded = it.founded,
                        founder = it.founderId, //rel
                        diedOut = it.diedOut,
                        ancestralWeapons = it.ancestralWeapons
                    )
                }.toTypedArray()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        complete()
    }

    /**
     * Запись данных о пересонажах в DB
     * @param characters - Список персонажей (модель CharacterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertCharacters(characters : List<CharacterRes>, complete: () -> Unit) = scope.launch {
        DatabaseService.db.characterDao().insertAll(
            *characters.map {
                Character(
                    id = it.id,
                    name = it.name,
                    gender = it.gender,
                    culture = it.culture,
                    born = it.born,
                    died = it.died,
                    titles = it.titles,
                    aliases = it.aliases,
                    father = it.fatherId,
                    mother = it.motherId,
                    spouse = it.spouse,
                    houseId = it.houseId
                )
            }.toTypedArray()
        )
        complete()
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun dropDb(complete: () -> Unit) = scope.launch {
            DatabaseService.db.clearAllTables()
            complete()
        }


    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharactersByHouseName(name : String, result: (characters : List<CharacterItem>) -> Unit)
            = scope.launch {
        result(
            DatabaseService.db.characterDao().getCharactersForHouse(name)
        )
    }

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharacterFullById(id : String, result: (character : CharacterFull) -> Unit)
            = scope.launch {
        val character = DatabaseService.db.characterDao().get(id)
        val mother = character.mother.takeIf { it.isNotEmpty() }?.let {
                DatabaseService.db.characterDao().get(it)
        }
        val father = character.father.takeIf { it.isNotEmpty() }?.let {
                DatabaseService.db.characterDao().get(it)
        }
        val house = DatabaseService.db.houseDao().get(character.houseId)
        house?.let {
            result(
                CharacterFull(
                    id = character.id,
                    name = character.name,
                    words = house.words,
                    born = character.born,
                    died = character.died,
                    titles = character.titles,
                    aliases = character.aliases,
                    house = character.houseId,
                    father = father?.let {
                        RelativeCharacter(
                            id = it.id,
                            name = it.name,
                            house = it.houseId
                        )
                    },
                    mother = mother?.let {
                        RelativeCharacter(
                            id = it.id,
                            name = it.name,
                            house = it.houseId
                        )
                    }
                )
            )
        }
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    fun isNeedUpdate(result: (isNeed : Boolean) -> Unit) = scope.launch {
        result( DatabaseService.db.houseDao().getHousesCount() == 0 &&
                DatabaseService.db.characterDao().getCharactersCount() == 0)
    }


    // Coroutines versions
    suspend fun isNeedUpdate() : Boolean {
        return suspendCoroutine { cont ->
            isNeedUpdate {
                cont.resume(it)
            }
        }
    }


    suspend fun getAllHouses() : List<HouseRes> = suspendCoroutine { cont ->
            getAllHouses {
                cont.resume(it)
            }
    }

    suspend fun getNeedHouses(vararg houseNames: String) : List<HouseRes> = suspendCoroutine { cont ->
        getNeedHouses {
            cont.resume(it)
        }
    }

    suspend fun dropDb() : Unit = suspendCoroutine { cont ->
        dropDb {
            cont.resume(Unit)
        }
    }

    suspend fun insertHouses(houses : List<HouseRes>) : Unit = suspendCoroutine { cont ->
        insertHouses(houses) {
            cont.resume(Unit)
        }
    }

    suspend fun insertCharacters(characters : List<CharacterRes>) : Unit = suspendCoroutine { cont ->
        insertCharacters(characters) {
            cont.resume(Unit)
        }
    }

    suspend fun getNeedHouseWithCharacters(vararg houseNames: String)
            : List<Pair<HouseRes, List<CharacterRes>>> = suspendCoroutine { cont ->
        getNeedHouseWithCharacters(*houseNames) {
            cont.resume(it)
        }
    }

     suspend fun updateAll() {
         dropDb()
         val houses = getNeedHouses(*AppConfig.NEED_HOUSES)
         insertHouses(houses)
         val housesWitCharacter = getNeedHouseWithCharacters(*AppConfig.NEED_HOUSES)
         housesWitCharacter.forEach {
             (house, characters) ->
             insertCharacters(characters)
         }
    }

    fun findCharacters(houseName: String): LiveData<List<CharacterItem>> {
        val result = MutableLiveData<List<CharacterItem>>()
        findCharactersByHouseName(houseName) {
            result.postValue(it)
        }
        return result
    }

    fun findCharacterFullById(characterId: String): LiveData<CharacterFull> {
        val result = MutableLiveData<CharacterFull>()
        findCharacterFullById(characterId) {
            result.postValue(it)
        }
        return result
    }

    suspend fun needHouseWithCharacters(vararg houseNames: String): List<Pair<HouseRes, List<CharacterRes>>> {
        val result = mutableListOf<Pair<HouseRes, List<CharacterRes>>>()
        val houses = getNeedHouses(*houseNames)

        scope.launch {
            houses.forEach {house ->
                var i = 0
                val characters = mutableListOf<CharacterRes>()
                result.add(house to characters)
                house.swornMembers.forEach { character ->
                    launch {
//                        NetworkService
//                            .getJSONApi()
//                            .getCharacter(character.split("/").last().toInt())
//                            .apply {  houseId = house.id }
//                            .also { characters.add(it) }
                    }

                }

            }
        }.join()
        return result;
    }

}