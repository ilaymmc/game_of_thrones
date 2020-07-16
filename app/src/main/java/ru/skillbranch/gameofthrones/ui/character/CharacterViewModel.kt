package ru.skillbranch.gameofthrones.ui.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.repositories.RootRepository
import ru.skillbranch.gameofthrones.ui.houses.house.HouseViewModel
import ru.skillbranch.gameofthrones.utils.combineAndCompute

class CharacterViewModel(private val characterId: String) : ViewModel() {
    private val repository = RootRepository

    fun getCharacter(): LiveData<CharacterFull> =
        repository.findCharacterFullById(characterId)

}

class CharacterViewModelFactory(private val characterId: String): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterViewModel::class.java)) {
            return CharacterViewModel(characterId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}