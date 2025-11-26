package com.example.levelupmobile.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.levelupmobile.data.local.database.dao.CartDao
import com.example.levelupmobile.data.local.database.dao.ProductDao
import com.example.levelupmobile.data.local.database.dao.ReviewDao
import com.example.levelupmobile.data.local.database.dao.UserDao
import com.example.levelupmobile.data.local.entities.CartItem
import com.example.levelupmobile.data.local.entities.Product
import com.example.levelupmobile.data.local.entities.Review
import com.example.levelupmobile.data.local.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Product::class, CartItem::class, Review::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "levelup_gamer_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            val productDao = database.productDao()

            val products = listOf(
                Product(
                    code = "JM001",
                    category = "Juegos de Mesa",
                    name = "Catan",
                    price = 29990,
                    description = "Un clásico juego de estrategia donde los jugadores compiten por colonizar y expandirse en la isla de Catan. Ideal para 3-4 jugadores y perfecto para noches de juego en familia o con amigos.",
                    stock = 15,
                    rating = 4.5f,
                    reviewCount = 23
                ),
                Product(
                    code = "JM002",
                    category = "Juegos de Mesa",
                    name = "Carcassonne",
                    price = 24990,
                    description = "Un juego de colocación de fichas donde los jugadores construyen el paisaje alrededor de la fortaleza medieval de Carcassonne. Ideal para 2-5 jugadores y fácil de aprender.",
                    stock = 20,
                    rating = 4.3f,
                    reviewCount = 18
                ),
                Product(
                    code = "AC001",
                    category = "Accesorios",
                    name = "Controlador Inalámbrico Xbox Series X",
                    price = 59990,
                    description = "Ofrece una experiencia de juego cómoda con botones mapeables y una respuesta táctil mejorada. Compatible con consolas Xbox y PC.",
                    stock = 30,
                    rating = 4.7f,
                    reviewCount = 45
                ),
                Product(
                    code = "AC002",
                    category = "Accesorios",
                    name = "Auriculares Gamer HyperX Cloud II",
                    price = 79990,
                    description = "Proporcionan un sonido envolvente de calidad con un micrófono desmontable y almohadillas de espuma viscoelástica para mayor comodidad durante largas sesiones de juego.",
                    stock = 25,
                    rating = 4.8f,
                    reviewCount = 67
                ),
                Product(
                    code = "CO001",
                    category = "Consolas",
                    name = "PlayStation 5",
                    price = 549990,
                    description = "La consola de última generación de Sony, que ofrece gráficos impresionantes y tiempos de carga ultrarrápidos para una experiencia de juego inmersiva.",
                    stock = 5,
                    rating = 4.9f,
                    reviewCount = 120
                ),
                Product(
                    code = "CG001",
                    category = "Computadores Gamers",
                    name = "PC Gamer ASUS ROG Strix",
                    price = 1299990,
                    description = "Un potente equipo diseñado para los gamers más exigentes, equipado con los últimos componentes para ofrecer un rendimiento excepcional en cualquier juego.",
                    stock = 3,
                    rating = 4.9f,
                    reviewCount = 34
                ),
                Product(
                    code = "SG001",
                    category = "Sillas Gamers",
                    name = "Silla Gamer Secretlab Titan",
                    price = 349990,
                    description = "Diseñada para el máximo confort, esta silla ofrece un soporte ergonómico y personalización ajustable para sesiones de juego prolongadas.",
                    stock = 10,
                    rating = 4.6f,
                    reviewCount = 56
                ),
                Product(
                    code = "MS001",
                    category = "Mouse",
                    name = "Mouse Gamer Logitech G502 HERO",
                    price = 49990,
                    description = "Con sensor de alta precisión y botones personalizables, este mouse es ideal para gamers que buscan un control preciso y personalización.",
                    stock = 40,
                    rating = 4.7f,
                    reviewCount = 89
                ),
                Product(
                    code = "MP001",
                    category = "Mousepad",
                    name = "Mousepad Razer Goliathus Extended Chroma",
                    price = 29990,
                    description = "Ofrece un área de juego amplia con iluminación RGB personalizable, asegurando una superficie suave y uniforme para el movimiento del mouse.",
                    stock = 50,
                    rating = 4.4f,
                    reviewCount = 72
                ),
                Product(
                    code = "PP001",
                    category = "Poleras Personalizadas",
                    name = "Polera Gamer Personalizada 'Level-Up'",
                    price = 14990,
                    description = "Una camiseta cómoda y estilizada, con la posibilidad de personalizarla con tu gamer tag o diseño favorito.",
                    stock = 100,
                    rating = 4.2f,
                    reviewCount = 31
                )
            )

            productDao.insertAll(products)
        }
    }
}