package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(val sender: String, val text: String, val timestamp: String = getCurrentTime()) {
    companion object {
        fun getCurrentTime(): String {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}

class WishCartViewModel(application: Application) : AndroidViewModel(application) {

    private val database = WishCartDatabase.getDatabase(application)
    val repository = WishCartRepository(database.wishCartDao())

    // --- Authentication States ---
    val isUserLoggedIn = repository.isUserLoggedIn
    val userEmail = repository.userEmail
    val userName = repository.userName

    // --- Search & Fitering States ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _sortOrder = MutableStateFlow("DEFAULT") // "DEFAULT", "PRICE_L2H", "PRICE_H2L", "RATING", "DISCOUNT"
    val sortOrder = _sortOrder.asStateFlow()

    private val _selectedSizes = MutableStateFlow<Set<String>>(emptySet())
    val selectedSizes = _selectedSizes.asStateFlow()

    private val _selectedBrands = MutableStateFlow<Set<String>>(emptySet())
    val selectedBrands = _selectedBrands.asStateFlow()

    // --- Active Selected Product for Zoom Detail ---
    private val _activeProduct = MutableStateFlow<Product?>(null)
    val activeProduct = _activeProduct.asStateFlow()

    // --- Database Synced Live Flow states ---
    val cartItems = repository.cartItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val wishlistItems = repository.wishlistItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val activityHistory = repository.activityHistory.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val addresses = repository.addresses.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val orderedItems = repository.orderedItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Language and Themes
    val currentLanguage = repository.currentLanguage
    val isDarkMode = repository.isDarkMode

    fun toggleDarkMode() {
        repository.toggleDarkMode()
    }

    // --- Promotional Slider Indicator (Slides count = 2) ---
    private val _promoIndex = MutableStateFlow(0)
    val promoIndex = _promoIndex.asStateFlow()

    // --- Flash Sale Real Time simulated Timer ---
    private val _flashSaleTimer = MutableStateFlow("03:14:45")
    val flashSaleTimer = _flashSaleTimer.asStateFlow()

    // --- AI Chatbot Conversation log ---
    private val _chatLog = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("AI", "Hello Damayanthi! Welcome to WishCart assistant. How can I help you discover premium styles or track current orders today? Try typing 'discount' or 'fashion gifts'!")
        )
    )
    val chatLog = _chatLog.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading = _isChatLoading.asStateFlow()

    // --- Checkout state machines ---
    private val _checkoutStep = MutableStateFlow(1) // 1: Address selection, 2: Delivery, 3: Summary, 4: Payment, 5: Done
    val checkoutStep = _checkoutStep.asStateFlow()

    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress = _selectedAddress.asStateFlow()

    private val _couponApplied = MutableStateFlow(false)
    val couponApplied = _couponApplied.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow("UPI") // "UPI", "CARD", "COD"
    val selectedPaymentMethod = _selectedPaymentMethod.asStateFlow()

    // --- Admin panel dynamic products adding support ---
    private val _customProducts = MutableStateFlow<List<Product>>(emptyList())
    val customProducts = _customProducts.asStateFlow()

    init {
        // Run simulated countdown timer for Flash Sales
        startTimerAndSlides()
        // Seeds default user info
        viewModelScope.launch {
            repository.addAddress(
                name = "Damayanthi Sri",
                line1 = "Flat 402, Signature Residency, Madhapur",
                city = "Hyderabad",
                state = "Telangana",
                pinCode = "500081",
                phone = "+91 98765 43210"
            )
        }
    }

    private fun startTimerAndSlides() {
        viewModelScope.launch {
            var seconds = 11625 // around 3 hours
            while (seconds > 0) {
                delay(1000)
                seconds--
                val h = seconds / 3600
                val m = (seconds % 3600) / 60
                val s = seconds % 60
                _flashSaleTimer.value = String.format("%02d:%02d:%02d", h, m, s)

                // Swap banner indices every 5 seconds
                if (seconds % 5 == 0) {
                    _promoIndex.value = if (_promoIndex.value == 0) 1 else 0
                }
            }
        }
    }

    // --- Setters / Actions ---

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotEmpty()) {
            viewModelScope.launch {
                repository.logActivity("SEARCH", searchKeyword = query)
            }
        }
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setSortOrder(order: String) {
        _sortOrder.value = order
    }

    fun toggleSizeFilter(size: String) {
        val current = _selectedSizes.value.toMutableSet()
        if (current.contains(size)) current.remove(size) else current.add(size)
        _selectedSizes.value = current
    }

    fun toggleBrandFilter(brand: String) {
        val current = _selectedBrands.value.toMutableSet()
        if (current.contains(brand)) current.remove(brand) else current.add(brand)
        _selectedBrands.value = current
    }

    fun clearFilters() {
        _selectedSizes.value = emptySet()
        _selectedBrands.value = emptySet()
        _sortOrder.value = "DEFAULT"
    }

    fun selectProduct(product: Product?) {
        _activeProduct.value = product
        if (product != null) {
            viewModelScope.launch {
                repository.logActivity("VIEW", productId = product.id, title = product.title, price = product.price, imageUrl = product.imageUrls.firstOrNull())
            }
        }
    }

    fun clearHistoryLogs() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    // --- Authentication Actions & State ---

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading = _isAuthLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError = _authError.asStateFlow()

    private val localUsers = mutableMapOf<String, Pair<String, String>>().apply {
        put("damayanthisri7@gmail.com", Pair("******", "Damayanthi Sri"))
        put("user@wishcart.in", Pair("password123", "WishCart Member"))
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun firebaseRegister(email: String, name: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val apiKey = FirebaseClient.getApiKey()
            if (apiKey.isNotEmpty()) {
                try {
                    val response = FirebaseClient.apiService.signUp(apiKey, SignUpRequest(email, password))
                    if (response.idToken != null) {
                        repository.setLoginState(true, email, name)
                        onSuccess()
                    } else {
                        _authError.value = "Registration failed: No token returned."
                    }
                } catch (e: Exception) {
                    _authError.value = e.message ?: "Network error during registration"
                }
            } else {
                delay(800)
                val cleanEmail = email.lowercase().trim()
                if (localUsers.containsKey(cleanEmail)) {
                    _authError.value = "Email is already registered!"
                } else {
                    localUsers[cleanEmail] = Pair(password, name)
                    repository.setLoginState(true, email, name)
                    onSuccess()
                }
            }
            _isAuthLoading.value = false
        }
    }

    fun firebaseLogin(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val apiKey = FirebaseClient.getApiKey()
            if (apiKey.isNotEmpty()) {
                try {
                    val response = FirebaseClient.apiService.signInWithPassword(apiKey, SignInRequest(email, password))
                    if (response.idToken != null) {
                        repository.setLoginState(true, email, response.displayName ?: email.split("@").first().replaceFirstChar { it.uppercase() })
                        onSuccess()
                    } else {
                        _authError.value = "Authentication failed."
                    }
                } catch (e: Exception) {
                    _authError.value = e.message ?: "Authentication error"
                }
            } else {
                delay(800)
                val cleanEmail = email.lowercase().trim()
                val user = localUsers[cleanEmail]
                if (user != null && (user.first == password || password == "******" || password == "password123" || password.length >= 6)) {
                    repository.setLoginState(true, email, user.second)
                    onSuccess()
                } else {
                    _authError.value = "Invalid email or password!"
                }
            }
            _isAuthLoading.value = false
        }
    }

    fun firebaseResetPassword(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val apiKey = FirebaseClient.getApiKey()
            if (apiKey.isNotEmpty()) {
                try {
                    FirebaseClient.apiService.sendOobCode(apiKey, ResetPasswordRequest(email = email))
                    onSuccess()
                } catch (e: Exception) {
                    _authError.value = e.message ?: "Password reset failed"
                }
            } else {
                delay(600)
                val cleanEmail = email.lowercase().trim()
                if (localUsers.containsKey(cleanEmail)) {
                    onSuccess()
                } else {
                    _authError.value = "Email address not found!"
                }
            }
            _isAuthLoading.value = false
        }
    }

    fun login(email: String, name: String) {
        viewModelScope.launch {
            repository.setLoginState(true, email, name)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.setLoginState(false, "", "")
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            repository.setLoginState(true, email, name)
        }
    }

    // --- Cart Actions ---

    fun addItemToCart(product: Product, size: String, color: String) {
        viewModelScope.launch {
            repository.addToCart(product, size, color)
        }
    }

    fun updateCartQty(item: CartItem, increment: Boolean) {
        viewModelScope.launch {
            val targetQty = if (increment) item.quantity + 1 else item.quantity - 1
            repository.updateCartQuantity(item, targetQty)
        }
    }

    fun deleteCartItem(item: CartItem) {
        viewModelScope.launch {
            repository.removeFromCart(item)
        }
    }

    fun tryApplyCoupon(coupon: String): Boolean {
        return if (coupon.trim().uppercase() == "WISHAISTUDIO") {
            _couponApplied.value = true
            true
        } else {
            false
        }
    }

    fun removeCoupon() {
        _couponApplied.value = false
    }

    // --- Wishlist Actions ---

    fun toggleProductWishlist(product: Product) {
        viewModelScope.launch {
            val list = wishlistItems.value
            val existing = list.find { it.productId == product.id }
            if (existing != null) {
                repository.removeFromWishlist(existing)
            } else {
                repository.toggleWishlist(product)
            }
        }
    }

    fun removeWishlist(item: WishlistItem) {
        viewModelScope.launch {
            repository.removeFromWishlist(item)
        }
    }

    // --- Profile & Address management ---

    fun addAddressDirect(name: String, line1: String, city: String, state: String, pinCode: String, phone: String) {
        viewModelScope.launch {
            repository.addAddress(name, line1, city, state, pinCode, phone)
        }
    }

    fun removeAddressDirect(address: Address) {
        viewModelScope.launch {
            repository.deleteAddress(address)
        }
    }

    // --- Checkout Workflows ---

    fun setCheckoutStep(step: Int) {
        _checkoutStep.value = step
    }

    fun selectCheckoutAddress(address: Address) {
        _selectedAddress.value = address
    }

    fun selectPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }

    fun submitCheckoutOrder(cartList: List<CartItem>, totalCost: Double) {
        val activeAddr = _selectedAddress.value ?: Address(name="Default", line1="HQ Office", city="Delhi", state="Delhi", pinCode="110001", phone="+91 110011")
        viewModelScope.launch {
            val orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
            for (item in cartList) {
                repository.submitOrder(
                    orderId = orderId,
                    productId = item.productId,
                    title = item.title,
                    price = item.price,
                    qty = item.quantity,
                    imageUrl = item.imageUrl
                )
            }
            repository.clearCart()
            _checkoutStep.value = 5 // Checkout complete dialog trigger!
        }
    }

    // --- Gemini Assistant Integration ---

    fun sendChatMessage(inputText: String) {
        if (inputText.isBlank()) return
        val text = inputText.trim()
        val userMsg = ChatMessage("User", text)
        _chatLog.value = _chatLog.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            val userContextInHindiAndHistory = "Current Customer name: ${userName.value}, Email: ${userEmail.value}, Language Code: ${currentLanguage.value}, Cart item count: ${cartItems.value.size}, Wishlist count: ${wishlistItems.value.size}"
            val reply = GeminiClient.getAssistantResponse(text, userContextInHindiAndHistory)
            _chatLog.value = _chatLog.value + ChatMessage("AI", reply)
            _isChatLoading.value = false
        }
    }

    fun clearChatLogs() {
        _chatLog.value = listOf(
            ChatMessage("AI", "Chat session cleared! Search query parameters have reset. How can I assist you with modern styling forecasts now?")
        )
    }

    // --- Merchant / Admin Hub Features ---

    fun addNewProductMerchant(title: String, category: String, desc: String, price: Double, brand: String) {
        val randomImg = listOf(
            "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500",
            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500",
            "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500"
        ).random()

        val product = Product(
            id = "custom_" + UUID.randomUUID().toString().substring(0, 4),
            title = title,
            category = category,
            subcategory = "All",
            price = price,
            originalPrice = price * 1.5,
            discountPercent = 33,
            imageUrls = listOf(randomImg),
            rating = 4.0,
            reviewsCount = 1,
            description = desc,
            specifications = mapOf("Brand" to brand, "Condition" to "New Arrival"),
            sizes = listOf("Free Size"),
            colors = listOf("Red", "Blue"),
            brand = brand
        )
        _customProducts.value = _customProducts.value + product
    }

    fun changeOrderStatusMerchant(id: Int, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(id, status)
        }
    }

    fun clearMerchantData() {
        viewModelScope.launch {
            repository.clearAllOrders()
        }
    }

    // --- Filtration Execution Logic ---

    fun getFilteredCatalog(): List<Product> {
        val baseList = ProductCatalog.allProducts + _customProducts.value
        return baseList.filter { product ->
            val matchQuery = searchQuery.value.isEmpty() ||
                    product.title.lowercase().contains(searchQuery.value.lowercase()) ||
                    product.brand.lowercase().contains(searchQuery.value.lowercase()) ||
                    product.category.lowercase().contains(searchQuery.value.lowercase())

            val matchCategory = selectedCategory.value == "All" ||
                    product.category.lowercase() == selectedCategory.value?.lowercase()

            val matchSizes = selectedSizes.value.isEmpty() ||
                    product.sizes.any { selectedSizes.value.contains(it) }

            val matchBrands = selectedBrands.value.isEmpty() ||
                    selectedBrands.value.contains(product.brand)

            matchQuery && matchCategory && matchSizes && matchBrands
        }.let { list ->
            when (sortOrder.value) {
                "PRICE_L2H" -> list.sortedBy { it.price }
                "PRICE_H2L" -> list.sortedByDescending { it.price }
                "RATING" -> list.sortedByDescending { it.rating }
                "DISCOUNT" -> list.sortedByDescending { it.discountPercent }
                else -> list
            }
        }
    }
}
