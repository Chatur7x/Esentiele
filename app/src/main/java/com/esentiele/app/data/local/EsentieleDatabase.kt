package com.esentiele.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ClothingItemEntity::class, OutfitEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class EsentieleDatabase : RoomDatabase() {

    abstract fun clothingDao(): ClothingDao
    abstract fun outfitDao(): OutfitDao

    companion object {
        @Volatile
        private var INSTANCE: EsentieleDatabase? = null

        fun getInstance(context: Context): EsentieleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EsentieleDatabase::class.java,
                    "esentiele_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
