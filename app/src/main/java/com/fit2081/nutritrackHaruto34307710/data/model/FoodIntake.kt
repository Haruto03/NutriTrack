package com.fit2081.nutritrackHaruto34307710.data.model

import androidx.room.*

@Entity(
    tableName = "food_intake",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["userId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FoodIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: String,
    val selectedCategories: String,
    val persona: String,
    val biggestMealTime: String,
    val sleepTime: String,
    val wakeUpTime: String,
    val submissionDate: Long
)