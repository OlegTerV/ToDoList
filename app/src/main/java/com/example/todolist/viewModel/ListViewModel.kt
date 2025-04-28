package com.example.todolist.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.MainActivity
import com.example.todolist.model.database.entity.ToDoNote
import com.example.todolist.viewModel.ListInterface.DataObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewModel: ViewModel(), ListInterface {
    val notesDao = MainActivity.getDatabase()!!.notesDao()

    var _data = MutableStateFlow<ArrayList<DataObject>>(arrayListOf())
    override var data = _data.asStateFlow()

    var _total = MutableStateFlow<Int>(0)
    var _checked = MutableStateFlow<Int>(0)

    override var total = _total.asStateFlow()
    override var checked = _checked.asStateFlow()

    init {/*
        notesDao.insertAll(
            ToDoNote(0, "Wash the dishes", "More info about dishes", true),
            ToDoNote(1, "Clone this repo", "Don't Show Toolbar", false),
            ToDoNote(2, "cd to the folder", "More info about cd to the folder", false),
            //ToDoNote(3, "npm install", "More info about npm install", false),
            ToDoNote(4, "npm run dev", "More info about npm run dev", false))*/

        viewModelScope.launch {
            notesDao.getAll().collect { notes ->
                _data.value = ArrayList(notes.map {
                    DataObject(it.id, it.title.toString(), it.text.toString(), it.flag!!)
                })
                _total.value = notesDao.getNotesCount()
                _checked.value = countCheckedNotes(_data.value)
            }
        }
    }

    override fun countCheckedNotes (elements: ArrayList<DataObject>): Int {
        var countChecked = 0;
        for (i in elements){
            if (i.flag==true) countChecked++
        }

        return countChecked
    }

    override fun changeCheckbox (type: Boolean, index: Int) {
        notesDao.updateFlagById(index, type)
    }

    override fun deleteItem (index: Int) {
        notesDao.deleteById(index)
    }

    override fun addItem (header: String, description: String){
        notesDao.insertAll(ToDoNote(nextId(), header, description, false))

    }

    override fun editItem (header: String, description: String, index: Int){
        notesDao.updateDescriptionAndTitleById(index, header, description)
    }

    fun nextId() : Int{
        return notesDao.getNextId()
    }

}