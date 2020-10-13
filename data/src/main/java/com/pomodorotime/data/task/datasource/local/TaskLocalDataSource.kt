package com.pomodorotime.data.task.datasource.local

import android.content.Context
import com.pomodorotime.data.task.dataBase.IDataBase
import com.pomodorotime.data.task.dataBase.TaskDataBase
import com.pomodorotime.data.task.dataBase.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskLocalDataSource(private val taskDataBase: IDataBase) {

    fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDataBase.getAllTask()
    }

    suspend fun insetTask(entity: TaskEntity): Long {
        return taskDataBase.insert(entity)
    }

    suspend fun getTaskById(id: Long): TaskEntity {
        return taskDataBase.getTaskById(id)
    }

    suspend fun deleteTasks(idList: List<Long>) = taskDataBase.deleteTaskList(idList)

    companion object {

        fun getNewInstance(context: Context): TaskLocalDataSource {
            return TaskLocalDataSource(TaskDataBase.getInstance(context).taskDao())
        }
    }
}