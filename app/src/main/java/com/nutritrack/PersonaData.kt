package com.nutritrack

import com.nutritrack.R


data class Persona(val name: String, val  descriptionId: Int, val imageRes: Int)


val personas = listOf(
    Persona("Health Devotee", R.string.persona_1_dsc, R.drawable.persona_1),
    Persona("Mindful Eater", R.string.persona_2_dsc, R.drawable.persona_2),
    Persona("Wellness Striver",R.string.persona_3_dsc, R.drawable.persona_3),
    Persona("Balance Seeker", R.string.persona_4_dsc, R.drawable.persona_4),
    Persona("Health Procrastinator", R.string.persona_5_dsc, R.drawable.persona_5),
    Persona("Food Carefree", R.string.persona_6_dsc, R.drawable.persona_6)
)
