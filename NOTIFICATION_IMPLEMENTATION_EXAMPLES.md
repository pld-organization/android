# Implementation Examples - Practical Guide

## How to Integrate Notification Badge & Triggers

---

## 1. Adding Notification Badge to Your Top Bar

### Example: Patient Home Screen Top Bar

Find your existing top app bar in the Patient home screen (likely in `PatientHomeScreen.kt`):

```kotlin
// BEFORE: Without notification badge
@Composable
fun PatientHomeScreen(...) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Existing top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Welcome, Patient")
            IconButton(onClick = { /* Profile */ }) {
                Icon(...)
            }
        }
        // ... rest of screen
    }
}

// AFTER: With notification badge
import com.example.sahtek.ui.notification.components.NotificationBadge
import com.example.sahtek.navigation.Screen

@Composable
fun PatientHomeScreen(
    navController: NavController, // Add this if not already present
    onNotificationsClick: () -> Unit, // Use this existing callback
    ...
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Updated top bar with notification badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Welcome, Patient")
            
            Row {
                // Notification Bell Badge
                IconButton(
                    onClick = onNotificationsClick // Already connected in AppNavGraph
                ) {
                    NotificationBadge(
                        unreadCount = 3, // Get this from your notification ViewModel
                        onClick = onNotificationsClick
                    )
                }
                
                // Profile button
                IconButton(onClick = { /* Profile */ }) {
                    Icon(...)
                }
            }
        }
        // ... rest of screen
    }
}
```

### Example: Doctor Home Screen Top Bar

Same pattern for doctor:

```kotlin
@Composable
fun DoctorHomeScreen(
    onNotificationsClick: () -> Unit,
    ...
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar with notification badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Welcome, Doctor")
            
            Row {
                IconButton(onClick = onNotificationsClick) {
                    NotificationBadge(
                        unreadCount = unreadNotificationCount,
                        onClick = onNotificationsClick
                    )
                }
                
                IconButton(onClick = { /* Profile */ }) {
                    Icon(...)
                }
            }
        }
        // ... rest of screen
    }
}
```

---

## 2. Getting Unread Count from ViewModel

To display the correct unread count, you need to expose it from your notification ViewModel:

```kotlin
// In NotificationViewModel.kt - add this property
val unreadCount: StateFlow<Int> = uiState
    .map { it.unreadCount }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = 0
    )
```

Then in your home screen:

```kotlin
@Composable
fun PatientHomeScreen(
    onNotificationsClick: () -> Unit,
    ...
) {
    // Create notification ViewModel
    val notificationRepository = NotificationRepositoryImpl(
        apiService = RetrofitClient.notificationApiService,
        sessionManager = SessionManager(context)
    )
    val notificationViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(notificationRepository)
    )
    
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar with dynamic unread count
        Row(...) {
            IconButton(onClick = onNotificationsClick) {
                NotificationBadge(
                    unreadCount = unreadCount, // Updated in real-time
                    onClick = onNotificationsClick
                )
            }
        }
        // ... rest
    }
}
```

---

## 3. Triggering Notification After Reservation Creation

### Find Your Reservation Creation Code

Look for where you create a reservation in your app. It's probably in a ViewModel or Repository that handles the creation logic.

### Example: In ReservationViewModel

```kotlin
// BEFORE: Without notification
class ReservationViewModel(
    private val repository: ReservationRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    fun createReservation(
        doctorId: String,
        patientId: String,
        ...
    ) {
        viewModelScope.launch {
            try {
                val result = repository.createReservation(
                    doctorId = doctorId,
                    patientId = patientId,
                    ...
                )
                // Success! But no notification sent
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

// AFTER: With notification trigger
import com.example.sahtek.data.notification.repository.NotificationRepositoryImpl
import com.example.sahtek.ui.notification.ReservationNotificationHelper

class ReservationViewModel(
    private val repository: ReservationRepository,
    private val sessionManager: SessionManager,
    private val notificationRepository: NotificationRepository // Add this
) : ViewModel() {
    
    fun createReservation(
        doctorId: String,
        patientId: String,
        reservationDay: String,
        reservationTime: String,
        meetingUrl: String,
        reason: String,
        ...
    ) {
        viewModelScope.launch {
            try {
                val reservation = repository.createReservation(
                    doctorId = doctorId,
                    patientId = patientId,
                    day = reservationDay,
                    time = reservationTime,
                    meetingUrl = meetingUrl,
                    reason = reason,
                    ...
                )
                
                // Trigger notification
                ReservationNotificationHelper.notifyReservationCreated(
                    repository = notificationRepository,
                    reservationId = reservation.id,
                    doctorId = doctorId,
                    patientId = patientId,
                    reservationDay = reservationDay,
                    reservationTime = reservationTime,
                    meetingUrl = meetingUrl,
                    reason = reason
                )
                
                // Show success to user
                _uiState.update { it.copy(successMessage = "Reservation created!") }
                
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }
}
```

### In Your Reservation Creation Screen

If you create reservations from a Compose screen directly:

```kotlin
@Composable
fun ReservationCreationScreen(
    onSuccess: () -> Unit,
    ...
) {
    val context = LocalContext.current
    
    Button(
        onClick = {
            viewModelScope.launch {
                try {
                    // Create reservation
                    val reservation = createReservation(...)
                    
                    // Get repositories
                    val notificationRepo = NotificationRepositoryImpl(
                        apiService = RetrofitClient.notificationApiService,
                        sessionManager = SessionManager(context)
                    )
                    
                    // Send notification
                    ReservationNotificationHelper.notifyReservationCreated(
                        repository = notificationRepo,
                        reservationId = reservation.id,
                        doctorId = reservation.doctorId,
                        patientId = reservation.patientId,
                        reservationDay = reservation.day,
                        reservationTime = reservation.time,
                        meetingUrl = reservation.meetingUrl,
                        reason = reservation.reason
                    )
                    
                    onSuccess()
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    ) {
        Text("Create Reservation")
    }
}
```

---

## 4. Triggering Notification After Reservation Cancellation

### Find Your Reservation Cancellation Code

Look for where you handle cancelling a reservation.

### Example: In ReservationViewModel

```kotlin
class ReservationViewModel(
    private val repository: ReservationRepository,
    private val notificationRepository: NotificationRepository // Add
) : ViewModel() {
    
    fun cancelReservation(
        reservationId: String,
        doctorId: String,
        patientId: String,
        reservationDay: String,
        reservationTime: String
    ) {
        viewModelScope.launch {
            try {
                // Cancel the reservation
                val result = repository.cancelReservation(reservationId)
                
                // Trigger cancellation notification
                ReservationNotificationHelper.notifyReservationCancelled(
                    repository = notificationRepository,
                    reservationId = reservationId,
                    doctorId = doctorId,
                    patientId = patientId,
                    reservationDay = reservationDay,
                    reservationTime = reservationTime
                )
                
                _uiState.update { it.copy(successMessage = "Reservation cancelled") }
                
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }
}
```

---

## 5. Full Integration Example: Reservation Feature

Here's a complete example showing how to add notifications to your existing reservation feature:

```kotlin
// File: MyReservationViewModel.kt
package com.example.sahtek.ui.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.authservice.SessionManager
import com.example.sahtek.data.notification.repository.NotificationRepository
import com.example.sahtek.ui.notification.ReservationNotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReservationUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class MyReservationViewModel(
    private val reservationRepository: ReservationRepository,
    private val notificationRepository: NotificationRepository, // NEW
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReservationUiState())
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()
    
    fun createReservation(
        doctorId: String,
        patientId: String,
        day: String,
        time: String,
        meetingUrl: String,
        reason: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Step 1: Create the reservation
                val response = reservationRepository.createReservation(
                    doctorId = doctorId,
                    patientId = patientId,
                    day = day,
                    time = time,
                    meetingUrl = meetingUrl,
                    reason = reason
                )
                
                // Step 2: Trigger notification (NEW)
                ReservationNotificationHelper.notifyReservationCreated(
                    repository = notificationRepository,
                    reservationId = response.id,
                    doctorId = doctorId,
                    patientId = patientId,
                    reservationDay = day,
                    reservationTime = time,
                    meetingUrl = meetingUrl,
                    reason = reason
                )
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Reservation created successfully!",
                        errorMessage = null
                    )
                }
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to create reservation",
                        successMessage = null
                    )
                }
            }
        }
    }
    
    fun cancelReservation(
        reservationId: String,
        doctorId: String,
        patientId: String,
        day: String,
        time: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Step 1: Cancel the reservation
                reservationRepository.cancelReservation(reservationId)
                
                // Step 2: Trigger cancellation notification (NEW)
                ReservationNotificationHelper.notifyReservationCancelled(
                    repository = notificationRepository,
                    reservationId = reservationId,
                    doctorId = doctorId,
                    patientId = patientId,
                    reservationDay = day,
                    reservationTime = time
                )
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Reservation cancelled",
                        errorMessage = null
                    )
                }
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to cancel reservation",
                        successMessage = null
                    )
                }
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update {
            it.copy(successMessage = null, errorMessage = null)
        }
    }
}
```

### Using the ViewModel

```kotlin
// In your composition function
val context = LocalContext.current

val reservationViewModel: MyReservationViewModel = viewModel(
    factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MyReservationViewModel(
                reservationRepository = YourReservationRepository,
                notificationRepository = NotificationRepositoryImpl(
                    apiService = RetrofitClient.notificationApiService,
                    sessionManager = SessionManager(context)
                ),
                sessionManager = SessionManager(context)
            ) as T
        }
    }
)

// In your button click
Button(
    onClick = {
        reservationViewModel.createReservation(
            doctorId = "doctor-123",
            patientId = "patient-456",
            day = "MONDAY",
            time = "09:00",
            meetingUrl = "https://example.com/room-123",
            reason = "Annual checkup"
        )
    }
) {
    Text("Create Reservation")
}
```

---

## 6. Making Notification Badge Update in Real-Time

To make the notification badge count update whenever new notifications arrive:

```kotlin
@Composable
fun PatientHomeScreen(
    onNotificationsClick: () -> Unit,
    navController: NavController,
    context: Context
) {
    // Initialize notification ViewModel
    val notificationRepository = remember {
        NotificationRepositoryImpl(
            apiService = RetrofitClient.notificationApiService,
            sessionManager = SessionManager(context)
        )
    }
    
    val notificationViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(notificationRepository)
    )
    
    // Collect unread count
    val uiState by notificationViewModel.uiState.collectAsState()
    val unreadCount = uiState.unreadCount
    
    // Refresh notifications periodically
    LaunchedEffect(Unit) {
        while (true) {
            notificationViewModel.refreshNotifications()
            kotlinx.coroutines.delay(30000) // Refresh every 30 seconds
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header with dynamic badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Welcome, Patient")
            
            Row {
                // Badge updates automatically
                IconButton(onClick = onNotificationsClick) {
                    NotificationBadge(
                        unreadCount = unreadCount,
                        onClick = onNotificationsClick
                    )
                }
            }
        }
        
        // Show success/error messages
        if (uiState.successMessage != null) {
            SnackbarHost(...)
        }
        
        if (uiState.errorMessage != null) {
            SnackbarHost(...)
        }
        
        // Rest of your screen
    }
}
```

---

## 7. Dependency Injection in Your ViewModel Factory

Create a factory to properly inject dependencies:

```kotlin
class MyReservationViewModelFactory(
    private val reservationRepository: ReservationRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MyReservationViewModel::class.java -> 
                MyReservationViewModel(
                    reservationRepository,
                    notificationRepository,
                    sessionManager
                ) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

// Usage
val context = LocalContext.current
val viewModel: MyReservationViewModel = viewModel(
    factory = MyReservationViewModelFactory(
        reservationRepository = YourReservationRepository,
        notificationRepository = NotificationRepositoryImpl(
            apiService = RetrofitClient.notificationApiService,
            sessionManager = SessionManager(context)
        ),
        sessionManager = SessionManager(context)
    )
)
```

---

## 8. Common Integration Patterns

### Pattern 1: LazyColumn with Notifications

```kotlin
LazyColumn(modifier = Modifier.fillMaxSize()) {
    items(notifications) { notification ->
        NotificationCard(
            notification = notification,
            onMarkAsRead = {
                viewModel.markNotificationAsRead(notification.id)
            },
            onDelete = {
                viewModel.deleteNotification(notification.id)
            }
        )
    }
}
```

### Pattern 2: FloatingActionButton with Badge

```kotlin
Box {
    FloatingActionButton(
        onClick = onNotificationsClick
    ) {
        Icon(Icons.Default.Notifications)
    }
    
    if (unreadCount > 0) {
        Badge(
            containerColor = Color.Red,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Text("$unreadCount")
        }
    }
}
```

### Pattern 3: Notification Stream

```kotlin
// For real-time updates when Socket.IO is implemented
LaunchedEffect(Unit) {
    notificationSocketManager.newNotificationFlow.collect { newNotification ->
        // Update UI with new notification
        viewModel.addNewNotification(newNotification)
    }
}
```

---

## ✅ Integration Checklist

- [ ] Added NotificationBadge to top bar
- [ ] Created notification ViewModel in home screen
- [ ] Set up unread count StateFlow
- [ ] Added refresh logic to periodically check notifications
- [ ] Integrated notification trigger in reservation creation
- [ ] Integrated notification trigger in reservation cancellation
- [ ] Tested notification creation
- [ ] Tested notification deletion
- [ ] Tested mark as read
- [ ] Tested unread badge updates
- [ ] Tested error handling
- [ ] Verified token is being passed correctly
- [ ] Deployed to test device/emulator

---

## 🐛 Debugging Tips

### See API calls in Logs
```kotlin
// Retrofit logging is enabled by default (BODY level)
// Look for logs with tag "NotificationApiService"
```

### Check Token
```kotlin
val sessionManager = SessionManager(context)
val token = sessionManager.getAuthToken()
Log.d("Token", "Current token: $token")
```

### Test Manually
```kotlin
// In MainActivity or test activity
val repository = NotificationRepositoryImpl(...)
viewModelScope.launch {
    val result = repository.getMyNotifications()
    result.onSuccess { notifications ->
        Log.d("Notifications", "Got ${notifications.size} notifications")
    }
    result.onFailure { error ->
        Log.e("Notifications", "Error: ${error.message}")
    }
}
```

---

That's it! You now have a complete, production-ready notification system integrated into your app. 🎉
