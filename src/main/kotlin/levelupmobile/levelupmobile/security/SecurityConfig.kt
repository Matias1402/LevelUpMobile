package levelupmobile.levelupmobile.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtFilter: JwtAuthenticationFilter) {

    // --- ESTA ES LA FUNCIÓN QUE TE FALTABA ---
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
    // -----------------------------------------

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                // Rutas públicas (H2, Login, Productos)
                it.requestMatchers(AntPathRequestMatcher("/h2-console/**")).permitAll()
                it.requestMatchers("/api/auth/**").permitAll()
                it.requestMatchers("/api/products/**").permitAll()

                // Todo lo demás privado
                it.anyRequest().authenticated()
            }
            .headers { headers ->
                headers.frameOptions { frame -> frame.disable() }
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}