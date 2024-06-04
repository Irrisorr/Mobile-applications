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
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.List
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import com.google.android.gms.maps.model.BitmapDescriptorFactory


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
    var wishlist by remember { mutableStateOf<List<AdventureActivity>>(emptyList()) }
    val dbHelper = UserDatabaseHelper(LocalContext.current)


    LaunchedEffect(loggedInUsername) {
        if (loggedInUsername.isNotEmpty()) {
            wishlist = dbHelper.getUserWishlist(loggedInUsername)
        }
    }

    val appTheme = if (isDarkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    MaterialTheme(colorScheme = appTheme) {
        val activities = remember { getActivitiesList() }
        val currentRoute = rememberNavController().currentBackStackEntry?.destination?.route ?: "login" // Получаем текущий маршрут

        Scaffold(
            bottomBar = {
                if (isLoggedIn) {
                    BottomNavigationBar(
                        modifier = Modifier.background(Color.White, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        onClick = { screen ->
                            navController.navigate(screen)
                        },
                        currentRoute = currentRoute // Передаем текущий маршрут
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
                composable("map") { MapScreen(activities = activities) }
                composable("settings") {
                    SettingsScreen(
                        onLogout = {
                            isLoggedIn = false
                            navController.navigate("login")
                        },
                        onThemeChange = { isDarkTheme = !isDarkTheme }
                    )
                }
                composable("activities") {
                    ActivitiesScreen(
                        activities = activities,
                        wishlist = wishlist,
                        onLikeClick = { activity ->
                            if (wishlist.contains(activity)) {
                                dbHelper.removeFromWishlist(loggedInUsername, activity.name)
                            } else {
                                dbHelper.addToWishlist(loggedInUsername, activity.name)
                            }
                            wishlist = dbHelper.getUserWishlist(loggedInUsername)
                        }
                    )
                }

                composable("wishlist") {
                    WishlistScreen(
                        wishlist = wishlist,
                        onLikeClick = { activity ->
                            dbHelper.removeFromWishlist(loggedInUsername, activity.name)
                            wishlist = dbHelper.getUserWishlist(loggedInUsername)
                        }
                    )
                }
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
        val dbHelper = UserDatabaseHelper(context)
        val user = dbHelper.getUser(username)
        val registrationDate = user?.registrationDate ?: "Unknown"

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
fun MapScreen(activities: List<AdventureActivity> = emptyList()) {
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
                snippet = "This is your current location",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE) // Синяя метка
            )
        }
        activities.forEach { activity ->
            val address = activity.address
            getLatLngFromAddress(context, address)?.let { latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    title = activity.name,
                    snippet = activity.address
                )
            }
        }
    }
}

fun getLatLngFromAddress(context: Context, strAddress: String): LatLng? {
    val coder = Geocoder(context)
    val address: List<Address>?
    var p1: LatLng? = null

    try {
        // Получаем список адресов из геокодера
        address = coder.getFromLocationName(strAddress, 5)

        if (address == null) {
            return null
        }

        // Берем первый адрес (обычно самый точный)
        val location: Address = address[0]
        p1 = LatLng(location.latitude, location.longitude)

    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    return p1
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    currentRoute: String // Добавляем текущий маршрут
) {
    val selectedItemSize = 28.dp
    val unselectedItemSize = 20.dp

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 2.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(if (currentRoute == "profile") selectedItemSize else unselectedItemSize)
                )
            },
            label = { Text("Profile", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute == "profile", // Сравниваем с текущим маршрутом
            onClick = { onClick("profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Blue,
                selectedTextColor = Color.Blue,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Map",
                    modifier = Modifier.size(if (currentRoute == "map") selectedItemSize else unselectedItemSize)
                )
            },
            label = { Text("Map", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute == "map",
            onClick = { onClick("map") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Blue,
                selectedTextColor = Color.Blue,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.List,
                contentDescription = "Activities",
                modifier = Modifier.size(if (currentRoute == "activities") selectedItemSize else unselectedItemSize)
            ) },
            label = { Text("Activities", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute == "activities",
            onClick = { onClick("activities") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Blue,
                selectedTextColor = Color.Blue,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Star,
                contentDescription = "My Wishlist",
                modifier = Modifier.size(if (currentRoute == "wishlist") selectedItemSize else unselectedItemSize)
            ) },
            label = { Text("My Wishlist", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute == "wishlist",
            onClick = { onClick("wishlist") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Blue,
                selectedTextColor = Color.Blue,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(if (currentRoute == "settings") selectedItemSize else unselectedItemSize)
            ) },
            label = { Text("Settings", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute == "settings",
            onClick = { onClick("settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Blue,
                selectedTextColor = Color.Blue,
                indicatorColor = Color.Transparent
            )
        )
    }
}

// Функция для получения списка активностей
fun getActivitiesList(): List<AdventureActivity> {
    return listOf(
        AdventureActivity(
            name = "Karting",
            category = "Race",
            description = "Witaj na jednym z najnowocześniejszych torów kartingowych w Polsce!",
            address = "ul. Cisowa 6, 60-185 Skórzewo",
            imageRes = R.drawable.karting
        ),
        AdventureActivity(
            name = "Bungee",
            category = "Adrenaline",
            description = "Ekstremalna atrakcja nad Maltą!",
            address = "Wileńska, 61-024 Poznań",
            imageRes = R.drawable.bungee
        ),
        AdventureActivity(
            name = "Wakepark",
            category = "Paddle",
            description = "Pierwszy Wake Park w centrum Poznania",
            address = "Jezioro Maltańskie, Poznań, Jana Pawła II, 61-130 Poznań",
            imageRes = R.drawable.wakepark
        ),
        AdventureActivity(
            name = "IceRing",
            category = "Paddle",
            description = "Nowoczesny i funkcjonalny obiekt znajdujący się w centrum miasta, wyposażony w pełnowymiarowe boisko do gry w hokeja na lodzie",
            address = "ul. Jana Spychalskiego 34, 61-553 Poznań",
            imageRes = R.drawable.icering
        ),
        AdventureActivity(
            name = "IceRing test 2",
            category = "Paddle",
            description = "Nowoczesny i funkcjonalny obiekt znajdujący się w centrum miasta, wyposażony w pełnowymiarowe boisko do gry w hokeja na lodzie",
            address = "ul. Jana Spychalskiego 34, 61-553 Poznań",
            imageRes = R.drawable.icering
        )
    )
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
fun ActivitiesScreen(
    activities: List<AdventureActivity>,
    wishlist: List<AdventureActivity>,
    onLikeClick: (AdventureActivity) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) } // Состояние для DropdownMenu

    val filteredActivities = if (selectedCategory != null) {
        activities.filter { it.category == selectedCategory }
    } else {
        activities
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // DropdownMenu для выбора категории
        Box(modifier = Modifier.padding(8.dp)) {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCategory != null) Color.Blue else Color.LightGray
                )
            ) {
                Text(
                    text = selectedCategory ?: "Select Category",
                    color = if (selectedCategory != null) Color.White else Color.Black
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                activities.map { it.category }.distinct().forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(filteredActivities) { activity ->
                val isLiked = wishlist.any { it.name == activity.name }
                ActivityItem(activity, isLiked, onLikeClick)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
fun ActivityItem(activity: AdventureActivity, isLiked: Boolean, onLikeClick: (AdventureActivity) -> Unit) {
    var liked by remember { mutableStateOf(isLiked) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = activity.imageRes),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = activity.name, style = MaterialTheme.typography.titleMedium)
            Text(text = activity.category, style = MaterialTheme.typography.bodySmall)
            Text(text = activity.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = activity.address, style = MaterialTheme.typography.bodySmall)
        }
        IconButton(
            onClick = {
                liked = !liked
                onLikeClick(activity)
            }
        ) {
            Icon(
                imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (liked) "Unlike" else "Like",
                tint = if (liked) Color.Red else Color.Gray
            )
        }
    }
}

@Composable
fun WishlistScreen(wishlist: List<AdventureActivity>, onLikeClick: (AdventureActivity) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(wishlist) { activity ->
            ActivityItem(activity, isLiked = true, onLikeClick = onLikeClick)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
