package levelupmobile.levelupmobile.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column

@Entity
@Table(name = "products")
data class Product(
    @Id
    val code: String,

    val category: String,
    val name: String,
    val price: Int,
    @Column(length = 1000)
    val description: String,
    val imageUrl: String = "",
    val stock: Int = 10,
    val rating: Float = 0f,
    val reviewCount: Int = 0
)