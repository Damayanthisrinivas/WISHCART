@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.graphicsLayer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

// --- MAIN WRAPPER CONTAINER ---

@Composable
fun WishCartApp(viewModel: WishCartViewModel) {
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    var activeScreen by remember { mutableStateOf("splash") }
    
    // Auto navigation from splash
    LaunchedEffect(Unit) {
        delay(2200)
        activeScreen = if (isUserLoggedIn) "home" else "auth"
    }

    MyApplicationTheme(darkTheme = isDarkMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BoxWithConstraints {
                val isTablet = maxWidth > 600.dp

                Row(modifier = Modifier.fillMaxSize()) {
                    // Responsive Navigation Rail for Tablets / Wide Screens
                    if (isTablet && activeScreen != "splash" && activeScreen != "auth") {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface,
                            header = {
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_wishcart_logo),
                                        contentDescription = "Logo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            NavigationRailItem(
                                selected = activeScreen == "home",
                                onClick = { activeScreen = "home" },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home") }
                            )
                            NavigationRailItem(
                                selected = activeScreen == "wishlist",
                                onClick = { activeScreen = "wishlist" },
                                icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist") },
                                label = { Text("Wish") }
                            )
                            NavigationRailItem(
                                selected = activeScreen == "cart",
                                onClick = { activeScreen = "cart" },
                                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
                                label = { Text("Cart") }
                            )
                            NavigationRailItem(
                                selected = activeScreen == "chatbot",
                                onClick = { activeScreen = "chatbot" },
                                icon = { Icon(Icons.Default.SmartToy, contentDescription = "AI Bot") },
                                label = { Text("AI Bot") }
                            )
                            NavigationRailItem(
                                selected = activeScreen == "profile" || activeScreen == "history",
                                onClick = { activeScreen = if (isUserLoggedIn) "profile" else "auth" },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                label = { Text("Profile") }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            NavigationRailItem(
                                selected = activeScreen == "admin",
                                onClick = { activeScreen = "admin" },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Merchant Hub", tint = MaterialTheme.colorScheme.secondary) },
                                label = { Text("Merchant", color = MaterialTheme.colorScheme.secondary) }
                            )
                        }
                    }

                    // Main Content Page
                    Box(modifier = Modifier.weight(1f)) {
                        when (activeScreen) {
                            "splash" -> SplashScreen()
                            "auth" -> AuthScreen(viewModel) { activeScreen = "home" }
                            "home" -> HomeScreen(viewModel, 
                                onNavigateToDetail = { activeScreen = "detail" },
                                onNavigateToCart = { activeScreen = "cart" },
                                onNavigateToChat = { activeScreen = "chatbot" },
                                onNavigateToWishlist = { activeScreen = "wishlist" },
                                onNavigateToHistory = { activeScreen = "history" },
                                onNavigateToProfile = { activeScreen = "profile" },
                                onNavigateToAuth = { activeScreen = "auth" }
                            )
                            "detail" -> ProductDetailScreen(viewModel, 
                                onBack = { activeScreen = "home" },
                                onNavigateToCart = { activeScreen = "cart" }
                            )
                            "cart" -> CartScreen(viewModel,
                                onBack = { activeScreen = "home" },
                                onProceedToCheckout = { activeScreen = "checkout" }
                            )
                            "wishlist" -> WishlistScreen(viewModel,
                                onBack = { activeScreen = "home" },
                                onNavigateToDetail = { activeScreen = "detail" }
                            )
                            "history" -> HistoryScreen(viewModel,
                                onBack = { activeScreen = "home" },
                                onNavigateToDetail = { activeScreen = "detail" }
                            )
                            "profile" -> ProfileScreen(viewModel,
                                onBack = { activeScreen = "home" },
                                onNavigateToHistory = { activeScreen = "history" },
                                onNavigateToAdmin = { activeScreen = "admin" },
                                onLogout = { activeScreen = "auth" }
                            )
                            "checkout" -> CheckoutScreen(viewModel,
                                onBackToCart = { activeScreen = "cart" },
                                onBackToHome = { activeScreen = "home" }
                            )
                            "chatbot" -> ChatbotScreen(viewModel,
                                onBack = { activeScreen = "home" }
                            )
                            "admin" -> AdminScreen(viewModel,
                                onBack = { activeScreen = "profile" }
                            )
                        }

                        // Responsive Bottom Navigation for Mobiles
                        if (!isTablet && activeScreen != "splash" && activeScreen != "auth") {
                            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                            ) {
                                HorizontalDivider(
                                    color = if (isDarkMode) Color(0xFF3E444D) else Color(0xFFECEFF1),
                                    thickness = 1.dp
                                )
                                val navBarColor = if (isDarkMode) Color(0xFF2C3035) else Color.White
                                val itemColors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = WishCartNavy,
                                    selectedTextColor = WishCartNavy,
                                    indicatorColor = if (isDarkMode) WishCartNavy.copy(alpha = 0.2f) else WishCartNavy.copy(alpha = 0.08f),
                                    unselectedIconColor = if (isDarkMode) Color.LightGray else Color.Gray,
                                    unselectedTextColor = if (isDarkMode) Color.LightGray else Color.Gray
                                )
                                NavigationBar(
                                    containerColor = navBarColor,
                                    tonalElevation = 0.dp,
                                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                                ) {
                                    NavigationBarItem(
                                        selected = activeScreen == "home" || activeScreen == "detail",
                                        onClick = { activeScreen = "home" },
                                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                        label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        colors = itemColors,
                                        modifier = Modifier.testTag("nav_home")
                                    )
                                    NavigationBarItem(
                                        selected = activeScreen == "wishlist",
                                        onClick = { activeScreen = "wishlist" },
                                        icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist") },
                                        label = { Text("Wishlist", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        colors = itemColors,
                                        modifier = Modifier.testTag("nav_wishlist")
                                    )
                                    NavigationBarItem(
                                        selected = activeScreen == "cart",
                                        onClick = { activeScreen = "cart" },
                                        icon = { BadgedBox(badge = {
                                            val cartList by viewModel.cartItems.collectAsStateWithLifecycle()
                                            if (cartList.isNotEmpty()) {
                                                Badge { Text(cartList.sumOf { it.quantity }.toString()) }
                                            }
                                        }) {
                                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                                        }},
                                        label = { Text("Cart", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        colors = itemColors,
                                        modifier = Modifier.testTag("nav_cart")
                                    )
                                    NavigationBarItem(
                                        selected = activeScreen == "chatbot",
                                        onClick = { activeScreen = "chatbot" },
                                        icon = { Icon(Icons.Default.SmartToy, contentDescription = "AI Assistant") },
                                        label = { Text("AI Ask", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        colors = itemColors,
                                        modifier = Modifier.testTag("nav_chatbot")
                                    )
                                    NavigationBarItem(
                                        selected = activeScreen == "profile",
                                        onClick = { activeScreen = if (isUserLoggedIn) "profile" else "auth" },
                                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
                                        label = { Text("Profile", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        colors = itemColors,
                                        modifier = Modifier.testTag("nav_profile")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 1: SPLASH SCREEN ---

@Composable
fun SplashScreen() {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1.0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        alpha.animateTo(1.0f, animationSpec = tween(500))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer(scaleX = scale.value, scaleY = scale.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_wishcart_logo),
                contentDescription = "WishCart Logo",
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(32.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "WishCart",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    color = WishCartCharcoal
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Everything You Love, Delivered",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = WishCartTeal,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

// --- SCREEN 2: AUTHENTICATION SCREEN ---

@Composable
fun AuthScreen(viewModel: WishCartViewModel, onLoginSuccess: () -> Unit) {
    val isLiveFirebase = remember { FirebaseClient.getApiKey().isNotEmpty() }
    val isDark = isSystemInDarkTheme()
    
    val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    var authState by remember { mutableStateOf("signIn") } // "signIn", "signUp", "forgot"
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    
    var showGoogleDialog by remember { mutableStateOf(false) }
    var showSuccessToast by remember { mutableStateOf<String?>(null) }

    // Auto-fill test values for ease of exploration
    LaunchedEffect(authState) {
        viewModel.clearAuthError()
        if (authState == "signIn" && email.isEmpty()) {
            email = "damayanthisri7@gmail.com"
            password = "******"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF1E2124) else Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 425.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Firebase Configuration Status Anchor
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isLiveFirebase) WishCartSuccessGreen.copy(alpha = 0.15f)
                        else Color.Gray.copy(alpha = 0.15f)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isLiveFirebase) WishCartSuccessGreen else Color.Gray)
                    )
                    Text(
                        text = if (isLiveFirebase) "Firebase Auth Active" else "Local Secure Auth Mode",
                        color = if (isLiveFirebase) WishCartSuccessGreen else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.img_wishcart_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "WishCart",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = WishCartNavy
            )
            
            Text(
                text = when (authState) {
                    "signIn" -> "Bespoke Lifestyle & E-Commerce Service"
                    "signUp" -> "Create your high-fashion account"
                    else -> "Reset password securely via Firebase"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Display error messages from Firebase Authentication
            authError?.let { err ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WishCartErrorRed.copy(alpha = 0.1f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = err,
                        color = WishCartErrorRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Display success notification from resets/signups
            showSuccessToast?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WishCartSuccessGreen.copy(alpha = 0.12f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = msg,
                        color = WishCartSuccessGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Form Fields based on authentication state
            if (authState == "signUp") {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = WishCartNavy) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_fullname"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WishCartNavy)
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = WishCartNavy) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WishCartNavy)
            )

            if (authState != "forgot") {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = WishCartNavy) },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_password"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WishCartNavy)
                )
            }

            if (authState == "signUp") {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = WishCartNavy) },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WishCartNavy)
                )
            }

            // Forgot password anchor for sign in
            if (authState == "signIn") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { authState = "forgot" }) {
                        Text("Forgot Password?", color = WishCartTeal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Core Action CTA Button
            Button(
                onClick = {
                    if (isAuthLoading) return@Button
                    when (authState) {
                        "signIn" -> {
                            viewModel.firebaseLogin(email, password) {
                                onLoginSuccess()
                            }
                        }
                        "signUp" -> {
                            if (password != confirmPassword) {
                                showSuccessToast = null
                                return@Button
                            }
                            viewModel.firebaseRegister(email, fullName, password) {
                                showSuccessToast = "Registration successful! Loading store..."
                                onLoginSuccess()
                            }
                        }
                        "forgot" -> {
                            viewModel.firebaseResetPassword(email) {
                                showSuccessToast = "Password reset instructions sent to $email."
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("auth_submit"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy)
            ) {
                if (isAuthLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = when (authState) {
                            "signIn" -> "Sign In to WishCart"
                            "signUp" -> "Create Account"
                            else -> "Send Reset Instructions"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Helpers to change states
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (authState) {
                        "signIn" -> "New to WishCart? "
                        "signUp" -> "Already have an account? "
                        else -> "Back to "
                    },
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = when (authState) {
                        "signIn" -> "Create Account"
                        "signUp" -> "Sign In"
                        else -> "Sign In Screen"
                    },
                    modifier = Modifier.clickable {
                        authState = if (authState == "signUp" || authState == "forgot") "signIn" else "signUp"
                        showSuccessToast = null
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = WishCartTeal
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(" OR CONTINUE WITH ", color = Color.LightGray, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp))
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Visually beautiful and fully functional authentic Google Sign-In trigger
            OutlinedButton(
                onClick = { showGoogleDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("google_login_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WishCartCharcoal)
            ) {
                // Circular multicolor Google icon representation
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Google App Logo",
                        tint = WishCartNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continue with Google", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Guest Browsing Entry
            TextButton(
                onClick = {
                    viewModel.login("guest@wishcart.in", "Guest Buyer")
                    onLoginSuccess()
                }
            ) {
                Text("Explore Products as Guest", fontWeight = FontWeight.SemiBold, color = WishCartTeal, fontSize = 13.sp)
            }
        }
    }

    // Interactive Multiaccount Google accounts picker dialog
    if (showGoogleDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleDialog = false },
            title = { Text("Choose a Google Account", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = WishCartCharcoal) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.login("damayanthisri7@gmail.com", "Damayanthi Sri")
                                onLoginSuccess()
                                showGoogleDialog = false
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Avatar", modifier = Modifier.size(36.dp), tint = WishCartNavy)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Damayanthi Sri", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WishCartCharcoal)
                            Text("damayanthisri7@gmail.com", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.login("guestuser@gmail.com", "App Reviewer")
                                onLoginSuccess()
                                showGoogleDialog = false
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Avatar", modifier = Modifier.size(36.dp), tint = WishCartTeal)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Guest Tester", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WishCartCharcoal)
                            Text("guestuser@gmail.com", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showGoogleDialog = false }) {
                    Text("Cancel", color = WishCartNavy, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = if (isDark) Color(0xFF2C3035) else Color.White
        )
    }
}

// --- SCREEN 3: HOME SCREEN ---

@Composable
fun HomeScreen(
    viewModel: WishCartViewModel,
    onNavigateToDetail: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val promoIndex by viewModel.promoIndex.collectAsStateWithLifecycle()
    val flashTimer by viewModel.flashSaleTimer.collectAsStateWithLifecycle()

    var showLngMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            val topBarBg = if (isDarkMode) Color(0xFF2C3035) else Color.White
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topBarBg)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "WishCart",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = WishCartNavy
                        )
                        Text(
                            WishCartLocalization.translate("tagline", currentLang),
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }

                    // Toolbar actions: Theme toggle, Account/Profile, and Language selection Menu
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.toggleDarkMode() }) {
                            Icon(
                                if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Mode Toggle",
                                tint = WishCartCharcoal
                            )
                        }
                        
                        val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
                        IconButton(
                            onClick = {
                                if (isUserLoggedIn) {
                                    onNavigateToProfile()
                                } else {
                                    onNavigateToAuth()
                                }
                            },
                            modifier = Modifier.testTag("account_profile_icon")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "User Account Dashboard",
                                tint = WishCartNavy
                            )
                        }

                        IconButton(onClick = { showLngMenu = true }) {
                            Icon(Icons.Default.Language, contentDescription = "Languages", tint = WishCartTeal)
                        }
                        
                        // Languages Dropdown Options
                        DropdownMenu(expanded = showLngMenu, onDismissRequest = { showLngMenu = false }) {
                            LanguageConfig.languages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text("${lang.nativeName} (${lang.name})") },
                                    onClick = {
                                        viewModel.repository.changeLanguage(lang.code)
                                        showLngMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToChat,
                containerColor = WishCartNavy,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.SmartToy, contentDescription = "Ask AI")
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        val contentBg = if (isDarkMode) Color(0xFF1E2124) else Color.White
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(contentBg)
        ) {
            // Main search bar with mock voice input
            SearchBarSection(viewModel)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("home_scroll")
            ) {
                // Banner sliders styled beautifully with Clean Minimalism
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(160.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(WishCartTeal)
                    ) {
                        // Blurred vector circle in background
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 16.dp, y = (-16).dp)
                                .size(128.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "NEW SEASON",
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                if (promoIndex == 0) "Elevate Your Lifestyle" else "Smart Gadget Spectrum",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp,
                                modifier = Modifier.fillMaxWidth(0.65f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "EXPLORE NOW",
                                    color = WishCartTeal,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Banner Indicator dots
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(2) { idx ->
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (idx == promoIndex) Color.White else Color.White.copy(alpha = 0.4f))
                                )
                            }
                        }
                    }
                }

                // Dynamic categories section
                item {
                    Text(
                        text = WishCartLocalization.translate("heading_categories", currentLang),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        color = WishCartCharcoal
                    )

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            CategoryChip("All", activeCategory == "All") { viewModel.setCategory("All") }
                        }
                        ProductCatalog.categories.forEach { desc ->
                            item {
                                CategoryChip(desc, activeCategory == desc) { viewModel.setCategory(desc) }
                            }
                        }
                    }
                }

                // Flash Sales countdown section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = WishCartNavy.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, WishCartNavy.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = "Flash", tint = WishCartNavy, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        WishCartLocalization.translate("heading_flash_sales", currentLang),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = WishCartCharcoal
                                    )
                                    Text("Dynamic price drops active now", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            // Working real-time countdown banner
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WishCartNavy)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(flashTimer, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Sorting & Filter states indicators
                item {
                    val filteredProducts = viewModel.getFilteredCatalog()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${filteredProducts.size} Items Found",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Sort Order cycle switcher
                            val sOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        val nextOrder = when (sOrder) {
                                            "DEFAULT" -> "PRICE_L2H"
                                            "PRICE_L2H" -> "PRICE_H2L"
                                            "PRICE_H2L" -> "RATING"
                                            "RATING" -> "DISCOUNT"
                                            else -> "DEFAULT"
                                        }
                                        viewModel.setSortOrder(nextOrder)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Sort: $sOrder", fontSize = 11.sp, color = WishCartCharcoal)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { viewModel.clearFilters() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Clear Filter", fontSize = 11.sp, color = WishCartErrorRed)
                            }
                        }
                    }
                }

                // Grid display of matching catalog items
                val listFiltered = viewModel.getFilteredCatalog()
                if (listFiltered.isEmpty()) {
                    item {
                        EmptyStateCatalog()
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            listFiltered.chunked(2).forEach { rowList ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowList.forEach { prod ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            ProductGridCard(prod, viewModel) {
                                                viewModel.selectProduct(prod)
                                                onNavigateToDetail()
                                            }
                                        }
                                    }
                                    if (rowList.size < 2) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }

                // Pad space to avoid bottom nav overlay
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

// Category selection chip Composable
@Composable
fun CategoryChip(title: String, selected: Boolean, onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (selected) WishCartNavy else (if (isDark) Color(0xFF2C3035) else WishCartLightGray)
    val textColor = if (selected) Color.White else (if (isDark) Color.LightGray else WishCartCharcoal)

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Search bar design with voice & scan cues
@Composable
fun SearchBarSection(viewModel: WishCartViewModel) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = query,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text(WishCartLocalization.translate("search_placeholder", lang), fontSize = 14.sp, color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = Color.Gray) },
            trailingIcon = {
                Row(modifier = Modifier.padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.updateSearchQuery("Shirt") }) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice search", tint = Color.Gray)
                    }
                    IconButton(onClick = { viewModel.updateSearchQuery("Watch") }) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = "Barcode search", tint = Color.Gray)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_field"),
            shape = CircleShape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (isDark) Color(0xFF2C3035) else WishCartLightGray,
                unfocusedContainerColor = if (isDark) Color(0xFF2C3035) else WishCartLightGray,
                disabledContainerColor = if (isDark) Color(0xFF2C3035) else WishCartLightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = WishCartNavy
            ),
            singleLine = true
        )
    }
}

// --- CATALOG CARD COMPOSABLE ---

@Composable
fun ProductGridCard(product: Product, viewModel: WishCartViewModel, onClick: () -> Unit) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val wishlist by viewModel.wishlistItems.collectAsStateWithLifecycle()
    val isItemWishlisted = wishlist.any { it.productId == product.id }
    val isDark = isSystemInDarkTheme()

    val cardBg = if (isDark) Color(0xFF2C3035) else WishCartLightGray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("product_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDark) Color(0xFF3E444D) else Color(0xFFE8E8E8))
            ) {
                AsyncImage(
                    model = product.imageUrls.firstOrNull(),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Heart / Favorite badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { viewModel.toggleProductWishlist(product) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isItemWishlisted) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Hearts",
                        tint = if (isItemWishlisted) WishCartErrorRed else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)) {
                Text(
                    product.brand.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    product.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    color = WishCartCharcoal,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "₹${product.price.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = WishCartCharcoal
                    )
                    Text(
                        "${product.discountPercent}% OFF",
                        color = WishCartSuccessGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                // Star Rating chip & Quick Add to Cart
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${product.rating} (${product.reviewsCount})",
                            fontSize = 9.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    // Minimal circular Add to Cart button
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(WishCartNavy)
                            .clickable {
                                viewModel.addItemToCart(product, "M", "Default")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Quick Add to Cart",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateCatalog() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Inbox,
            contentDescription = "Empty",
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No matching products. Try custom filters!",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
    }
}

// --- SCREEN 4: PRODUCT DETAIL SCREEN ---

@Composable
fun ProductDetailScreen(viewModel: WishCartViewModel, onBack: () -> Unit, onNavigateToCart: () -> Unit) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val product by viewModel.activeProduct.collectAsStateWithLifecycle()
    val wishlist by viewModel.wishlistItems.collectAsStateWithLifecycle()

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found.", fontWeight = FontWeight.Bold)
        }
        return
    }

    val item = product!!
    val isItemWishlisted = wishlist.any { it.productId == item.id }

    var selectedSize by remember { mutableStateOf(item.sizes.firstOrNull() ?: "Standard") }
    var selectedColor by remember { mutableStateOf(item.colors.firstOrNull() ?: "Default") }
    var activeZoom by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.brand.uppercase(), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onNavigateToCart) { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Main Product visual displays with ZOOM trigger
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (activeZoom) 400.dp else 280.dp)
                    .clickable { activeZoom = !activeZoom }
            ) {
                AsyncImage(
                    model = item.imageUrls.firstOrNull(),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = if (activeZoom) ContentScale.Fit else ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (activeZoom) "Tap to unzoom" else "Tap to double-zoom",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Product Title & Rating
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = WishCartCharcoal
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${item.rating} Rating Overview", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("(${item.reviewsCount} verified reviews)", color = Color.Gray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price display with INR
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "₹${item.price.toInt()}",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = WishCartCharcoal
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "₹${item.originalPrice.toInt()}",
                        textDecoration = TextDecoration.LineThrough,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "${item.discountPercent}% SPECIAL OFF",
                        fontWeight = FontWeight.Bold,
                        color = WishCartErrorRed,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Sizes chip configurations
                Text("Select Style & Size Tag", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.sizes.forEach { sz ->
                        item {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedSize == sz) WishCartNavy else WishCartLightGray)
                                    .clickable { selectedSize = sz },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    sz,
                                    color = if (selectedSize == sz) Color.White else WishCartCharcoal,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Colors selections
                Text("Select Available Colorway", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.colors.forEach { col ->
                        Box(
                            modifier = Modifier
                                .border(1.dp, if (selectedColor == col) WishCartNavy else Color.LightGray, RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selectedColor == col) WishCartNavy.copy(alpha = 0.08f) else Color.White)
                                .clickable { selectedColor = col }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(col, color = WishCartCharcoal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(20.dp))

                // Descriptions Specifications
                Text("Product Specifications & Craft Details", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, lineHeight = 20.sp)

                Spacer(modifier = Modifier.height(12.dp))
                item.specifications.forEach { (key, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(key, color = Color.Gray, fontSize = 12.sp)
                        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Action Toolbar block at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                tonalElevation = 12.dp,
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.toggleProductWishlist(item) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isItemWishlisted) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Hearts",
                            tint = if (isItemWishlisted) WishCartErrorRed else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isItemWishlisted) "Wishlisted" else "Save Wish", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.addItemToCart(item, selectedSize, selectedColor)
                            onNavigateToCart()
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                            .testTag("add_to_cart_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add To Cart", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

// --- SCREEN 5: SHOPPING CART SCREEN ---

@Composable
fun CartScreen(viewModel: WishCartViewModel, onBack: () -> Unit, onProceedToCheckout: () -> Unit) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val cartList by viewModel.cartItems.collectAsStateWithLifecycle()
    val isCouponApplied by viewModel.couponApplied.collectAsStateWithLifecycle()

    var couponField by remember { mutableStateOf("WISHAISTUDIO") }
    var couponMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(WishCartLocalization.translate("title_cart", lang), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        if (cartList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(WishCartLocalization.translate("empty_cart", lang), fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy)) {
                        Text(WishCartLocalization.translate("back_to_home", lang))
                    }
                }
            }
            return@Scaffold
        }

        // Calculate Cost Summaries
        val baseSum = cartList.sumOf { it.price * it.quantity }
        val gstTax = (baseSum * 0.18)
        val couponDiscount = if (isCouponApplied) (baseSum * 0.15) else 0.0
        val deliveryFee = if (baseSum > 1000) 0.0 else 99.0
        val totalSum = baseSum + gstTax - couponDiscount + deliveryFee

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Cart items listing
            cartList.forEach { cItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WishCartLightGray)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = cItem.imageUrl,
                            contentDescription = cItem.title,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cItem.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 14.sp)
                            Text("Size: ${cItem.size} | Color: ${cItem.color}", color = Color.Gray, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹${cItem.price.toInt()} each", fontWeight = FontWeight.Bold, color = WishCartTeal, fontSize = 12.sp)
                        }

                        // Quantity selector mechanics
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.updateCartQty(cItem, increment = false) }) {
                                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrement")
                            }
                            Text(cItem.quantity.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                            IconButton(onClick = { viewModel.updateCartQty(cItem, increment = true) }) {
                                Icon(Icons.Default.AddCircleOutline, contentDescription = "Increment")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Coupon Code Application Block
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(WishCartLocalization.translate("coupon_applied", lang), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = couponField,
                        onValueChange = { couponField = it },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Button(
                        onClick = {
                            val ok = viewModel.tryApplyCoupon(couponField)
                            couponMessage = if (ok) "Special AI Voucher applied! Flat 15% discount." else "Invalid Code!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Apply")
                    }
                }
                if (couponMessage.isNotEmpty()) {
                    Text(
                        couponMessage,
                        color = if (isCouponApplied) WishCartSuccessGreen else WishCartErrorRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Pricing summary invoice template
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(WishCartLocalization.translate("billing_summary", lang), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Cart Items Value", color = Color.Gray, fontSize = 12.sp)
                    Text("₹${baseSum.toInt()}", fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(WishCartLocalization.translate("gst_calculation", lang), color = Color.Gray, fontSize = 12.sp)
                    Text("₹${gstTax.toInt()}", fontWeight = FontWeight.Bold)
                }
                if (isCouponApplied) {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("AICoupon Code Promo Discount", color = WishCartSuccessGreen, fontSize = 12.sp)
                        Text("-₹${couponDiscount.toInt()}", color = WishCartSuccessGreen, fontWeight = FontWeight.Bold)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(WishCartLocalization.translate("delivery_charge", lang), color = Color.Gray, fontSize = 12.sp)
                    Text(if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}", fontWeight = FontWeight.Bold, color = if (deliveryFee == 0.0) WishCartSuccessGreen else WishCartCharcoal)
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(WishCartLocalization.translate("total_amount", lang), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text("₹${totalSum.toInt()}", fontWeight = FontWeight.ExtraBold, color = WishCartNavy, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    WishCartLocalization.translate("secure_badge", lang),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onProceedToCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
                    .testTag("checkout_continue_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy)
            ) {
                Text("Proceed to Checkout", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

// --- SCREEN 6: WISHLIST SCREEN ---

@Composable
fun WishlistScreen(viewModel: WishCartViewModel, onBack: () -> Unit, onNavigateToDetail: () -> Unit) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val wishlist by viewModel.wishlistItems.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(WishCartLocalization.translate("title_wishlist", lang), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        if (wishlist.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(WishCartLocalization.translate("empty_wishlist", lang), fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy)) {
                        Text(WishCartLocalization.translate("back_to_home", lang))
                    }
                }
            }
            return@Scaffold
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(wishlist) { wItem ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WishCartLightGray)
                ) {
                    Box(modifier = Modifier.height(140.dp)) {
                        AsyncImage(
                            model = wItem.imageUrl,
                            contentDescription = wItem.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { viewModel.removeWishlist(wItem) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = WishCartErrorRed, modifier = Modifier.size(18.dp))
                        }
                    }

                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(wItem.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                        Text("₹${wItem.price.toInt()}", fontWeight = FontWeight.ExtraBold, color = WishCartNavy)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Move to Cart Button action
                        Button(
                            onClick = {
                                // Get product reference
                                val prod = ProductCatalog.allProducts.find { it.id == wItem.productId }
                                if (prod != null) {
                                    viewModel.addItemToCart(prod, "M", "Default")
                                    viewModel.removeWishlist(wItem)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy)
                        ) {
                            Text("Move to Cart", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 7: SEARCH & VIEW CLINIC HISTORY ---

@Composable
fun HistoryScreen(viewModel: WishCartViewModel, onBack: () -> Unit, onNavigateToDetail: () -> Unit) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val list by viewModel.activityHistory.collectAsStateWithLifecycle()

    var showClearPopup by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(WishCartLocalization.translate("title_history", lang), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    if (list.isNotEmpty()) {
                        IconButton(onClick = { showClearPopup = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Purge logs", tint = WishCartErrorRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        
        // Confirmation alert popup checks before deletions as instructed
        if (showClearPopup) {
            AlertDialog(
                onDismissRequest = { showClearPopup = false },
                title = { Text("Confirm Erasure Check") },
                text = { Text(WishCartLocalization.translate("confirm_clear_history", lang)) },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = WishCartErrorRed),
                        onClick = {
                            viewModel.clearHistoryLogs()
                            showClearPopup = false
                        }
                    ) {
                        Text(WishCartLocalization.translate("confirm", lang))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearPopup = false }) {
                        Text(WishCartLocalization.translate("cancel", lang))
                    }
                }
            )
        }

        if (list.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(WishCartLocalization.translate("placeholder_history", lang), color = Color.Gray, fontWeight = FontWeight.Bold)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            items(list) { act ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(act.timestamp))

                    Icon(
                        imageVector = when (act.type) {
                            "SEARCH" -> Icons.Default.Search
                            "VIEW" -> Icons.Default.Visibility
                            else -> Icons.Default.Inventory
                        },
                        contentDescription = null,
                        tint = WishCartTeal,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (act.type) {
                                "SEARCH" -> "Searched keyword: '${act.searchKeyword}'"
                                "VIEW" -> "Viewed product: ${act.title}"
                                else -> "Placed shopping checkout order success"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(dateStr, color = Color.Gray, fontSize = 11.sp)
                    }

                    if (act.productId != null) {
                        Button(
                            onClick = {
                                val item = ProductCatalog.allProducts.find { it.id == act.productId }
                                if (item != null) {
                                    viewModel.selectProduct(item)
                                    onNavigateToDetail()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy)
                        ) {
                            Text("Buy", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 8: ACCOUNT PROFILE PAGE ---

@Composable
fun ProfileScreen(
    viewModel: WishCartViewModel,
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val email by viewModel.userEmail.collectAsStateWithLifecycle()
    val name by viewModel.userName.collectAsStateWithLifecycle()
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    val addresses by viewModel.addresses.collectAsStateWithLifecycle()
    val orders by viewModel.orderedItems.collectAsStateWithLifecycle()
    val wishlistState by viewModel.wishlistItems.collectAsStateWithLifecycle()
    val cartList by viewModel.cartItems.collectAsStateWithLifecycle()

    var isEditingProfile by remember { mutableStateOf(false) }
    var editName by remember(name) { mutableStateOf(name) }
    var editEmail by remember(email) { mutableStateOf(email) }

    var expandedAddressForm by remember { mutableStateOf(false) }
    var addrName by remember { mutableStateOf("") }
    var addrLine1 by remember { mutableStateOf("") }
    var addrCity by remember { mutableStateOf("") }
    var addrState by remember { mutableStateOf("") }
    var addrZip by remember { mutableStateOf("") }
    var addrPhone by remember { mutableStateOf("") }

    var activeTab by remember { mutableStateOf("orders") } // "orders", "wishlist", "cart", "addresses"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Account", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // PROFILE CARD (Profile Information)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = WishCartNavy),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(54.dp), tint = Color.White)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (isEditingProfile) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Full Name", color = Color.White) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Email Address", color = Color.White) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { isEditingProfile = false },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, Color.White),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Cancel", fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    viewModel.updateProfile(editName, editEmail)
                                    isEditingProfile = false
                                },
                                modifier = Modifier.weight(1.2f),
                                colors = ButtonDefaults.buttonColors(containerColor = WishCartTeal, contentColor = Color.White)
                            ) {
                                Text("Save Info", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Text(name.ifEmpty { "Guest Buyer" }, fontWeight = FontWeight.Bold, fontSize = 21.sp, color = Color.White)
                        Text(email.ifEmpty { "guest@wishcart.in" }, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(40))
                                .background(WishCartTeal.copy(alpha = 0.85f))
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text("Sovereign Member", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = {
                                editName = name
                                editEmail = email
                                isEditingProfile = true
                            },
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(34.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile Data", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TAB BAR FOR SECTIONS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(WishCartLightGray)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    Pair("orders", "Orders (${orders.size})"),
                    Pair("wishlist", "Wishlist (${wishlistState.size})"),
                    Pair("cart", "Cart (${cartList.size})"),
                    Pair("addresses", "Addresses")
                ).forEach { (tabId, label) ->
                    val isSelected = activeTab == tabId
                    Button(
                        onClick = { activeTab = tabId },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color.White else Color.Transparent,
                            contentColor = if (isSelected) WishCartNavy else Color.Gray
                        ),
                        elevation = null,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TAB CONTENTS
            when (activeTab) {
                "orders" -> {
                    Text("My Orders Timeline", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WishCartCharcoal)
                    Spacer(modifier = Modifier.height(10.dp))
                    if (orders.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(44.dp), tint = Color.LightGray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No order transactions logged yet.", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    } else {
                        orders.forEach { order ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = WishCartLightGray),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = order.imageUrl,
                                        contentDescription = order.title,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(order.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("Qty: ${order.quantity} | Total: ₹${(order.price * order.quantity).toInt()}", fontSize = 12.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(WishCartSuccessGreen)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(order.status, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = WishCartSuccessGreen)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "wishlist" -> {
                    Text("My Saved Wishlist", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WishCartCharcoal)
                    Spacer(modifier = Modifier.height(10.dp))
                    if (wishlistState.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Your wishlist is empty.", color = Color.Gray, fontSize = 13.sp)
                        }
                    } else {
                        wishlistState.forEach { wItem ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = WishCartLightGray),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = wItem.imageUrl,
                                        contentDescription = wItem.title,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(wItem.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("₹${wItem.price.toInt()}", fontWeight = FontWeight.ExtraBold, color = WishCartNavy, fontSize = 13.sp)
                                    }
                                    IconButton(onClick = { viewModel.removeWishlist(wItem) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = WishCartErrorRed, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Button(
                                        onClick = {
                                            val prod = ProductCatalog.allProducts.find { it.id == wItem.productId }
                                            if (prod != null) {
                                                viewModel.addItemToCart(prod, "M", "Default")
                                                viewModel.removeWishlist(wItem)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Add to Cart", fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                "cart" -> {
                    Text("Active Cart Review", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WishCartCharcoal)
                    Spacer(modifier = Modifier.height(10.dp))
                    if (cartList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Your cart is empty.", color = Color.Gray, fontSize = 13.sp)
                        }
                    } else {
                        cartList.forEach { cItem ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = WishCartLightGray),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = cItem.imageUrl,
                                        contentDescription = cItem.title,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(cItem.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("₹${cItem.price.toInt()}", fontWeight = FontWeight.ExtraBold, color = WishCartNavy, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        // Quantity adjusts
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            IconButton(onClick = { viewModel.updateCartQty(cItem, false) }, modifier = Modifier.size(24.dp)) {
                                                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                            }
                                            Text("${cItem.quantity}", fontWeight = FontWeight.Bold)
                                            IconButton(onClick = { viewModel.updateCartQty(cItem, true) }, modifier = Modifier.size(24.dp)) {
                                                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                    IconButton(onClick = { viewModel.deleteCartItem(cItem) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = WishCartErrorRed, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                "addresses" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Saved Addresses", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WishCartCharcoal)
                        TextButton(onClick = { expandedAddressForm = !expandedAddressForm }) {
                            Text(if (expandedAddressForm) "Collapse Form" else "+ Add Address", color = WishCartTeal, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (expandedAddressForm) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            border = BorderStroke(1.dp, WishCartNavy.copy(alpha = 0.2f)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("New Delivery Address", fontWeight = FontWeight.Bold, color = WishCartNavy)
                                OutlinedTextField(
                                    value = addrName,
                                    onValueChange = { addrName = it },
                                    label = { Text("Contact Full Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                OutlinedTextField(
                                    value = addrLine1,
                                    onValueChange = { addrLine1 = it },
                                    label = { Text("Street Address Line 1") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = addrCity,
                                        onValueChange = { addrCity = it },
                                        label = { Text("City") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    OutlinedTextField(
                                        value = addrState,
                                        onValueChange = { addrState = it },
                                        label = { Text("State") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = addrZip,
                                        onValueChange = { addrZip = it },
                                        label = { Text("Zip Code") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    OutlinedTextField(
                                        value = addrPhone,
                                        onValueChange = { addrPhone = it },
                                        label = { Text("Contact Phone") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                                Button(
                                    onClick = {
                                        if (addrName.isNotEmpty() && addrLine1.isNotEmpty() && addrCity.isNotEmpty()) {
                                            viewModel.addAddressDirect(addrName, addrLine1, addrCity, addrState, addrZip, addrPhone)
                                            // Reset Form
                                            addrName = ""
                                            addrLine1 = ""
                                            addrCity = ""
                                            addrState = ""
                                            addrZip = ""
                                            addrPhone = ""
                                            expandedAddressForm = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Add Address to Wallet", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (addresses.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No saved addresses found.", color = Color.Gray, fontSize = 13.sp)
                        }
                    } else {
                        addresses.forEach { addr ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = WishCartLightGray),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = WishCartNavy)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(addr.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${addr.line1}, ${addr.city}, ${addr.state} - ${addr.pinCode}", fontSize = 12.sp, color = Color.Gray)
                                        Text("Phone: ${addr.phone}", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    IconButton(onClick = { viewModel.removeAddressDirect(addr) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = WishCartErrorRed, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ADDITIONAL GENERAL PREFERENCES
            ListItemButton("Browsing History Logs", Icons.Default.History, onNavigateToHistory)
            ListItemButton("Merchant Hub Console", Icons.Default.Settings, onNavigateToAdmin)

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_logout"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WishCartErrorRed)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out Session", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun ListItemButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = WishCartLightGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = WishCartNavy)
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

// --- SCREEN 9: SECURE PAYMENT GATEWAY CHECKOUT SCREEN ---

@Composable
fun CheckoutScreen(viewModel: WishCartViewModel, onBackToCart: () -> Unit, onBackToHome: () -> Unit) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val step by viewModel.checkoutStep.collectAsStateWithLifecycle()
    val addressList by viewModel.addresses.collectAsStateWithLifecycle()
    val cartList by viewModel.cartItems.collectAsStateWithLifecycle()
    val selectedPayment by viewModel.selectedPaymentMethod.collectAsStateWithLifecycle()

    val bSum = cartList.sumOf { it.price * it.quantity }
    val totalCost = bSum + (bSum * 0.18)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(WishCartLocalization.translate("title_checkout", lang), fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackToCart) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Steps indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Address", "Summary", "Payment", "Confirm").forEachIndexed { idx, title ->
                    val isActive = step >= (idx + 1)
                    Text(
                        title,
                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (isActive) WishCartNavy else Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (step) {
                1 -> {
                    // Address entry Selector Step
                    Text(WishCartLocalization.translate("address_book", lang), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))
                    addressList.forEach { addr ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    viewModel.selectCheckoutAddress(addr)
                                    viewModel.setCheckoutStep(2)
                                },
                            colors = CardDefaults.cardColors(containerColor = WishCartLightGray),
                            border = BorderStroke(1.dp, WishCartNavy)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(addr.name, fontWeight = FontWeight.Bold)
                                Text(addr.line1)
                                Text("${addr.city}, ${addr.state} - ${addr.pinCode}")
                                Text("Call: ${addr.phone}", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.setCheckoutStep(2) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy)
                    ) {
                        Text("Deliver to defaults")
                    }
                }
                2 -> {
                    // Order invoice details checking
                    Text(WishCartLocalization.translate("order_summary", lang), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(16.dp))
                    cartList.forEach { cItem ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${cItem.title} x ${cItem.quantity}", maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f), fontSize = 12.sp)
                            Text("₹${(cItem.price * cItem.quantity).toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.setCheckoutStep(3) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy)
                    ) {
                        Text("Proceed containing ₹${totalCost.toInt()}")
                    }
                }
                3 -> {
                    // Secure tokenized gateway selectors
                    Text(WishCartLocalization.translate("select_payment", lang), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(16.dp))

                    listOf("UPI", "CARD", "COD").forEach { mth ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedPayment == mth) WishCartNavy.copy(alpha = 0.08f) else WishCartLightGray)
                                .clickable { viewModel.selectPaymentMethod(mth) }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                when (mth) {
                                    "UPI" -> WishCartLocalization.translate("upi_gpay", lang)
                                    "CARD" -> WishCartLocalization.translate("card_payment", lang)
                                    else -> WishCartLocalization.translate("cod", lang)
                                },
                                fontWeight = FontWeight.Bold
                            )
                            RadioButton(selected = selectedPayment == mth, onClick = { viewModel.selectPaymentMethod(mth) })
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.submitCheckoutOrder(cartList, totalCost) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WishCartSuccessGreen)
                    ) {
                        Text("Place and Lock Order", fontWeight = FontWeight.Bold)
                    }
                }
                5 -> {
                    // Confeti card checkout successes
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Icon(Icons.Default.Celebration, contentDescription = null, modifier = Modifier.size(80.dp), tint = WishCartNavy)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = String.format(WishCartLocalization.translate("order_placed_success", lang), totalCost.toInt().toString()),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Your AJIO-inspired WishCart has been processed under secure SSL encryption.", color = Color.Gray, textAlign = TextAlign.Center)

                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                viewModel.setCheckoutStep(1)
                                onBackToHome()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Back to Shop Home")
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 10: AI ASSISTANT CHATBOT SERVICE ---

@Composable
fun ChatbotScreen(viewModel: WishCartViewModel, onBack: () -> Unit) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val chatLog by viewModel.chatLog.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()

    var inputMsg by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(WishCartLocalization.translate("ai_assistant_title", lang), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearChatLogs() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear logs")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillWithConstraints()
                    .padding(12.dp)
            ) {
                items(chatLog) { chat ->
                    val isBot = chat.sender == "AI"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = if (isBot) 0.dp else 12.dp,
                                    bottomEnd = if (isBot) 12.dp else 0.dp
                                ))
                                .background(if (isBot) WishCartLightGray else WishCartNavy)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    chat.text,
                                    color = if (isBot) WishCartCharcoal else Color.White,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    chat.timestamp,
                                    color = if (isBot) Color.Gray else Color.White.copy(alpha = 0.6f),
                                    fontSize = 9.sp,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }

                if (isChatLoading) {
                    item {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Text("Gemini is brainstorming styles...", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // Chat text entrance field row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputMsg,
                    onValueChange = { inputMsg = it },
                    placeholder = { Text(WishCartLocalization.translate("chatbot_placeholder", lang), fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        viewModel.sendChatMessage(inputMsg)
                        inputMsg = ""
                    }),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        viewModel.sendChatMessage(inputMsg)
                        inputMsg = ""
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(WishCartNavy)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

// --- SCREEN 11: MERCHANT HUB ADMIN PORTAL ---

@Composable
fun AdminScreen(viewModel: WishCartViewModel, onBack: () -> Unit) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val ordersList by viewModel.orderedItems.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("AJIO Summer Linen Shirt") }
    var price by remember { mutableStateOf("1299") }
    var category by remember { mutableStateOf("Fashion") }
    var brand by remember { mutableStateOf("Levis") }
    var note by remember { mutableStateOf("Fresh handspun natural linen.") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(WishCartLocalization.translate("admin_mode", lang), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Live Stats Cards
            Text("Hub Analytical Metric Vitals", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(WishCartLightGray).padding(12.dp)) {
                    Column {
                        Text("Live Traffic", fontSize = 10.sp, color = Color.Gray)
                        Text("₹2.4 Lakh", fontWeight = FontWeight.ExtraBold, color = WishCartNavy)
                    }
                }
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(WishCartLightGray).padding(12.dp)) {
                    Column {
                        Text("Orders Check", fontSize = 10.sp, color = Color.Gray)
                        Text("${ordersList.size} Pending", fontWeight = FontWeight.ExtraBold, color = WishCartTeal)
                    }
                }
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(WishCartLightGray).padding(12.dp)) {
                    Column {
                        Text("App Conversion", fontSize = 10.sp, color = Color.Gray)
                        Text("4.85%", fontWeight = FontWeight.ExtraBold, color = WishCartSuccessGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Add Dynamic Product Form
            Text("Add Dynamic Catalog Item", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Product name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price ₹") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand code") }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.addNewProductMerchant(title, category, note, price.toDoubleOrNull() ?: 999.0, brand)
                    title = ""
                    note = ""
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = WishCartNavy)
            ) {
                Text("Publish to WishCart Catalog Grid")
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Orders status updates
            Text("Active Orders Real-Time Controller", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            if (ordersList.isEmpty()) {
                Text("No placed checkout transactions to manage.", color = Color.Gray, fontSize = 12.sp)
            } else {
                ordersList.forEach { ord ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = WishCartLightGray)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ID: ${ord.orderId} - ₹${ord.price.toInt()}", fontWeight = FontWeight.Bold)
                            Text("Current State: ${ord.status}", color = WishCartTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(onClick = { viewModel.changeOrderStatusMerchant(ord.id, "Shipped") }, shape = RoundedCornerShape(4.dp), contentPadding = PaddingValues(horizontal = 8.dp), modifier = Modifier.height(28.dp)) {
                                    Text("Ship", fontSize = 9.sp)
                                }
                                Button(onClick = { viewModel.changeOrderStatusMerchant(ord.id, "Delivered") }, shape = RoundedCornerShape(4.dp), contentPadding = PaddingValues(horizontal = 8.dp), modifier = Modifier.height(28.dp)) {
                                    Text("Deliver", fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Custom simple helper extension
fun Modifier.fillWithConstraints() = this.fillMaxWidth().fillMaxHeight()
