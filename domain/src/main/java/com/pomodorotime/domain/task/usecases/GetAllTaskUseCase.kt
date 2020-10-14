package com.pomodorotime.domain.task.usecases

import com.pomodorotime.domain.FlowUseCase
import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.task.ITaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class GetAllTaskUseCase(
    private val repository: ITaskRepository,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    errorHandler: IErrorHandler
) : FlowUseCase<Unit, List<Task>>(coroutineDispatcher,errorHandler) {

    override fun execute(parameters: Unit): Flow<List<Task>> =
        repository.getAllTasks()
}