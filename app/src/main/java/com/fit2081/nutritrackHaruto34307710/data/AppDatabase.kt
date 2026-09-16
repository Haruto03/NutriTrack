package com.fit2081.nutritrackHaruto34307710.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fit2081.nutritrackHaruto34307710.data.dao.FoodIntakeDao
import com.fit2081.nutritrackHaruto34307710.data.dao.NutriCoachTipDao
import com.fit2081.nutritrackHaruto34307710.data.dao.PatientDao
import com.fit2081.nutritrackHaruto34307710.data.model.FoodIntake
import com.fit2081.nutritrackHaruto34307710.data.model.NutriCoachTip
import com.fit2081.nutritrackHaruto34307710.data.model.Patient

@Database(
    entities = [Patient::class, FoodIntake::class, NutriCoachTip::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun foodIntakeDao(): FoodIntakeDao
    abstract fun nutriCoachTipDao(): NutriCoachTipDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nutritrack_app_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}