package com.task4.storage.data

import androidx.room.*
import com.task4.storage.viewmodel.SortOrder
import kotlinx.coroutines.flow.Flow


@Dao
interface AnimalDao {

    fun getAnimal(query: String, sortOrder: SortOrder, hideFavorite: Boolean): Flow<List<Animal>> =
        when (sortOrder) {
            SortOrder.BY_NAME -> getAnimalSortedByName(query)
            SortOrder.BY_AGE -> getAnimalSortedByAge(query)
            SortOrder.BY_BREED -> getAnimalSortedByBreed(query)
            SortOrder.BY_DATE -> getAnimalSortedByDateCreated(query)
        }

    @Query("SELECT * FROM animal_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    fun getAnimalSortedByName(searchQuery: String): Flow<List<Animal>>

    @Query("SELECT * FROM animal_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY age ASC")
    fun getAnimalSortedByAge(searchQuery: String): Flow<List<Animal>>

    @Query("SELECT * FROM animal_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY breed ASC")
    fun getAnimalSortedByBreed(searchQuery: String): Flow<List<Animal>>

    @Query("SELECT * FROM animal_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY created ASC")
    fun getAnimalSortedByDateCreated(searchQuery: String): Flow<List<Animal>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAnimal(animal: Animal)

    @Update
    suspend fun updateAnimal(animal: Animal)

    @Delete
    suspend fun deleteAnimal(animal: Animal)
}