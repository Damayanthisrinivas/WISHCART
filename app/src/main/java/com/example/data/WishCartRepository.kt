package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

// Language options
data class LanguageOption(val code: String, val name: String, val nativeName: String)

object LanguageConfig {
    val languages = listOf(
        LanguageOption("en", "English", "English"),
        LanguageOption("hi", "Hindi", "हिन्दी"),
        LanguageOption("te", "Telugu", "తెలుగు"),
        LanguageOption("ta", "Tamil", "தமிழ்"),
        LanguageOption("kn", "Kannada", "ಕನ್ನಡ"),
        LanguageOption("ml", "Malayalam", "മലയാളം"),
        LanguageOption("mr", "Marathi", "मराठी"),
        LanguageOption("bn", "Bengali", "বাংলা"),
        LanguageOption("gu", "Gujarati", "ગુજરાતી")
    )
}

class WishCartRepository(private val dao: WishCartDao) {

    // --- Local Memory App State ---
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow(false) // Start of in logged out state
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    // --- Methods for App Configuration change ---
    fun changeLanguage(langCode: String) {
        _currentLanguage.value = langCode
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setLoginState(isLoggedIn: Boolean, email: String = "damayanthisri7@gmail.com", name: String = "Damayanthi Sri") {
        _isUserLoggedIn.value = isLoggedIn
        _userEmail.value = email
        _userName.value = name
    }

    // --- Data Access Flows ---
    val cartItems: Flow<List<CartItem>> = dao.getCartItems()
    val wishlistItems: Flow<List<WishlistItem>> = dao.getWishlistItems()
    val activityHistory: Flow<List<ActivityHistory>> = dao.getActivityHistory()
    val addresses: Flow<List<Address>> = dao.getAddresses()
    val orderedItems: Flow<List<OrderedItem>> = dao.getOrderedItems()

    fun isWishlisted(productId: String): Flow<Boolean> = dao.isWishlisted(productId)

    // --- Cart Actions ---
    suspend fun addToCart(product: Product, size: String, color: String, quantity: Int = 1) {
        val cartItem = CartItem(
            productId = product.id,
            title = product.title,
            price = product.price,
            discountPercent = product.discountPercent,
            imageUrl = product.imageUrls.firstOrNull() ?: "",
            size = size,
            color = color,
            quantity = quantity
        )
        dao.insertCartItem(cartItem)
        
        // Log recently viewed click transition as activity
        logActivity("VIEW", productId = product.id, title = product.title, price = product.price, imageUrl = product.imageUrls.firstOrNull())
    }

    suspend fun updateCartQuantity(item: CartItem, newQty: Int) {
        if (newQty <= 0) {
            dao.deleteCartItem(item)
        } else {
            item.quantity = newQty
            dao.updateCartItem(item)
        }
    }

    suspend fun removeFromCart(item: CartItem) {
        dao.deleteCartItem(item)
    }

    suspend fun clearCart() = dao.clearCart()

    // --- Wishlist Actions ---
    suspend fun toggleWishlist(product: Product) {
        // Find if already exists
        val wishlistedItem = WishlistItem(
            productId = product.id,
            title = product.title,
            price = product.price,
            discountPercent = product.discountPercent,
            imageUrl = product.imageUrls.firstOrNull() ?: "",
            rating = product.rating
        )
        dao.insertWishlist(wishlistedItem)
        logActivity("VIEW", productId = product.id, title = product.title, price = product.price, imageUrl = product.imageUrls.firstOrNull())
    }

    suspend fun removeFromWishlist(item: WishlistItem) {
        dao.deleteWishlist(item)
    }

    suspend fun clearWishlist() = dao.clearWishlist()

    // --- History Tracking Actions ---
    suspend fun logActivity(type: String, productId: String? = null, searchKeyword: String? = null, title: String? = null, price: Double? = null, imageUrl: String? = null) {
        dao.insertActivity(
            ActivityHistory(
                type = type,
                productId = productId,
                searchKeyword = searchKeyword,
                title = title,
                price = price,
                imageUrl = imageUrl
            )
        )
    }

    suspend fun clearHistory() = dao.clearActivityHistory()

    // --- Address Actions ---
    suspend fun addAddress(name: String, line1: String, city: String, state: String, pinCode: String, phone: String) {
        dao.insertAddress(
            Address(
                name = name,
                line1 = line1,
                city = city,
                state = state,
                pinCode = pinCode,
                phone = phone
            )
        )
    }

    suspend fun deleteAddress(address: Address) {
        dao.deleteAddress(address)
    }

    // --- Order Placement Actions ---
    suspend fun checkoutCart(address: Address, paymentMethod: String) {
        // Generate order number
        val orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
        // Collect current cart items
        // Since repo works in coroutines, we fetch them synchronously
        // For simplicity, we write ordered items:
        
        // Log transaction purchase activities
        logActivity("PURCHASE")
    }

    suspend fun submitOrder(orderId: String, productId: String, title: String, price: Double, qty: Int, imageUrl: String) {
        dao.insertOrder(
            OrderedItem(
                orderId = orderId,
                productId = productId,
                title = title,
                price = price,
                quantity = qty,
                imageUrl = imageUrl,
                status = "Ordered"
            )
        )
    }

    suspend fun updateOrderStatus(id: Int, status: String) {
        dao.updateOrderStatus(id, status)
    }

    suspend fun clearAllOrders() {
        dao.clearAllOrders()
    }
}
