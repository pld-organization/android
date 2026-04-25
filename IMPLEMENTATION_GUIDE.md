# Premium UI Redesign - Implementation Guide

## Quick Start

### 1. Import the Animation System
```kotlin
import com.example.sahtek.ui.animation.AnimationDurations
import com.example.sahtek.ui.animation.AnimationSpecs
import com.example.sahtek.ui.animation.fadeInAnimation
import com.example.sahtek.ui.animation.scaleInAnimation
import com.example.sahtek.ui.animation.slideUpAnimation
```

### 2. Import Modern Components
```kotlin
// Premium UI Components
import com.example.sahtek.ui.components.ButtonVariant
import com.example.sahtek.ui.components.PremiumButton
import com.example.sahtek.ui.components.PremiumCard
import com.example.sahtek.ui.components.PremiumLoadingIndicator
import com.example.sahtek.ui.components.PremiumEmptyState
import com.example.sahtek.ui.components.StatusBadge

// Dashboard Components
import com.example.sahtek.ui.home.modern.AnimatedTabBar
import com.example.sahtek.ui.home.modern.PremiumStatCard
import com.example.sahtek.ui.home.modern.FeaturedSectionCard
import com.example.sahtek.ui.home.modern.AnimatedListItem

// Auth Components
import com.example.sahtek.ui.components.SahtekPrimaryButton
import com.example.sahtek.ui.components.SahtekPanel
```

## Implementation Patterns

### Pattern 1: Adding Animations to Existing Composables

**Before:**
```kotlin
@Composable
fun MyCard(content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(content)
    }
}
```

**After (with animations):**
```kotlin
@Composable
fun MyCard(content: String) {
    var isVisible by remember { mutableStateOf(false) }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(
                alpha = alpha,
                scaleX = scale,
                scaleY = scale
            )
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(content)
    }
}
```

### Pattern 2: Using Modifier Extensions for Animations

```kotlin
// Simple fade in
Box(modifier = Modifier.fadeInAnimation(duration = 300))

// Scale in with delay
Box(modifier = Modifier.scaleInAnimation(duration = 300, delay = 100))

// Slide up from bottom
Box(modifier = Modifier.slideUpAnimation(duration = 400))

// Combine animations
Box(modifier = Modifier
    .fadeInAnimation(duration = 300)
    .slideUpAnimation(duration = 400)
)
```

### Pattern 3: Loading States

**Basic Loading:**
```kotlin
@Composable
fun LoadingScreen() {
    PremiumLoadingIndicator(
        message = "Loading your appointments..."
    )
}
```

**In a LazyColumn:**
```kotlin
LazyColumn {
    if (isLoading) {
        item {
            PremiumLoadingIndicator()
        }
    } else {
        items(appointments) { appointment ->
            AppointmentCard(appointment)
        }
    }
}
```

### Pattern 4: Empty States

```kotlin
@Composable
fun EmptyAppointmentsList() {
    if (appointments.isEmpty()) {
        PremiumEmptyState(
            title = "No Appointments",
            message = "You don't have any appointments scheduled. Search for a doctor to book one.",
            modifier = Modifier.padding(32.dp)
        )
    }
}
```

### Pattern 5: Animated Buttons

```kotlin
// Primary Button (CTA)
PremiumButton(
    text = "Book Appointment",
    onClick = { /* Handle click */ },
    variant = ButtonVariant.PRIMARY,
    size = ButtonSize.LARGE
)

// Secondary Button
PremiumButton(
    text = "Cancel",
    onClick = { /* Handle click */ },
    variant = ButtonVariant.SECONDARY
)

// Danger Button
PremiumButton(
    text = "Delete",
    onClick = { /* Handle click */ },
    variant = ButtonVariant.DANGER
)

// Success Button
PremiumButton(
    text = "Confirm",
    onClick = { /* Handle click */ },
    variant = ButtonVariant.SUCCESS
)

// With Loading State
var isLoading by remember { mutableStateOf(false) }
PremiumButton(
    text = "Submit",
    onClick = { isLoading = true },
    isLoading = isLoading
)
```

### Pattern 6: Status Badges

```kotlin
Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    StatusBadge(StatusType.SUCCESS)
    StatusBadge(StatusType.WARNING)
    StatusBadge(StatusType.ERROR)
    StatusBadge(StatusType.INFO)
}
```

### Pattern 7: Animated Tab Navigation

```kotlin
val tabs = listOf("Overview", "Appointments", "Analysis", "Schedule")
var selectedTab by remember { mutableIntStateOf(0) }

Column {
    // Content below
    when (selectedTab) {
        0 -> OverviewTab()
        1 -> AppointmentsTab()
        2 -> AnalysisTab()
        3 -> ScheduleTab()
    }
    
    // Animated tabs at bottom
    AnimatedTabBar(
        tabs = tabs,
        selectedTabIndex = selectedTab,
        onTabSelected = { selectedTab = it }
    )
}
```

### Pattern 8: Premium Stat Cards (Dashboard)

```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
    PremiumStatCard(
        title = "Upcoming",
        value = "3",
        subtitle = "This week",
        icon = Icons.Default.CalendarMonth,
        backgroundColor = Color(0xFFE3F2FD),
        iconTint = Color(0xFF1976D2),
        modifier = Modifier.weight(1f)
    )
    
    PremiumStatCard(
        title = "Completed",
        value = "12",
        subtitle = "This month",
        icon = Icons.Default.CheckCircle,
        backgroundColor = Color(0xFFE8F5E9),
        iconTint = Color(0xFF388E3C),
        modifier = Modifier.weight(1f)
    )
}
```

### Pattern 9: Staggered List Animations

```kotlin
LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    itemsIndexed(items) { index, item ->
        AnimatedListItem(
            title = item.title,
            subtitle = item.subtitle,
            leadingContent = {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = SahtekBlue
                )
            },
            delay = index * 50, // Stagger each item by 50ms
            onClick = { /* Handle click */ }
        )
    }
}
```

### Pattern 10: Featured Section Card

```kotlin
FeaturedSectionCard(
    title = "Start Your Analysis",
    description = "Get personalized health insights based on your medical data.",
    buttonText = "Begin",
    onButtonClick = { navigateToAnalysis() },
    icon = Icons.Default.AnalyticsOutlined,
    isNew = true
)
```

## Page-by-Page Update Plan

### 1. **Authentication Pages** ✅
- **LoginScreen**: Added animations to header and panels
- **SignupScreen**: Use same patterns
- **RoleSelectionScreen**: Add scale animations to role cards
- Components to use: `SahtekPrimaryButton`, `SahtekPanel`, `SahtekRoleCard`

### 2. **Patient Home Page**
```kotlin
@Composable
fun ModernPatientHome() {
    Column(modifier = Modifier
        .fillMaxSize()
        .fadeInAnimation(duration = 400)
    ) {
        // Animated greeting
        Text("Welcome, Sara!", modifier = Modifier.slideUpAnimation(duration = 300))
        
        // Animated stat cards
        Row {
            repeat(3) { index ->
                PremiumStatCard(
                    // ... stats
                    modifier = Modifier
                        .weight(1f)
                        .slideUpAnimation(
                            duration = 300,
                            delay = index * 100
                        )
                )
            }
        }
        
        // Animated featured sections
        FeaturedSectionCard(
            // ... content
            modifier = Modifier.slideUpAnimation(
                duration = 300,
                delay = 300
            )
        )
        
        // Animated list items
        LazyColumn {
            itemsIndexed(appointments) { index, item ->
                AnimatedListItem(
                    // ... content
                    delay = 300 + index * 50
                )
            }
        }
    }
}
```

**Key Changes:**
- Add greeting header animation
- Animate stat cards with stagger
- Add featured section with slide-up
- Use `AnimatedListItem` for appointments
- Add `AnimatedTabBar` at bottom

### 3. **Doctor Home Page** 
**Similar pattern to patient:**
- Animate doctor profile section
- Animate appointment grid
- Add animated metrics cards
- Stagger appointment list

### 4. **Schedule Pages**
```kotlin
@Composable
fun SchedulePage() {
    Column {
        // Animated tabs
        AnimatedTabBar(
            tabs = listOf("Upcoming", "Completed", "Cancelled"),
            selectedTabIndex = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        
        // Conditional content
        when (selectedTab) {
            0 -> {
                if (upcomingAppointments.isEmpty()) {
                    PremiumEmptyState(
                        title = "No Upcoming Appointments",
                        message = "Search for doctors to book your next appointment."
                    )
                } else {
                    LazyColumn {
                        itemsIndexed(upcomingAppointments) { index, appointment ->
                            AppointmentTimelineCard(
                                // ... content
                                modifier = Modifier.slideUpAnimation(
                                    duration = 300,
                                    delay = index * 50
                                )
                            )
                        }
                    }
                }
            }
            // ... other tabs
        }
    }
}
```

### 5. **Bottom Navigation**
```kotlin
@Composable
fun AnimatedBottomNav() {
    val items = listOf("Home", "Search", "Profile", "Settings")
    var selectedItem by remember { mutableIntStateOf(0) }
    
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                label = { Text(item) },
                // Add click animation
                modifier = Modifier.clickable {
                    // Scale animation on click
                    selectedItem = index
                }
            )
        }
    }
}
```

## Color Integration Checklist

- [ ] Update all blue colors to `SahtekBlue` (#3B7FE5)
- [ ] Use `SahtekSuccess` (#059669) for positive states
- [ ] Use `SahtekWarning` (#F59E0B) for alerts
- [ ] Use `SahtekError` (#DC2626) for errors
- [ ] Use `SahtekTextPrimary` (#0F172A) for main text
- [ ] Use `SahtekTextSecondary` (#475569) for secondary text
- [ ] Update backgrounds to `SahtekBackground` (#FAFCFE)
- [ ] Update surfaces to `SahtekSurface` (#FFFFFF)

## Animation Best Practices

### Do's ✅
- Use meaningful durations (200-500ms)
- Combine fade + scale for polished feel
- Add delays for staggered list animations
- Use spring animations for playful interactions
- Test animations on real devices

### Don'ts ❌
- Don't animate everything (it distract)
- Don't use durations > 800ms except special cases
- Don't block user input during animations
- Don't overuse bounce/elastic effects
- Don't animate every item in a long list (performance)

## Performance Tips

1. **Use `remember` for animation states:**
   ```kotlin
   val scale = remember { Animatable(1f) }
   ```

2. **Limit concurrent animations:**
   ```kotlin
   // Good - max 3-4 staggered items
   itemsIndexed(items.take(4)) { index, item ->
       AnimatedListItem(delay = index * 50)
   }
   ```

3. **Test with Android Profiler:**
   - Check frame rate (should be 60 FPS)
   - Monitor memory usage
   - Look for jank or dropped frames

4. **Use `LaunchedEffect` for cleanup:**
   ```kotlin
   LaunchedEffect(Unit) {
       animation.animateTo(targetValue)
   }
   ```

## Testing Animations

### Visual Testing
```kotlin
// Test fade in
Box(modifier = Modifier.fadeInAnimation(duration = 300))

// Test scale in
Box(modifier = Modifier.scaleInAnimation(duration = 300))

// Test slide up
Box(modifier = Modifier.slideUpAnimation(duration = 300))
```

### Performance Testing
1. Open Android Profiler
2. Track fps while navigating pages
3. Monitor memory during animations
4. Check for dropped frames

### User Testing
- Test on slow devices
- Test with reduced motion enabled
- Get feedback on animation timing
- Verify no accessibility issues

## Rollout Strategy

### Phase 1: Priority Pages ⭐
1. Authentication screens (Login/Signup)
2. Patient Home dashboard
3. Doctor Home dashboard

### Phase 2: Secondary Pages
1. Schedule pages with animations
2. Appointment booking flow
3. Profile pages

### Phase 3: Polish
1. Bottom navigation animations
2. Page transitions
3. Loading states
4. Empty states
5. Error states

## Troubleshooting

### Animations Feel Slow
- Reduce duration by 50-100ms
- Use different easing (EaseInOutCubic)
- Check if on performance test device

### Animations Jitter
- Check for recomposition triggers
- Verify animations use immutable values
- Move state computation out of animation

### Animations Don't Appear
- Check `LaunchedEffect` triggers
- Verify animation values animate to different target
- Check modifier application order

### Performance Issues
- Reduce animated items count
- Use simpler animations (fade vs complex paths)
- Profile with Android Profiler
- Test on real devices

## Resources

- **Compose Animation Docs**: https://developer.android.com/jetpack/compose/animation
- **Material 3 Guide**: https://m3.material.io/
- **Healthcare UX Patterns**: Check design files in repo

---

**Last Updated**: April 2026
**Version**: 1.0
**Maintainer**: Design System Team
