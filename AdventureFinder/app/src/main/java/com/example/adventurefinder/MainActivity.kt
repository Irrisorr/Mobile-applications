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
import kotlinx.coroutines.launch
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import android.Manifest
import android.app.Activity
import androidx.compose.runtime.DisposableEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import android.content.IntentSender
import android.location.LocationManager
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.List
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import androidx.compose.foundation.lazy.items


val Context.dataStore by preferencesDataStore("user_data")
val userImageKey = intPreferencesKey("user_image")
private const val REQUEST_CHECK_SETTINGS = 123



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
fun SettingsScreen(onLogout: () -> Unit, onThemeChange: () -> Unit) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Settings") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onThemeChange,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Theme")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
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
fun AdventureFinderApp() {
    val navController = rememberNavController()
    var loggedInUsername by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(false) }
    var isDarkTheme by remember { mutableStateOf(false) }

    val appTheme = if (isDarkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    MaterialTheme(colorScheme = appTheme) {
        Scaffold(
            bottomBar = {
                if (isLoggedIn) {
                    BottomNavigationBar(
                        modifier = Modifier.background(Color.White, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        onClick = { screen ->
                            navController.navigate(screen)
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(navController, loggedInUsername) { username ->
                        loggedInUsername = username
                        isLoggedIn = true
                        navController.navigate("profile")
                    }
                }
                composable("registration") { RegistrationScreen(navController) }
                composable("profile") { ProfileScreen(username = loggedInUsername) }
                composable("map") { MapScreen() }
                composable("wishlist") { Text("My Wishlist Screen") }
                composable("settings") {
                    SettingsScreen(
                        onLogout = {
                            isLoggedIn = false
                            navController.navigate("login")
                        },
                        onThemeChange = { isDarkTheme = !isDarkTheme }
                    )
                }
                composable("activities") { ActivitiesScreen() }
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
    var selectedImage by remember { mutableStateOf(R.drawable.my_lonely_kitten) }
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
            selectedImage = preferences[userImageKey] ?: R.drawable.my_lonely_kitten
        }
    }

// Сохранение выбранного изображения в DataStore
    Button(
        onClick = {
            isEditing = false
            scope.launch {
                context.dataStore.edit { preferences ->
                    preferences[userDescriptionKey] = userDescription
                    preferences[userImageKey] = selectedImage
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Save")
    }

    val imageOptions = listOf(
        R.drawable.my_feel_good,
        R.drawable.my_lonely_kitten,
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
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clickable {
                        showDialog = true // Показываем диалог при клике на изображение
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
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        isEditing = false
                        scope.launch {
                            context.dataStore.edit { preferences ->
                                preferences[userDescriptionKey] = userDescription
                                preferences[userImageKey] = selectedImage
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            } else {
                Text(userDescription, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit")
                }
            }

            // Диалог для выбора изображения
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Select an image") },
                    text = {
                        Column {
                            imageOptions.forEach { imageRes ->
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clickable {
                                            selectedImage = imageRes
                                            scope.launch {
                                                context.dataStore.edit { preferences ->
                                                    preferences[userImageKey] = selectedImage
                                                }
                                            }
                                            showDialog = false
                                        }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val locationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val defaultLocation = LatLng(52.2319, 21.0067) // Варшава

    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let {
                currentLocation = LatLng(it.latitude, it.longitude)
            }
        }
    }

    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    if (!isLocationEnabled) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(context)
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        context as Activity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Обработка ошибки
                }
            }
        }

    }

    DisposableEffect(Unit) {
        if (permissionsState.allPermissionsGranted) {
            locationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            permissionsState.launchMultiplePermissionRequest()
        }
        onDispose {
            locationClient.removeLocationUpdates(locationCallback)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation ?: defaultLocation, 6.5f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(mapType = MapType.NORMAL),
        uiSettings = MapUiSettings(zoomControlsEnabled = false)
    ) {
        currentLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                title = "Your Location",
                snippet = "This is your current location"
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = { onClick("profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Blue,
                selectedTextColor = Color.Blue
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Map") },
            label = { Text("Map") },
            selected = false,
            onClick = { onClick("map") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Blue,
                selectedTextColor = Color.Blue
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Activities") },
            label = { Text("Activities") },
            selected = false,
            onClick = { onClick("activities") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Blue,
                selectedTextColor = Color.Blue
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "My Wishlist") },
            label = { Text("My Wishlist") },
            selected = false,
            onClick = { onClick("wishlist") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Blue,
                selectedTextColor = Color.Blue
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = false,
            onClick = { onClick("settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Blue,
                selectedTextColor = Color.Blue
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
// Переименуйте ваш класс Activity в AdventureActivity
data class AdventureActivity(
    val name: String,
    val category: String,
    val description: String,
    val address: String,
    val imageRes: Int
)

@Composable
fun ActivitiesScreen() {
    val activities = listOf(
        AdventureActivity(
            name = "Hiking",
            category = "Outdoor",
            description = "Enjoy a scenic hike in the mountains.",
            address = "123 Mountain Trail",
            imageRes = R.drawable.my_feel_good
        ),
        AdventureActivity(
            name = "Hiking 2",
            category = "Test",
            description = "Enjoy a scenic hike in the mountains.",
            address = "123 Mountain Trail",
            imageRes = R.drawable.my_lonely_kitten
        )
        // Добавьте остальные активности здесь
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(activities) { activity ->
            ActivityItem(activity)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ActivityItem(activity: AdventureActivity) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = activity.imageRes),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = activity.name, style = MaterialTheme.typography.titleMedium)
            Text(text = activity.category, style = MaterialTheme.typography.bodySmall)
            Text(text = activity.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = activity.address, style = MaterialTheme.typography.bodySmall)
        }
    }
}
