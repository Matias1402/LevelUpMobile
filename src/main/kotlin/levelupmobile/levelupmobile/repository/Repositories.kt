package levelupmobile.levelupmobile.repository

import levelupmobile.levelupmobile.model.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
}

@Repository
interface ProductRepository : JpaRepository<Product, String>

@Repository
interface CartItemRepository : JpaRepository<CartItem, Long> {
    fun findByUserId(userId: Long): List<CartItem>
    fun deleteByUserId(userId: Long)
}

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByProductCode(productCode: String): List<Review>
}