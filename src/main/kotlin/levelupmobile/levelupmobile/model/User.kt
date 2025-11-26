package levelupmobile.levelupmobile.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,
    @Column(unique = true)
    val email: String,
    val password: String,
    val age: Int,

    val isDuocStudent: Boolean = false,
    val levelUpPoints: Int = 0,
    val referralCode: String = "",
    val profileImageUri: String = "",

    val createdAt: Long = System.currentTimeMillis()
)