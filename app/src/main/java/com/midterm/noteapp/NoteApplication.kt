package com.midterm.noteapp

import android.app.Application
import com.midterm.noteapp.data.NoteRoomDatabase

class NoteApplication : Application() {
    val database: NoteRoomDatabase by lazy {
        NoteRoomDatabase.getDatabase(this)
    }
}