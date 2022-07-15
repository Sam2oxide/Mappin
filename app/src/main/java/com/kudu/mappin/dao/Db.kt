package com.kudu.mappin.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kudu.mappin.model.Polygon
import com.kudu.mappin.util.subscribeOnBackground

@Database(entities = [Polygon::class], version = 1, exportSchema = false)
abstract class Db : RoomDatabase() {
    abstract fun polygonDao(): PolygonDao

    /* companion object {

         // For Singleton instantiation
         @Volatile private var instance: Db? = null

         fun getInstance(context: Context): Db {
             return instance ?: synchronized(this) {
                 instance ?: buildDatabase(context).also { instance = it }
             }
         }

         // Create and pre-populate the database. See this article for more details:
         // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
         private fun buildDatabase(context: Context): Db {
             return Room.databaseBuilder(context, Db::class.java, DATABASE_NAME)
                 .addCallback(
                     object : RoomDatabase.Callback() {
                         override fun onCreate(db: SupportSQLiteDatabase) {
                             super.onCreate(db)
                             val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
                                 .setInputData(workDataOf(KEY_FILENAME to PLANT_DATA_FILENAME))
                                 .build()
                             WorkManager.getInstance(context).enqueue(request)
                         }
                     }
                 )
                 .build()
         }
     }*/

    companion object {
        private var instance: Db? = null

        @Synchronized
        fun getInstance(ctx: Context): Db {
            if (instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext, Db::class.java,
                    "mappin_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()

            return instance!!

        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                populateDatabase(instance!!)
            }
        }

        private fun populateDatabase(db: Db) {
            val polygonDao = db.polygonDao()
            subscribeOnBackground {
//                polygonDao.insert(ContactsContract.CommonDataKinds.Note("title 1", "desc 1", 1))
                polygonDao.insertPolygon(Polygon("225511", "Nombre1", "Comentario1"))
                polygonDao.insertPolygon(Polygon("225512", "Nombre2", "Comentario2"))
                polygonDao.insertPolygon(Polygon("225513", "Nombre3", "Comentario3"))
            }
        }
    }


}