package levelupmobile.levelupmobile.controller

import levelupmobile.levelupmobile.model.User
import levelupmobile.levelupmobile.repository.UserRepository
import levelupmobile.levelupmobile.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

data class LoginReq(val email: String, val password: String)
data class RegisterReq(val name: String, val email: String, val password: String, val age: Int)
data class AuthRes(val token: String, val userId: Long, val name: String, val email: String)

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepo: UserRepository,
    private val jwtProvider: JwtTokenProvider,
    private val encoder: PasswordEncoder
) {

    @PostMapping("/register")
    fun register(@RequestBody req: RegisterReq): AuthRes {
        if (userRepo.existsByEmail(req.email))
            throw RuntimeException("Email en uso")

        val user = User(
            id = 0L,
            name = req.name,
            email = req.email,
            password = encoder.encode(req.password) ?: "",
            age = req.age,
            levelUpPoints = 100
        )
        val saved = userRepo.save(user)
        val token = jwtProvider.generateToken(saved.email)
        return AuthRes(token, saved.id!!, saved.name, saved.email)
    }

    @PostMapping("/login")
    fun login(@RequestBody req: LoginReq): AuthRes {
        val user = userRepo.findByEmail(req.email).orElseThrow {
            RuntimeException("Usuario no encontrado")
        }

        if (!encoder.matches(req.password, user.password))
            throw RuntimeException("Clave incorrecta")

        val token = jwtProvider.generateToken(user.email)
        return AuthRes(token, user.id!!, user.name, user.email)
    }
}