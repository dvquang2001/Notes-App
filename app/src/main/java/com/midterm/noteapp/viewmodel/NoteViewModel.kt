package com.midterm.noteapp.viewmodel

import androidx.lifecycle.*
import com.midterm.noteapp.data.Note
import com.midterm.noteapp.data.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {

    val notesByIdDesc: Flow<List<Note>> = noteDao.getNotesByIdDesc()
    val notesByIdAsc: Flow<List<Note>> = noteDao.getNotesByIdAsc()
    val notesByTitleDesc: Flow<List<Note>> = noteDao.getNotesByTitleDesc()
    val notesByTitleAsc: Flow<List<Note>> = noteDao.getNotesByTitleAsc()
    val notesFromRecycleBin: Flow<List<Note>> = noteDao.getNotesFromRecycleBin()

    private fun insert(note: Note) {
        viewModelScope.launch {
            noteDao.insert(note)
        }
    }

    private fun getNewNoteEntry(title: String, content: String): Note {
        return Note(title = title, content = content)
    }

    fun addNewNote(title: String, content: String) {
        val newNote = getNewNoteEntry(title, content)
        insert(newNote)
    }

    fun isEntryValid(title: String, content: String): Boolean {
        if (title.isBlank() || content.isBlank()) {
            return false
        }
        return true
    }

    fun retrieveNote(id: Int): Flow<Note> {
        return noteDao.getNote(id)
    }

    private fun update(note: Note) {
        viewModelScope.launch {
            noteDao.update(note)
        }
    }

    fun updateNote(
        noteId: Int,
        noteTitle: String,
        noteContent: String
    ) {
        val updatedNote = Note(id = noteId, title = noteTitle, content = noteContent)
        update(updatedNote)
    }

    fun removeNote(note: Note) {
        val removeNote = note.copy(isDelete = true)
        update(removeNote)
    }

    private fun delete(note: Note) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }

    fun deleteNote(note: Note) {
        delete(note)
    }

    fun restoreNote(note: Note) {
        val newNote = note.copy(isDelete = false)
        update(newNote)
    }


}

class NoteViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}