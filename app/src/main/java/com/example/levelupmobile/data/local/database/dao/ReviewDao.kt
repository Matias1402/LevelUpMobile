package cl.duoc.levelupmobile.data.local.database.dao

import androidx.room.*
import cl.duoc.levelupmobile.data.local.entities.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE productCode = :productCode ORDER BY createdAt DESC")
    fun getProductReviews(productCode: String): Flow<List<Review>>

    @Insert
    suspend fun insertReview(review: Review)

    @Query("SELECT AVG(rating) FROM reviews WHERE productCode = :productCode")
    suspend fun getAverageRating(productCode: String): Float?

    @Query("SELECT COUNT(*) FROM reviews WHERE productCode = :productCode")
    suspend fun getReviewCount(productCode: String): Int
}