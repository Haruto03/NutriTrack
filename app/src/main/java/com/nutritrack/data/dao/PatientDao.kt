package com.nutritrack.data.dao

import androidx.room.*
import com.nutritrack.data.model.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllPatients(patients: List<Patient>)

    @Query("SELECT * FROM patients WHERE userId = :userId LIMIT 1")
    suspend fun getPatientById(userId: String): Patient?

    @Query("SELECT * FROM patients WHERE userId = :userId AND phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getPatientByIdAndPhone(userId: String, phoneNumber: String): Patient?

    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int

    @Query("UPDATE patients SET name = :name, password = :password WHERE userId = :userId AND phoneNumber = :phoneNumber")
    suspend fun claimAccount(userId: String, phoneNumber: String, name: String, password: String): Int

    @Query("SELECT * FROM patients")
    fun getAllPatientsFlow(): Flow<List<Patient>>

    @Query("SELECT AVG(heifaTotalScoreMale) FROM patients WHERE sex = 'Male'")
    suspend fun getAverageHeifaScoreMale(): Float?

    @Query("SELECT AVG(heifaTotalScoreFemale) FROM patients WHERE sex = 'Female'")
    suspend fun getAverageHeifaScoreFemale(): Float?
}