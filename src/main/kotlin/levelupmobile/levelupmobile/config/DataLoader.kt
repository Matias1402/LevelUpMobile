package levelupmobile.levelupmobile.config

import levelupmobile.levelupmobile.model.Product
import levelupmobile.levelupmobile.repository.ProductRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataLoader {

    @Bean
    fun initDatabase(repository: ProductRepository) = CommandLineRunner {
        if (repository.count() == 0L) {
            val p1 = Product("GM-001", "Consolas", "PlayStation 5", 500000, "Consola Sony", "https://i.imgur.com/3fM6X9h.jpg", 20)
            val p2 = Product("GM-002", "Perifericos", "Mouse Gamer", 25000, "16000 DPI", "https://i.imgur.com/mouse.jpg", 50)
            repository.saveAll(listOf(p1, p2))
            println("--- PRODUCTOS CARGADOS AUTOMATICAMENTE ---")
        }
    }
}