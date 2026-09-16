package com.todo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.todo.data.model.Task

/**
 * Room database for the To-Do List application.
 * Manages the SQLite database and provides access to DAOs.
 */
@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {

    /**
     * Get the TaskDao for database operations.
     */
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        /**
         * Get or create the database instance.
         * Uses double-checked locking for thread safety.
         *
         * @param context Application context
         * @return TaskDatabase instance
         */
        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Destroy the database instance (useful for testing).
         */
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
