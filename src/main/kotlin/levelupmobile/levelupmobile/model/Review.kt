package levelupmobile.levelupmobile.model

import jakarta.persistence.*

@Entity
@Table(name = "reviews")
data class Review(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val productCode: String,
    val userId: Long,
    val userName: String,
    val rating: Float,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)