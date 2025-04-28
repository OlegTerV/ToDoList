package com.example.todolist.viewModel

import com.example.todolist.MainActivity
import kotlinx.coroutines.flow.StateFlow

interface ListInterface {

    var data: StateFlow<ArrayList<DataObject>>

    data class DataObject(
        var id: Int,
        var title: String,
        var detailInformation: String,
        var flag: Boolean
    )

    var total:  StateFlow<Int>
    var checked:  StateFlow<Int>

    fun countCheckedNotes (elements: ArrayList<DataObject>): Int

    fun changeCheckbox (type: Boolean, index: Int)

    fun deleteItem (index: Int)

    fun addItem (header: String, description: String)

    fun editItem (header: String, description: String, index: Int)
}