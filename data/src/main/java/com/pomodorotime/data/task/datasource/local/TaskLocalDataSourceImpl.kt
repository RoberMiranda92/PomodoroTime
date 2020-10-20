package com.pomodorotime.data.task.datasource.local

import android.content.Context
import com.pomodorotime.data.task.dataBase.IDataBase
import com.pomodorotime.data.task.dataBase.TaskDataBase
import com.pomodorotime.data.task.dataBase.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskLocalDataSourceImpl(private val taskDataBase: IDataBase):ITaskLocalDataSource {

    override fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDataBase.getAllTask()
    }

    override suspend fun insetTask(entity: TaskEntity): Long {
        return taskDataBase.insert(entity)
    }

    override suspend fun getTaskById(id: Long): TaskEntity {
        return taskDataBase.getTaskById(id)
    }

    override suspend fun deleteTasks(idList: List<Long>) = taskDataBase.deleteTaskList(idList)

    companion object {

        fun getNewInstance(context: Context): TaskLocalDataSourceImpl {
            return TaskLocalDataSourceImpl(TaskDataBase.getInstance(context).taskDao())
        }
    }
}