package com.fit2081.nutritrackHaruto34307710

import android.app.Application

class NutriTrackApplication : Application() {
    val repository: NutriTrackRepository by lazy {
        NutriTrackRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
        AuthManager.init(applicationContext)
    }
}