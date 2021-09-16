package com.task4.storage.viewmodel


import androidx.hilt.Assisted
import androidx.lifecycle.*
import com.task4.storage.data.Animal
import com.task4.storage.data.AnimalDao
import com.task4.storage.ui.ADD_ANIMAL_RESULT_OK
import com.task4.storage.ui.UPDATE_ANIMAL_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalViewModel @Inject constructor(
    private val animalDao: AnimalDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {
    val searchQuery = state.getLiveData("searchQuery","")
    val sortOrder = MutableStateFlow(SortOrder.BY_NAME)
    val hideFavorite = MutableStateFlow(false)

    private val animalEventChannel = Channel<AnimalsEvent>()
    val animalsEvent=animalEventChannel.receiveAsFlow()

    private val animalFlow = combine(
        searchQuery.asFlow(),
        sortOrder,
        hideFavorite
    ) { query, sortOrder, hideFavorite ->
        Triple(query, sortOrder, hideFavorite)
    }
        .flatMapLatest { (query, sortOrder, hideFavorite)->
            animalDao.getAnimal(query, sortOrder, hideFavorite)
        }
    val animals = animalFlow.asLiveData()

    fun onAnimalSelected(animal: Animal)=viewModelScope.launch{
        animalEventChannel.send(AnimalsEvent.NavigateToEditAnimalScreen(animal))
    }
    fun onAnimalCheckedChanged(animal: Animal, isFavorite:Boolean) = viewModelScope.launch {
        animalDao.updateAnimal(animal.copy(favorite = isFavorite))
    }
    fun onAnimalSwiped(animal: Animal)=viewModelScope.launch {
        animalDao.deleteAnimal(animal)
    animalEventChannel.send(AnimalsEvent.ShowUndoDeleteMessage(animal))}

    fun onUndoDeleteClick(animal: Animal) =viewModelScope.launch {
        animalDao.insertAnimal(animal)
    }
    fun onAddNewAnimalClick() = viewModelScope.launch {
        animalEventChannel.send(AnimalsEvent.NavigateToAddAnimalScreen)
    }
    fun onAddUpdateResult(result: Int) {
        when(result){
            ADD_ANIMAL_RESULT_OK -> showAnimalSavedConfirmationMessage("Animal added")
            UPDATE_ANIMAL_RESULT_OK -> showAnimalSavedConfirmationMessage("Animal updated")
        }
    }
    private fun showAnimalSavedConfirmationMessage(text: String)=viewModelScope.launch {
        animalEventChannel.send(AnimalsEvent.ShowAnimalSavedConfirmationMessage(text))
    }

    sealed class AnimalsEvent{
        object NavigateToAddAnimalScreen:AnimalsEvent()
        data class NavigateToEditAnimalScreen(val animal: Animal): AnimalsEvent()
        data class ShowUndoDeleteMessage(val animal: Animal): AnimalsEvent()
        data class ShowAnimalSavedConfirmationMessage(val msg: String): AnimalsEvent()
    }
}

enum class SortOrder { BY_NAME, BY_AGE, BY_BREED, BY_DATE }