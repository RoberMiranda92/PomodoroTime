package com.pomodorotime.domain.task

import com.pomodorotime.data.task.ITaskRepository
import com.pomodorotime.domain.SuspendableUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DeleteTaskUseCase(
    private val repository: ITaskRepository,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SuspendableUseCase<DeleteTaskUseCase.DeleteTaskUseCaseParams, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: DeleteTaskUseCaseParams) =
        repository.deleteTasks(parameters.idList)

    data class DeleteTaskUseCaseParams(val idList: List<Long>)
}