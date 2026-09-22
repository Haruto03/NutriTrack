package com.nutritrack.data.dao

import androidx.room.*
import com.nutritrack.data.model.NutriCoachTip
import kotlinx.coroutines.flow.Flow

@Dao
interface NutriCoachTipDao {
    @Insert
    suspend fun insertTip(tip: NutriCoachTip)

    @Query("SELECT * FROM nutri_coach_tips WHERE patientId = :patientId ORDER BY generatedDate DESC")
    fun getTipsByPatientId(patientId: String): Flow<List<NutriCoachTip>>
}