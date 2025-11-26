package levelupmobile.levelupmobile.model

import jakarta.persistence.*

@Entity
@Table(name = "cart_items")
data class CartItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val productCode: String,
    val productName: String,
    val productPrice: Int,
    val quantity: Int,
    val userId: Long
)