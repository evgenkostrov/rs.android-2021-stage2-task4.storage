package com.task4.storage.viewmodel

import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task4.storage.data.Animal
import com.task4.storage.data.AnimalDao
import com.task4.storage.ui.ADD_ANIMAL_RESULT_OK
import com.task4.storage.ui.UPDATE_ANIMAL_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val animalDao: AnimalDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    private val addAnimalEventChannel = Channel<AddUpdateAnimalEvent>()
    val addAnimalsEvent=addAnimalEventChannel.receiveAsFlow()

    val animal = state.get<Animal>("animal")

    var animalName = state.get<String>("animalName") ?: animal?.name ?: ""
        set(value) {
            field = value
            state.set("animalName", value)
        }
    var animalAge = state.get<Int>("animalAge") ?: animal?.age ?: ""
        set(value) {
            field = value
            state.set("animalAge", value)
        }
    var animalBreed = state.get<String>("animalBreed") ?: animal?.breed ?: ""
        set(value) {
            field = value
            state.set("animalBreed", value)
        }

    fun onSaveClick() {
//        if (animalName.isBlank() || animalBreed.isBlank()) {
        if (animalAge == ""|| animalName.isBlank() || animalBreed.isBlank()) {
            showInvalidInputMessage("Property can not be empty !")
        } else {
            if (animal != null) {
                val updatedAnimal =
                    animal.copy(name = animalName, age = animalAge as Int, breed = animalBreed)
                updateAnimal(updatedAnimal)
            } else {
                val newAnimal =
                    Animal(name = animalName, age = animalAge as Int, breed = animalBreed)
                createAnimal(newAnimal)
            }
        }
    }
    private fun showInvalidInputMessage(text:String )=viewModelScope.launch {
        addAnimalEventChannel.send(AddUpdateAnimalEvent.ShowInvalidInputMessage(text))
    }
    private fun createAnimal(animal: Animal)=viewModelScope.launch {
        animalDao.insertAnimal(animal)
        addAnimalEventChannel.send(AddUpdateAnimalEvent.NavigateBackWithResult(ADD_ANIMAL_RESULT_OK))
    }
    private fun updateAnimal(animal: Animal)=viewModelScope.launch {
        animalDao.updateAnimal(animal)
        addAnimalEventChannel.send(AddUpdateAnimalEvent.NavigateBackWithResult(
            UPDATE_ANIMAL_RESULT_OK))
    }
    sealed class  AddUpdateAnimalEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddUpdateAnimalEvent()
        data class NavigateBackWithResult(val result: Int) : AddUpdateAnimalEvent()
    }




}