package com.pomodorotime.data.task.dataBase

import android.content.Context
import androidx.room.*
import java.util.Date

@Database(entities = [TaskEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class TaskDataBase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        @Volatile
        private var INSTANCE: TaskDataBase? = null

        private const val TASK_DATABASE_NAME = "task.db"
        fun getInstance(context: Context): TaskDataBase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TaskDataBase::class.java, TASK_DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}