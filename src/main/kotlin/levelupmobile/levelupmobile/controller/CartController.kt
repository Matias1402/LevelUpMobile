package levelupmobile.levelupmobile.controller

import levelupmobile.levelupmobile.model.CartItem
import levelupmobile.levelupmobile.repository.CartItemRepository
import org.springframework.web.bind.annotation.*
import jakarta.transaction.Transactional

@RestController
@RequestMapping("/api/cart")
class CartController(private val repo: CartItemRepository) {

    @GetMapping("/{userId}")
    fun getCart(@PathVariable userId: Long): List<CartItem> = repo.findByUserId(userId)

    @PostMapping
    fun addToCart(@RequestBody item: CartItem): CartItem = repo.save(item)

    @DeleteMapping("/clear/{userId}")
    @Transactional
    fun clearCart(@PathVariable userId: Long) = repo.deleteByUserId(userId)
}