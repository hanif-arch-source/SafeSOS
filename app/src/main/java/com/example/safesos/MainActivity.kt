package com.example.safesos

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.safesos.data.local.AppDatabase
import com.example.safesos.data.local.UserEntity
import com.example.safesos.data.repository.AuthRepository
import com.example.safesos.data.repository.ContactsRepository
import com.example.safesos.data.repository.IncidentRepository
import com.example.safesos.location.LocationHelper
import com.example.safesos.model.IncidentType
import com.example.safesos.sensor.ShakeDetector
import com.example.safesos.ui.screens.*
import com.example.safesos.ui.theme.SafeSOSTheme
import com.example.safesos.ui.theme.SosGreen
import com.example.safesos.ui.theme.SosRed
import com.example.safesos.util.SessionManager
import com.example.safesos.util.SmsSender
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeSosApp()
        }
    }
}

private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val SELECT_INCIDENT = "select_incident"
    const val EMERGENCY_DETAILS = "emergency_details"
    const val CONFIRM_SEND = "confirm_send"
    const val SOS_SENT = "sos_sent"
    const val CONTACTS = "contacts"
    const val SETTINGS = "settings"
    const val SAFETY_TIPS = "safety_tips"
    const val INCIDENT_HISTORY = "incident_history"
    const val PROFILE = "profile"
    const val MEDICAL_INFO = "medical_info"
    const val ABOUT = "about"
}

@OptIn(ExperimentalPermissionsApi::class)
@androidx.compose.runtime.Composable
fun SafeSosApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val database = remember { AppDatabase.getInstance(context) }
    val authRepository = remember { AuthRepository(database.userDao()) }
    val contactsRepository = remember { ContactsRepository(database.contactDao()) }
    val incidentRepository = remember { IncidentRepository(database.incidentDao()) }
    val sessionManager = remember { SessionManager(context) }
    val locationHelper = remember { LocationHelper(context) }

    var currentUser by remember { mutableStateOf<UserEntity?>(null) }
    var isSessionChecked by remember { mutableStateOf(false) }

    // Restore a previous session (if any) before deciding the start screen.
    LaunchedEffect(Unit) {
        val savedUserId = sessionManager.getUserId()
        if (savedUserId != null) {
            currentUser = authRepository.getUser(savedUserId)
        }
        isSessionChecked = true
    }

    val formState = remember { EmergencyFormState() }
    var selectedIncidentType by remember { mutableStateOf(IncidentType.OTHER) }
    var sendReport by remember { mutableStateOf<SmsSender.SendReport?>(null) }

    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }
    var addressLine by remember { mutableStateOf("Fetching location...") }
    val hasMapsKey = BuildConfig.MAPS_API_KEY.isNotBlank()

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    fun refreshLocation() {
        scope.launch {
            val resolved = locationHelper.getResolvedLocation()
            if (resolved != null) {
                currentLatLng = LatLng(resolved.location.latitude, resolved.location.longitude)
                addressLine = resolved.addressLine
                formState.location = resolved.addressLine
            } else {
                addressLine = "Location unavailable"
            }
        }
    }

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            refreshLocation()
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Shake-to-alert: only while logged in and sitting on Home, so a shake
    // mid-form doesn't yank the user out of what they're typing. A shake is
    // treated as a general emergency — it skips choosing a specific incident
    // type and goes straight to reviewing/sending the alert to every saved
    // contact, since the whole point of a shake trigger is speed.
    DisposableEffect(currentUser, currentRoute) {
        val detector = ShakeDetector(context) {
            if (currentUser != null && currentRoute == Routes.HOME) {
                selectedIncidentType = IncidentType.OTHER
                formState.incidentTypeId = IncidentType.OTHER.id
                formState.description = "Emergency SOS triggered by shaking the phone."
                navController.navigate(Routes.CONFIRM_SEND)
            }
        }
        if (currentUser != null && currentRoute == Routes.HOME) {
            detector.start()
        }
        onDispose { detector.stop() }
    }

    val statusColor = when {
        currentRoute == Routes.SOS_SENT -> SosGreen
        currentRoute == Routes.LOGIN -> Color.White
        else -> SosRed
    }
    val darkStatusIcons = currentRoute == Routes.LOGIN

    SafeSOSTheme(statusBarColor = statusColor, darkIcons = darkStatusIcons) {
        if (!isSessionChecked) {
            Box(modifier = Modifier.fillMaxSize())
            return@SafeSOSTheme
        }

        val startDestination = if (currentUser != null) Routes.HOME else Routes.LOGIN

        NavHost(navController = navController, startDestination = startDestination) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    authRepository = authRepository,
                    onLoginSuccess = { user ->
                        currentUser = user
                        sessionManager.saveUserId(user.userId)
                        navController.navigate(Routes.HOME) { popUpTo(0) }
                    },
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    authRepository = authRepository,
                    onBack = { navController.popBackStack() },
                    onRegisterSuccess = { user ->
                        currentUser = user
                        sessionManager.saveUserId(user.userId)
                        navController.navigate(Routes.HOME) { popUpTo(0) }
                    }
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    userFirstName = currentUser?.fullName?.trim()?.split(" ")?.firstOrNull(),
                    addressLine = addressLine,
                    currentLatLng = currentLatLng,
                    hasMapsKey = hasMapsKey,
                    hasLocationPermission = locationPermissionState.status.isGranted,
                    onRequestLocationPermission = { locationPermissionState.launchPermissionRequest() },
                    onRefreshLocation = { refreshLocation() },
                    onSosClick = { navController.navigate(Routes.SELECT_INCIDENT) },
                    onContactsClick = { navController.navigate(Routes.CONTACTS) },
                    onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                    onSafetyTipsClick = { navController.navigate(Routes.SAFETY_TIPS) }
                )
            }
            composable(Routes.SELECT_INCIDENT) {
                SelectIncidentTypeScreen(
                    onBack = { navController.popBackStack() },
                    onTypeSelected = { type ->
                        selectedIncidentType = type
                        // Pre-fill from the saved medical profile (Settings > Manage
                        // Medical Info) so it doesn't need retyping every time,
                        // without overwriting anything already entered this session.
                        if (type.isMedical) {
                            currentUser?.let { user ->
                                if (formState.bloodType.isBlank()) formState.bloodType = user.bloodType
                                if (formState.condition.isBlank()) formState.condition = user.medicalCondition
                                if (formState.allergies.isBlank()) formState.allergies = user.allergies
                            }
                        }
                        navController.navigate(Routes.EMERGENCY_DETAILS)
                    }
                )
            }
            composable(Routes.EMERGENCY_DETAILS) {
                EmergencyDetailsScreen(
                    incidentType = selectedIncidentType,
                    formState = formState,
                    liveLocation = addressLine,
                    onBack = { navController.popBackStack() },
                    onContinue = { navController.navigate(Routes.CONFIRM_SEND) }
                )
            }
            composable(Routes.CONFIRM_SEND) {
                val contacts by contactsRepository
                    .observeContacts(currentUser?.userId ?: -1)
                    .collectAsState(initial = emptyList())
                val mapsUrl = currentLatLng?.let {
                    "https://maps.google.com/?q=${it.latitude},${it.longitude}"
                }
                ConfirmSendScreen(
                    incidentType = selectedIncidentType,
                    formState = formState,
                    contacts = contacts,
                    liveLocation = addressLine,
                    mapsUrl = mapsUrl,
                    onBack = { navController.popBackStack() },
                    onSent = { report ->
                        sendReport = report
                        val ownerId = currentUser?.userId
                        if (ownerId != null) {
                            val detailsSummary = if (selectedIncidentType.isMedical) {
                                listOfNotNull(
                                    formState.bloodType.takeIf { it.isNotBlank() }?.let { "Blood type: $it" },
                                    formState.condition.takeIf { it.isNotBlank() }?.let { "Condition: $it" }
                                ).joinToString(" \u2022 ")
                            } else {
                                formState.description
                            }
                            scope.launch {
                                incidentRepository.logIncident(
                                    ownerId = ownerId,
                                    incidentType = selectedIncidentType,
                                    locationLine = addressLine,
                                    detailsSummary = detailsSummary,
                                    report = report
                                )
                            }
                        }
                        navController.navigate(Routes.SOS_SENT) {
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }
            composable(Routes.SOS_SENT) {
                SosSentScreen(
                    incidentType = selectedIncidentType,
                    report = sendReport,
                    onBackToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.CONTACTS) {
                ContactsScreen(
                    ownerId = currentUser?.userId ?: -1,
                    contactsRepository = contactsRepository,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    currentUser = currentUser,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        sessionManager.clear()
                        currentUser = null
                        navController.navigate(Routes.LOGIN) { popUpTo(0) }
                    },
                    onViewIncidentHistory = { navController.navigate(Routes.INCIDENT_HISTORY) },
                    onEditProfile = { navController.navigate(Routes.PROFILE) },
                    onManageMedicalInfo = { navController.navigate(Routes.MEDICAL_INFO) },
                    onAbout = { navController.navigate(Routes.ABOUT) }
                )
            }
            composable(Routes.SAFETY_TIPS) {
                SafetyTipScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.INCIDENT_HISTORY) {
                IncidentHistoryScreen(
                    ownerId = currentUser?.userId ?: -1,
                    incidentRepository = incidentRepository,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.PROFILE) {
                val user = currentUser
                if (user == null) {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                } else {
                    ProfileScreen(
                        currentUser = user,
                        authRepository = authRepository,
                        onBack = { navController.popBackStack() },
                        onProfileUpdated = { updated -> currentUser = updated }
                    )
                }
            }
            composable(Routes.MEDICAL_INFO) {
                val user = currentUser
                if (user == null) {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                } else {
                    MedicalInfoScreen(
                        currentUser = user,
                        authRepository = authRepository,
                        onBack = { navController.popBackStack() },
                        onSaved = { updated -> currentUser = updated }
                    )
                }
            }
            composable(Routes.ABOUT) {
                AboutScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
