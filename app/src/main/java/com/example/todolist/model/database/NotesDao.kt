package com.example.todolist.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.model.database.entity.ToDoNote
import kotlinx.coroutines.flow.Flow


@Dao
interface NotesDao {
    @Query("SELECT COUNT(*) FROM todonote")
    fun getNotesCount(): Int

    @Query("SELECT * FROM todonote ORDER BY id ASC")
    fun getAll(): Flow<List<ToDoNote>>

    @Insert
    fun insertAll(vararg notes: ToDoNote)

    @Update
    fun updateAll(vararg notes: ToDoNote)

    @Delete
    fun deleteAll(vararg notes: ToDoNote)

    @Query("DELETE FROM todonote WHERE id = :id")
    fun deleteById(id: Int)

    @Query("UPDATE todonote SET flag = :newFlag WHERE id = :id")
    fun updateFlagById(id: Int, newFlag: Boolean)

    @Query("UPDATE todonote SET title = :newTitle, text = :newText WHERE id = :id")
    fun updateDescriptionAndTitleById(id: Int, newTitle: String, newText: String)

    @Query("SELECT max(id)+1 FROM todonote")
    fun getNextId(): Int
}