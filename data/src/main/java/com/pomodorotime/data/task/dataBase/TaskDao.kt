package com.pomodorotime.data.task.dataBase

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao : IDataBase {

    @Query("SELECT * FROM tasks")
    override fun getAllTask(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id is :id")
    override suspend fun getTaskById(id: Int): TaskEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(taskList: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(task: TaskEntity): Long

    @Delete
    override suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM TASKS WHERE id IN (:list)")
    override suspend fun deleteTaskList(list: List<Long>)
}