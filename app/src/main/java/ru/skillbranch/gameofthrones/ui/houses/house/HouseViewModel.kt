package ru.skillbranch.gameofthrones.ui.houses.house

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.repositories.RootRepository
import ru.skillbranch.gameofthrones.utils.combineAndCompute

class HouseViewModel(private val houseName: String) : ViewModel() {
    private val repository = RootRepository
    private val queryStr = MutableLiveData("")

    fun getCharacters(): LiveData<List<CharacterItem>> {
        val characters: LiveData<List<CharacterItem>> = repository.findCharacters(houseName)
        return characters.combineAndCompute(queryStr) { list, query ->
            if (query.isEmpty()) {
                list
            } else {
                list.filter { it.name.contains(query, true) }
            }
        }
    }

    fun handleSearchQuery(str: String) {
        queryStr.value = str
    }
}

class HouseViewModelFactory(private val houseName: String): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HouseViewModel::class.java)) {
            return HouseViewModel(houseName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}