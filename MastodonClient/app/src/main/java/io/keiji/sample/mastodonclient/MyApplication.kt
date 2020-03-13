package io.keiji.sample.mastodonclient

import android.app.Application
import android.util.Log
import androidx.room.Room
import io.keiji.sample.mastodonclient.db.AppDatabase

class MyApplication : Application() {

    companion object {
        private val TAG = MyApplication::class.java.simpleName

        private const val DB_FILE_NAME = "room.db"

        private var appDatabase: AppDatabase? = null

        @JvmStatic
        fun getAppDatabase(application: Application): AppDatabase {
            appDatabase?.also {
                return it
            }

            return initDatabase(application).also {
                appDatabase = it
            }
        }

        @JvmStatic
        private fun initDatabase(application: Application): AppDatabase {
            return Room.databaseBuilder(
                application,
                AppDatabase::class.java,
                DB_FILE_NAME
            ).build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application is created.")

        initDatabase(this)
    }
}