package com.example.todolist

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.todolist.model.database.AppDatabase
import com.example.todolist.model.database.entity.ToDoNote
import com.example.todolist.ui.theme.LightBlue
import com.example.todolist.ui.theme.ToDoListTheme
import com.example.todolist.viewModel.ListInterface
import com.example.todolist.viewModel.ListInterface.DataObject
import com.example.todolist.viewModel.ListViewModel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    companion object {
        var roomDb: AppDatabase? = null
        fun getDatabase(): AppDatabase? {
            return roomDb
        }
    }

    private val viewModel: ListViewModel by viewModels()
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomDb = Room.databaseBuilder(
            super.getApplicationContext(),
            AppDatabase::class.java, "room_notes_data_base_test37.db"
        ).allowMainThreadQueries().build()
        val dao = roomDb!!.notesDao()
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "ToDoList") {
                    composable("ToDoList") {
                        ToDoListScreen(viewModel, navController)
                    }
                    composable(
                        "ItemInfo" + "/{noteId}",
                        arguments = listOf(navArgument("noteId") {
                            type = NavType.IntType
                        }),
                    ) { stackEntry ->
                        val noteId = stackEntry.arguments!!.getInt("noteId")
                        ToDoListItemInfo(viewModel, navController, noteId)
                    }
                    composable("AddNewItem") {
                        ToDoListAddNewItem(viewModel, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun ToDoListScreen(viewModel: ListInterface, navController: NavHostController) {
    var notesState = viewModel.data.collectAsState()
    var notes = notesState.value

    var totalState = viewModel.total.collectAsState()
    var total = totalState.value

    var checkedState = viewModel.checked.collectAsState()
    var checked = checkedState.value

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title= {
                    Text("To do list",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold)
                },
                actions={
                    Text("Total: " + total.toString() + " - Checked: " + checked.toString(),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 10.dp))
                },
                colors= TopAppBarDefaults.topAppBarColors(containerColor = LightBlue,
                    titleContentColor = Color.White, actionIconContentColor = Color.White)
            )
        },
        bottomBar = {
            BottomAppBar (containerColor = Color.Transparent)  {
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp),
                    shape = RoundedCornerShape(10),
                    colors = ButtonDefaults.buttonColors(Color.White),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 3.dp
                    ),
                    onClick = {navController.navigate("AddNewItem"){popUpTo("ToDoList")} }){
                    Text("Add", fontSize = 22.sp, color = Color.Black)
                }
            }
        }
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(3.dp)
        ){
            itemsIndexed(
                notes
            ){index, item ->

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = item.flag,
                            onCheckedChange = { viewModel.changeCheckbox(it, item.id) },
                            colors = CheckboxDefaults.colors(LightBlue)
                        )

                        if (item.flag == true) {
                            Text(
                                text = item.title,
                                fontSize = 22.sp,
                                textDecoration = TextDecoration.LineThrough,
                                modifier = Modifier.clickable{navController.navigate("ItemInfo" + "/${item.id}"){popUpTo("ToDoList")}}
                            )
                        } else {
                            Text(
                                text = item.title,
                                fontSize = 22.sp,
                                textDecoration = TextDecoration.None,
                                modifier = Modifier.clickable{navController.navigate("ItemInfo" + "/${item.id}"){popUpTo("ToDoList")}}
                            )
                        }
                    }
                    IconButton(onClick = {viewModel.deleteItem(item.id)}) {
                        Icon(Icons.Filled.Delete, contentDescription = "Удалить заметку")
                    }
                }
            }
        }
    }
}

@Composable
fun ToDoListItemInfo(viewModel: ListInterface, navController: NavHostController, noteId: Int) {
    //val list = Dependencies.roomDb.notesDao().getAll()
    var notesState = viewModel.data.collectAsState()
    var notes = notesState.value

    var newHeader by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }

    val configuration = LocalConfiguration.current
    //val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val buttonSizeWidth = screenWidth / 2 - 10.dp

    var currentNoteIndex = 0

    var countElements=0
    for (n in notes){
        if (n.id == noteId) {
            currentNoteIndex = countElements
        }
        countElements++

    }

    newHeader = notes[currentNoteIndex].title
    newDescription = notes[currentNoteIndex].detailInformation

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            CenterAlignedTopAppBar(
                title= {
                    Text("Note description",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {navController.navigate("ToDoList"){popUpTo("ToDoList")}}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                },
                colors= TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightBlue,
                    titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        bottomBar = {
            BottomAppBar (containerColor = Color.Transparent)  {
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                    Button(
                        modifier = Modifier
                            .shadow(3.dp)
                            .width(buttonSizeWidth),
                        shape = RoundedCornerShape(10),
                        colors = ButtonDefaults.buttonColors(Color.White),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 3.dp
                        ),
                        onClick = {navController.navigate("ToDoList"){popUpTo("ToDoList")}}) {
                        Text("Cancel", fontSize = 22.sp, color = Color.Black)
                    }
                    Button(
                        modifier = Modifier
                            .shadow(3.dp)
                            .width(buttonSizeWidth),
                        shape = RoundedCornerShape(10),
                        colors = ButtonDefaults.buttonColors(Color.White),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 3.dp
                        ),
                        onClick = {
                            viewModel.editItem(newHeader, newDescription, noteId)
                            navController.navigate("ToDoList"){popUpTo("ToDoList")}
                        }) {
                        Text("Save changes", fontSize = 22.sp, color = Color.Black)
                    }
                }
            }
        }
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ){
            Text (
                text = "Заголовок",
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            TextField(
                value = newHeader,
                placeholder = { Text(text = "Введите заголовок", fontSize = 22.sp) },
                onValueChange = { newHeader=it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 10.dp, end = 10.dp),
                minLines = 1,
                maxLines = 1,
                textStyle = TextStyle.Default.copy(fontSize = 22.sp)
            )
            Text (
                text = "Описание",
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            TextField(
                value = newDescription,
                placeholder = { Text(text = "Введите описание", fontSize = 22.sp) },
                onValueChange = { newDescription=it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 10.dp, end = 10.dp),
                minLines = 10,
                maxLines = 10,
                textStyle = TextStyle.Default.copy(fontSize = 22.sp)
            )

        }
    }
}

@Composable
fun ToDoListAddNewItem(viewModel: ListInterface, navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val buttonSizeWidth = screenWidth / 2 - 10.dp
    //val buttonSizeHeight = (screenHeight - 3.dp * 7 - 60.dp ) / 5

    var newHeader by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            CenterAlignedTopAppBar(
                title= {
                    Text("Add note",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {navController.navigate("ToDoList"){popUpTo("ToDoList")}}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                },
                colors= TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightBlue,
                    titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        bottomBar = {
            BottomAppBar (containerColor = Color.Transparent)  {
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                    Button(
                        modifier = Modifier
                            .shadow(3.dp)
                            .width(buttonSizeWidth),
                        shape = RoundedCornerShape(10),
                        colors = ButtonDefaults.buttonColors(Color.White),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 3.dp
                        ),
                        onClick = {navController.navigate("ToDoList"){popUpTo("ToDoList")}}) {
                        Text("Cancel", fontSize = 22.sp, color = Color.Black)
                    }
                    Button(
                        modifier = Modifier
                            .shadow(3.dp)
                            .width(buttonSizeWidth),
                        shape = RoundedCornerShape(10),
                        colors = ButtonDefaults.buttonColors(Color.White),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 3.dp
                        ),
                        onClick = {
                            viewModel.addItem(newHeader, newDescription)
                            navController.navigate("ToDoList"){popUpTo("ToDoList")}
                        }) {
                        Text("Add", fontSize = 22.sp, color = Color.Black)
                    }
                }
            }
        }
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ){
            Text (
                text = "Заголовок",
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            TextField(
                value = newHeader,
                placeholder = { Text(text = "Введите заголовок", fontSize = 22.sp) },
                onValueChange = { newHeader=it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 10.dp, end = 10.dp),
                minLines = 1,
                maxLines = 1,
                textStyle = TextStyle.Default.copy(fontSize = 22.sp)
            )
            Text (
                text = "Описание",
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            TextField(
                value = newDescription,
                placeholder = { Text(text = "Введите описание", fontSize = 22.sp) },
                onValueChange = { newDescription=it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 10.dp, end = 10.dp),
                minLines = 10,
                maxLines = 10,
                textStyle = TextStyle.Default.copy(fontSize = 22.sp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    //ToDoListTheme {
        //Greeting("Android")
    //}
}