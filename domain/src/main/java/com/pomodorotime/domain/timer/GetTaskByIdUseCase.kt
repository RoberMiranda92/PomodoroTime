package com.pomodorotime.domain.timer

import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.SuspendableUseCase
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.task.ITaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetTaskByIdUseCase(
    private val repository: ITaskRepository,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    errorHandler: IErrorHandler
) : SuspendableUseCase<GetTaskByIdUseCase.GetTaskByIdParams, Task>(
    coroutineDispatcher,
    errorHandler
) {

    override suspend fun execute(parameters: GetTaskByIdParams): Task =
        repository.getTaskById(parameters.taskId)

    data class GetTaskByIdParams(val taskId: Long)

}