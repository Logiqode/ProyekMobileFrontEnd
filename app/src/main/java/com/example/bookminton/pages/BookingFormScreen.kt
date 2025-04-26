package com.example.bookminton.pages

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.shape.RoundedCornerShape
import java.time.LocalDate
import java.time.LocalTime
import com.example.bookminton.ui.theme.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.bookminton.data.Booking
import com.example.bookminton.navigation.Screen
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter
import com.example.bookminton.data.DataSingleton
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    navController: NavHostController,
    courtName: String,
    courtAddress: String
) {
    // State variables
    var courtNumber by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var startTime by remember { mutableStateOf(LocalTime.MIN) }
    var endTime by remember { mutableStateOf(LocalTime.MIN) }
    val showToast = remember { mutableStateOf(false) }
    val toastMessage = remember { mutableStateOf("") }
    val isSuccess = remember { mutableStateOf(true) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var courtNumberError by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }

    // Time options
    val timeOptions = remember {
        (8..22).map { hour -> LocalTime.of(hour, 0) }
    }

    // Toast handler
    if (showToast.value) {
        LaunchedEffect(Unit) {
            delay(3500)
            showToast.value = false
            if (isSuccess.value) {
                navController.navigate(Screen.Transactions.route) {
                    popUpTo(Screen.Home.route)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Book Court",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = LightBlue
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() }
                .background(SoftTeal)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Toast notification
                        if (showToast.value) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isSuccess.value) statusGreen else statusRed)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = toastMessage.value,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Court Information Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = courtName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = LightBlue
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = courtAddress,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Court Number Input
                        var isCourtNumberFocused by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            value = courtNumber,
                            onValueChange = {
                                courtNumber = it
                                courtNumberError = it.isBlank()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { isCourtNumberFocused = it.isFocused },
                            label = {
                                Text(
                                    "Court Number",
                                    color = if (courtNumberError) Color.Red
                                    else if (courtNumber.isBlank()) Color.LightGray
                                    else Color.Black
                                )
                            },
                            textStyle = LocalTextStyle.current.copy(
                                color = if (courtNumber.isBlank()) Color.LightGray else Color.Black
                            ),
                            isError = courtNumberError,
                            supportingText = {
                                if (courtNumberError) {
                                    Text("Court number is required", color = Color.Red)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = if (courtNumberError) Color.Red else LightBlue,
                                unfocusedBorderColor = if (courtNumberError) Color.Red else Color.Gray,
                                focusedTextColor = if (courtNumber.isBlank()) Color.LightGray else Color.Black,
                                unfocusedTextColor = if (courtNumber.isBlank()) Color.LightGray else Color.Black,
                                errorContainerColor = Color.White,
                                errorTextColor = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Date Picker
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White,
                                    contentColor = if (selectedDate == null) Color.LightGray else Color.Black
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = selectedDate?.toString() ?: "Select a date",
                                        color = if (selectedDate == null) Color.LightGray else Color.Black,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(
                                        onClick = {
                                            DatePickerDialog(
                                                context,
                                                { _, year, month, dayOfMonth ->
                                                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                                    dateError = false
                                                },
                                                LocalDate.now().year,
                                                LocalDate.now().monthValue - 1,
                                                LocalDate.now().dayOfMonth
                                            ).apply {
                                                datePicker.minDate = System.currentTimeMillis()
                                                show()
                                            }
                                        },
                                        modifier = Modifier.wrapContentWidth()
                                    ) {
                                        Text("Choose", color = LightBlue)
                                    }
                                }
                            }
                            if (dateError) {
                                Text(
                                    text = "Please select a date",
                                    color = Color.Red,
                                    modifier = Modifier.align(Alignment.Start)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Time Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Start Time", style = MaterialTheme.typography.labelSmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    TimeDropdown(
                                        selectedTime = startTime,
                                        onTimeSelected = { startTime = it },
                                        timeOptions = timeOptions
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("End Time", style = MaterialTheme.typography.labelSmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    TimeDropdown(
                                        selectedTime = endTime,
                                        onTimeSelected = { endTime = it },
                                        timeOptions = timeOptions.filter { it.isAfter(startTime) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Price Display
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Cream),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Price",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$30.0",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = LightBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Confirm Button
                        Button(
                            onClick = {
                                courtNumberError = courtNumber.isBlank()
                                dateError = selectedDate == null

                                if (!courtNumberError && !dateError) {
                                    val booking = Booking(
                                        courtName = courtName,
                                        courtNumber = courtNumber,
                                        date = selectedDate!!,
                                        startTime = startTime,
                                        endTime = endTime,
                                        price = 30.0
                                    )
                                    DataSingleton.addBooking(booking)
                                    toastMessage.value = "Booking successful. Redirecting to Transactions in 3 seconds."
                                    isSuccess.value = true
                                    showToast.value = true
                                } else {
                                    toastMessage.value = "Please fill all required fields"
                                    isSuccess.value = false
                                    showToast.value = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LightBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Confirm Booking", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDropdown(
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    timeOptions: List<LocalTime>
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isTimeSelected = remember(selectedTime) {
        selectedTime != LocalTime.MIN || timeOptions.contains(selectedTime)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.clickable { expanded = true }
        ) {
            OutlinedTextField(
                value = if (selectedTime == LocalTime.MIN) "Select time" else selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select time")
                },
                textStyle = LocalTextStyle.current.copy(
                    color = if (!isTimeSelected) Color.LightGray else Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = LightBlue,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = if (!isTimeSelected) Color.LightGray else Color.Black,
                    unfocusedTextColor = if (!isTimeSelected) Color.LightGray else Color.Black,
                    disabledTextColor = if (!isTimeSelected) Color.LightGray else Color.Black
                ),
                enabled = false,
                interactionSource = interactionSource
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            timeOptions.forEach { time ->
                DropdownMenuItem(
                    text = {
                        Text(
                            time.format(DateTimeFormatter.ofPattern("HH:mm")),
                            color = Color.Black
                        )
                    },
                    onClick = {
                        onTimeSelected(time)
                        expanded = false
                    }
                )
            }
        }
    }
}
