package com.nutritrack.data.dao

import androidx.room.*
import com.nutritrack.data.model.FoodIntake
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodIntakeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodIntake(foodIntake: FoodIntake)

    @Query("SELECT * FROM food_intake WHERE patientId = :patientId ORDER BY submissionDate DESC")
    fun getFoodIntakeByPatientId(patientId: String): Flow<List<FoodIntake>>

    @Query("SELECT * FROM food_intake WHERE patientId = :patientId ORDER BY submissionDate DESC LIMIT 1")
    suspend fun getLatestFoodIntakeByPatientId(patientId: String): FoodIntake?
}