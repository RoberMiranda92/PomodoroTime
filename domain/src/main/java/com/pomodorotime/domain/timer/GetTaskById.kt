package com.pomodorotime.domain.timer

import com.pomodorotime.data.task.ITaskRepository
import com.pomodorotime.domain.SuspendableUseCase
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.models.toDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetTaskById(
    private val repository: ITaskRepository,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SuspendableUseCase<GetTaskById.GetTaskByIdParams, Task>(coroutineDispatcher) {

    override suspend fun execute(parameters: GetTaskByIdParams): Task =
        repository.getTaskById(parameters.taskId).toDomainModel()

    data class GetTaskByIdParams(val taskId: Long)

}