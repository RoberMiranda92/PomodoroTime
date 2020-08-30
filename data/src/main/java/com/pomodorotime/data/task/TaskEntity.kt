package com.pomodorotime.data.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "creationDate")
    val creationDate: Date,
    @ColumnInfo(name = "estimated_pomodoros")
    val estimatedPomodoros: Int,
    @ColumnInfo(name = "is_completed")
    val completed: Boolean
)


