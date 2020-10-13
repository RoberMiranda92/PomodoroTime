package com.pomodorotime.domain.task

import com.pomodorotime.data.task.ITaskRepository
import com.pomodorotime.domain.FlowUseCase
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.models.toDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class GetAllTaskUseCase(
    private val repository: ITaskRepository,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FlowUseCase<Nothing, List<Task>>(coroutineDispatcher) {

    override fun execute(parameters: Nothing): Flow<List<Task>> =
        repository.getAllTasks().transform { it.map { it.toDomainModel() } }
}