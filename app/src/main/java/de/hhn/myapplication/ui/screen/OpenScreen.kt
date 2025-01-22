package de.hhn.myapplication.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import de.hhn.myapplication.database.controller.ToDoController
import de.hhn.myapplication.database.dataclass.ToDoDataClass

/**
 * Displays the "Open ToDo's screen, showing all open ToDo items in a list.
 */
@Composable
fun OpenScreen(
    context: Context,
    navController: NavHostController = rememberNavController()
) {
    //Controller for interacting with ToDo database
    val toDoController = ToDoController(context)
    var toDoOpen by remember { mutableStateOf(toDoController.getAllToDosOpen()) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedToDo by remember { mutableStateOf<ToDoDataClass?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("dashboard") {
                popUpTo("dashboard") { inclusive = true }
            } }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to the dashboard"
                )
            }
            Text(
                text = "Open ToDo's",
                style = MaterialTheme.typography.titleLarge
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(toDoOpen) { toDo ->
                ToDoCard(
                    toDo = toDo,
                    onEditClick = {
                        selectedToDo = toDo
                        showEditDialog = true
                    },
                    onItemClick = {
                        toDoController.toggleToDo(toDo)
                        toDoOpen= toDoController.getAllToDosOpen()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        IconButton(onClick = { selectedToDo = null
            showEditDialog = true },modifier = Modifier
            .padding(10.dp)
            .background(Color.LightGray, shape = RectangleShape)
            .padding(8.dp)){
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create a new ToDo"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showEditDialog) {
        EditToDoDialog(
            toDo = selectedToDo,
            onDismiss = { showEditDialog = false },
            onSave = { toDo ->
                if (toDo.id == 0) {
                    toDoController.insertToDo(toDo)
                } else {
                    toDoController.updateToDo(toDo)
                }
                toDoOpen = toDoController.getAllToDosOpen()
                showEditDialog = false
            },
            onDelete = { toDo ->
                toDoController.deleteToDo(toDo.id)
                toDoOpen = toDoController.getAllToDosOpen()
                showEditDialog = false
            }
        )
    }
}