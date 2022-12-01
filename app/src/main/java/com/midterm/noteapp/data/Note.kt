package com.midterm.noteapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "is_delete")
    val isDelete: Boolean = false,
    @ColumnInfo(name = "date")
    val date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
)