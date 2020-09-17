package com.pomodorotime.data.task

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {E

    @Query("SELECT * FROM tasks")
    fun getAllTask(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id is :id")
    suspend fun getTaskById(id: Int): TaskEntity

    @Insert
    suspend fun insert(taskList: List<TaskEntity>)

    @Insert
    suspend fun insert(task: TaskEntity)

    @Delete
    fun delete(task: TaskEntity)

    @Query("DELETE FROM TASKS WHERE id IN (:list)")
    fun deleteTaskList(list: List<Int>)
}