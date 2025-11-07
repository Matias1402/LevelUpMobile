package cl.duoc.levelupmobile.data.local.database.dao

import androidx.room.*
import cl.duoc.levelupmobile.data.local.entities.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItems(userId: Int): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Int)

    @Query("SELECT * FROM cart_items WHERE productCode = :productCode AND userId = :userId")
    suspend fun getCartItemByProduct(productCode: String, userId: Int): CartItem?

    @Query("SELECT SUM(quantity) FROM cart_items WHERE userId = :userId")
    fun getCartItemCount(userId: Int): Flow<Int?>
}