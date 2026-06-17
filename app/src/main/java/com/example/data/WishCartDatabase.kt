package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- Room Database Entities ---

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val title: String,
    val price: Double,
    val discountPercent: Int,
    val imageUrl: String,
    val size: String,
    val color: String,
    var quantity: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist_items")
data class WishlistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val title: String,
    val price: Double,
    val discountPercent: Int,
    val imageUrl: String,
    val rating: Double,
    val size: String = "M",
    val color: String = "Default",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "activity_history")
data class ActivityHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "SEARCH", "VIEW", "PURCHASE"
    val productId: String? = null,
    val searchKeyword: String? = null,
    val title: String? = null,
    val price: Double? = null,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_addresses")
data class Address(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val line1: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val phone: String
)

@Entity(tableName = "ordered_items")
data class OrderedItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: String,
    val productId: String,
    val title: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val orderDate: Long = System.currentTimeMillis(),
    val status: String // "Ordered", "Packed", "Shipped", "Out For Delivery", "Delivered"
)

// --- DAO (Data Access Object) Design ---

@Dao
interface WishCartDao {

    // Cart Queries
    @Query("SELECT * FROM cart_items ORDER BY timestamp DESC")
    fun getCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItem)

    @Update
    suspend fun updateCartItem(item: CartItem)

    @Delete
    suspend fun deleteCartItem(item: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Wishlist Queries
    @Query("SELECT * FROM wishlist_items ORDER BY timestamp DESC")
    fun getWishlistItems(): Flow<List<WishlistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlist(item: WishlistItem)

    @Delete
    suspend fun deleteWishlist(item: WishlistItem)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
    fun isWishlisted(productId: String): Flow<Boolean>

    @Query("DELETE FROM wishlist_items")
    suspend fun clearWishlist()

    // Activity History Queries
    @Query("SELECT * FROM activity_history ORDER BY timestamp DESC")
    fun getActivityHistory(): Flow<List<ActivityHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityHistory)

    @Query("DELETE FROM activity_history")
    suspend fun clearActivityHistory()

    // Address Queries
    @Query("SELECT * FROM user_addresses ORDER BY id DESC")
    fun getAddresses(): Flow<List<Address>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: Address)

    @Delete
    suspend fun deleteAddress(address: Address)

    // Order Queries
    @Query("SELECT * FROM ordered_items ORDER BY orderDate DESC")
    fun getOrderedItems(): Flow<List<OrderedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderedItem)

    @Query("UPDATE ordered_items SET status = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: Int, status: String)

    @Query("DELETE FROM ordered_items")
    suspend fun clearAllOrders()
}

// --- App Database Definition ---

@Database(
    entities = [
        CartItem::class,
        WishlistItem::class,
        ActivityHistory::class,
        Address::class,
        OrderedItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WishCartDatabase : RoomDatabase() {
    abstract fun wishCartDao(): WishCartDao

    companion object {
        @Volatile
        private var INSTANCE: WishCartDatabase? = null

        fun getDatabase(context: Context): WishCartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WishCartDatabase::class.java,
                    "wishcart_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
