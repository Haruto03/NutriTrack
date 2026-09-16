package com.fit2081.nutritrackHaruto34307710.data.model

import androidx.room.*

@Entity(
    tableName = "nutri_coach_tips",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["userId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["patientId"])]
)
data class NutriCoachTip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: String,
    val tipText: String,
    val generatedDate: Long
)