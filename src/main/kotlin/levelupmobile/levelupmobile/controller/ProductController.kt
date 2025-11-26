package levelupmobile.levelupmobile.controller

import levelupmobile.levelupmobile.model.Product
import levelupmobile.levelupmobile.repository.ProductRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val repo: ProductRepository) {

    @GetMapping
    fun getAll(): List<Product> = repo.findAll()

    @GetMapping("/{code}")
    fun getOne(@PathVariable code: String): Product = repo.findById(code).orElseThrow()

    @PostMapping
    fun create(@RequestBody product: Product): Product = repo.save(product)
}