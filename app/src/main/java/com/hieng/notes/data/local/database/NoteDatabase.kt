package com.hieng.notes.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hieng.notes.core.constant.DatabaseConst
import com.hieng.notes.data.local.dao.NoteDao
import com.hieng.notes.domain.model.Note

@Database(
    entities = [Note::class],
    version = DatabaseConst.NOTES_DATABASE_VERSION,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
}