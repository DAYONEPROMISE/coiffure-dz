package dz.coiffuredz.app

import android.app.Application
import androidx.room.Room

class CoiffureDzApp : Application() {
    lateinit var db: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, AppDatabase::class.java, "coiffuredz.db")
            .fallbackToDestructiveMigration()
            .build()
        SeedDefaults.seed(this)
    }
}
