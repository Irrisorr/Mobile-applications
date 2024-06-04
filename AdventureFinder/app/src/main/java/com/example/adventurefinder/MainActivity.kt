package com.example.adventurefinder

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.launch
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


val Context.dataStore by preferencesDataStore("user_data")


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdventureFinderApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdventureFinderApp() {
    val navController = rememberNavController()
    var loggedInUsername by remember { mutableStateOf("") } // Хранение имени пользователя

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, loggedInUsername) { username ->
            loggedInUsername = username  // Сохраняем имя при успешном входе
        } }
        composable("registration") { RegistrationScreen(navController) }
        composable("main") { ProfileScreen(username = loggedInUsername) } // Передаем имя
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    loggedInUsername: String,
    onLoginSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val dbHelper = UserDatabaseHelper(context)
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Login") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val user = dbHelper.getUser(username)
                    if (user != null && user.password == password) {
                        onLoginSuccess(username) // Вызываем колбэк при успехе
                        navController.navigate("main")

                    } else {
                        Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { navController.navigate("registration") }) {
                Text("Registration")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dbHelper = UserDatabaseHelper(context)

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Register") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    if (dbHelper.addUser(User(username = username, password = password, registrationDate = currentDate))) {
                        Toast.makeText(context, "Registration completed successfully!", Toast.LENGTH_SHORT).show()
                        navController.navigate("login")
                    } else {
                        Toast.makeText(context, "registration error", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(username: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userDescription by remember { mutableStateOf("About me: ") }
    var selectedImage by remember { mutableStateOf(R.drawable.lonely_kitten) }
    var isEditing by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) } // Состояние для диалога
    val dbHelper = UserDatabaseHelper(context)
    val user = dbHelper.getUser(username)
    val registrationDate = user?.registrationDate ?: "Unknown"

    val userDescriptionKey = stringPreferencesKey("user_description_$username")
    val userImageKey = intPreferencesKey("user_image_$username")

    // Загрузка описания из DataStore
    LaunchedEffect(username) {
        context.dataStore.data.collect { preferences ->
            userDescription = preferences[userDescriptionKey] ?: "About me: "
            selectedImage = preferences[userImageKey] ?: R.drawable.lonely_kitten
        }
    }

    val imageOptions = listOf(
        R.drawable.feel_good,
        R.drawable.lonely_kitten,
        // добавь сюда ID своих картинок
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("My Profile") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hello, $username!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Registration date: $registrationDate", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                bitmap = BitmapFactory.decodeResource(LocalContext.current.resources, selectedImage).asImageBitmap(),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clickable {
                        showDialog = true  // Показываем диалог при клике
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = userDescription,
                    onValueChange = { userDescription = it },
                    label = { Text("About me") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(userDescription)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (isEditing) {
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[userDescriptionKey] = userDescription
                        }
                    }
                    Toast.makeText(context, "Description saved!", Toast.LENGTH_SHORT).show()
                }
                isEditing = !isEditing
            }) {
                Text(if (isEditing) "Save Changes" else "Change Information")
            }
        }
    }

    // Диалог выбора аватара
    if (showDialog) {
        var selectedIndex by remember { mutableStateOf(0) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Choose your avatar") },
            text = {
                Column {
                    imageOptions.forEachIndexed { index, image ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = image),
                                contentDescription = "Avatar option",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable { selectedIndex = index }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Avatar ${index + 1}")
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    selectedImage = imageOptions[selectedIndex]
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[userImageKey] = selectedImage
                        }
                    }
                    showDialog = false
                }) {
                    Text("OK")
                }
            }
        )
    }
}