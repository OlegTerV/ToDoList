package com.example.todolist.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolist.model.database.entity.ToDoNote

@Database(
    entities = [
        ToDoNote::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}