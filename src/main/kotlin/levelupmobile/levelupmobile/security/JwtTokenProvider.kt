package levelupmobile.levelupmobile.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider {

    @Value("\${app.jwt-secret}")
    private lateinit var jwtSecret: String

    @Value("\${app.jwt-expiration-milliseconds}")
    private var jwtExpirationDate: Long = 0

    private fun getKey(): SecretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray(StandardCharsets.UTF_8))

    fun generateToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationDate)

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getKey(), SignatureAlgorithm.HS512)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun getUsername(token: String): String {
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).body.subject
    }
}