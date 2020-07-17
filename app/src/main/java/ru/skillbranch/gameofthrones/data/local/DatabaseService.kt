package ru.skillbranch.gameofthrones.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Transaction
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Room
import androidx.room.RoomDatabase

import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House

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
    fun insertAll(vararg houses: House): List<Long>

    @Update
    fun update(vararg houses: House)

    @Delete
    fun delete(house: House)

    @Query("SELECT * FROM house")
    fun getAllHouses(): List<House>

    @Query("SELECT COUNT(*) FROM house")
    fun getHousesCount(): Int

    @Query("SELECT * FROM house WHERE id = :id")
    fun get(id: String): House

    @Transaction
    fun sync(houses: List<House>) {
        insertAll(*houses.toTypedArray())
            .mapIndexed { index, l -> if (l == -1L) houses[index] else null }
            .filterNotNull()
            .also {
                if (it.isNotEmpty()) update(*it.toTypedArray())
            }
    }
}

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg characters: Character): List<Long>

    @Update
    fun update(vararg character: Character)

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

    @Transaction
    fun sync(vararg characters: Character) {
        insertAll(*characters)
            .mapIndexed { index, l -> if (l == -1L) characters[index] else null}
            .filterNotNull()
            .also {
                if (it.isNotEmpty()) update(*it.toTypedArray())
            }
    }
}

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
