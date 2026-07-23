package com.esentiele.app

import android.app.Application

class EsentieleApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize the Room database singleton here
        // AppDatabase.getDatabase(this)
    }
}
