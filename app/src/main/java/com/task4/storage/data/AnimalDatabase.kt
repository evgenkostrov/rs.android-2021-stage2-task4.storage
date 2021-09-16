package com.task4.storage.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.task4.storage.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Animal::class], version = 1, exportSchema = false,)
abstract class AnimalDatabase : RoomDatabase() {

    abstract fun animalDao():AnimalDao

    class Callback @Inject constructor(
        private val database: Provider<AnimalDatabase>,
        @ApplicationScope private val applicationScope:CoroutineScope
    ): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val dao = database.get().animalDao()
            applicationScope.launch {
                dao.insertAnimal(Animal(name = "Sosiska", age = 1, breed = "cat"))
                dao.insertAnimal(Animal(name = "Sos", age = 2, breed = "cat"))
                dao.insertAnimal(Animal(name = "Ka", age = 3, breed = "bat"))
                dao.insertAnimal(Animal(name = "a", age = 2, breed = "bat"))
            }
        }
    }


}