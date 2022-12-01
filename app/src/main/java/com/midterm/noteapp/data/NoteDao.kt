package com.midterm.noteapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM note WHERE is_delete = 0 ORDER BY id DESC")
    fun getNotesByIdDesc(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE is_delete = 0 ORDER BY id ASC")
    fun getNotesByIdAsc(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE is_delete = 0 ORDER BY title DESC")
    fun getNotesByTitleDesc(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE is_delete = 0 ORDER BY title ASC")
    fun getNotesByTitleAsc(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE is_delete = 1 ORDER BY id DESC")
    fun getNotesFromRecycleBin(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE id = :id")
    fun getNote(id: Int): Flow<Note>
}