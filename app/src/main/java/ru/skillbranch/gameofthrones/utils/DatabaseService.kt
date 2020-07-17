package ru.skillbranch.gameofthrones.utils

import androidx.room.*
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.local.entities.RelativeCharacter
import java.util.stream.Collectors

@Database(entities = [House::class, Character::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun houseDao(): HouseDao
    abstract fun characterDao(): CharacterDao
    //abstract fun characterItemDao(): CharacterItemDao

}

object DatabaseService {
     val db : AppDatabase by lazy {
         Room.databaseBuilder(
             App.instance.applicationContext,
             AppDatabase::class.java, "gof-database"
         ).build()
     }

    fun closeDb() {
        db.close()
    }
}

@Dao
interface HouseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg houses: House)

    @Delete
    fun delete(house: House)

    @Query("SELECT * FROM house")
    fun getAllHouses(): List<House>

    @Query("SELECT COUNT(*) FROM house")
    fun getHousesCount(): Int

    @Query("SELECT * FROM house WHERE id = :id")
    fun get(id: String): House

}

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg characters: Character)

    @Delete
    fun delete(character: Character)

//    @Query("SELECT * FROM character")
//    fun getAllCharacters(): List<Character>

    @Query("SELECT COUNT(*) FROM character")
    fun getCharactersCount(): Int

    @Query("SELECT * FROM character WHERE id = :id")
    fun get(id: String): Character

    @Query("SELECT id, house, name, titles, aliases FROM character WHERE house = :house ORDER BY name")
    fun getCharactersForHouse(house: String): List<CharacterItem>

//    @Query("SELECT * FROM house WHERE name = name")
//    fun getAllPeopleWithFavoriteColor(name: String): List<Characters>
}

//@Dao
//interface CharacterFullDao {
//    @Query("SELECT id, name, words, born, died, titles, aliases, house, father, mother FROM character WHERE id = :id")
//
//    fun get(id: String): CharacterFull
//}

class Converters {
    @TypeConverter
    fun fromList(list: List<String?>): String {
        return list.joinToString(separator = "\t")
    }

    @TypeConverter
    fun stringToList(string: String): List<String> {
        return string.split("\t")
    }
}
