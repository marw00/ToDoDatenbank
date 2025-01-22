package de.hhn.myapplication.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.hhn.myapplication.database.dataclass.ToDoDataClass
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A dialog that allows the user to edit or create a ToDo entry.
 */
@Composable
fun EditToDoDialog(
    toDo: ToDoDataClass?,
    onDismiss: () -> Unit,
    onSave: (ToDoDataClass) -> Unit,
    onDelete: (ToDoDataClass) -> Unit
) {
    var name by remember { mutableStateOf(toDo?.name ?: "") }
    var priority by remember { mutableStateOf(toDo?.priority ?: "") }
    var endDate by remember { mutableStateOf(toDo?.endDate ?: "") }
    var description by remember { mutableStateOf(toDo?.description ?: "") }
    var isDatePickerVisible by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")//
    val priorityOptions = listOf("Low", "Normal", "High")

    //Create and display the AlertDialog
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (toDo == null) "Neuen Eintrag anlegen" else "Eintrag bearbeiten")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endDate.format(dateFormatter),
                    onValueChange = {},
                    label = { Text("Select EndDate") },
                    enabled = false,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDatePickerVisible = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Priority", style = MaterialTheme.typography.bodyLarge)
                    priorityOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (priority == option),
                                    onClick = { priority = option },
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = (priority == option),
                                onClick = null
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(text = option)
                        }
                    }
                }
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("description") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (isDatePickerVisible) {
                    DatePickerModal(
                        onDateSelected = { selectedDate ->
                            endDate = selectedDate?.let {
                                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it)
                            } ?: ""
                            isDatePickerVisible = false
                        },
                        onDismiss = { isDatePickerVisible = false }
                    )
                }
            }
        },
        confirmButton = {
            Log.d("Dialog",priority)
            Button(onClick = {
                val updatedToDo =
                    ToDoDataClass(
                        id = toDo?.id ?: 0,
                        name = name,
                        priority = priority,
                        endDate = endDate,
                        description = description,
                        status = toDo?.status ?: 0
                    )
                    onSave(updatedToDo)
            }) {
                Text("Speichern")
            }
        },
        dismissButton = {
            if (toDo != null) {
                Button(onClick = { onDelete(toDo) }) {
                    Text("Löschen")
                }
            }
        }
    )
}






