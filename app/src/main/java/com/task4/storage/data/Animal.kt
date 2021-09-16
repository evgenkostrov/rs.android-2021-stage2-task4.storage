package com.task4.storage.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Parcelize
@Entity(tableName = "animal_table")
data class Animal(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val name: String,
    val age: Int,
    val breed: String,
    val favorite: Boolean = false,
    val created: Long = System.currentTimeMillis()
) : Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getDateInstance().format(created)
}