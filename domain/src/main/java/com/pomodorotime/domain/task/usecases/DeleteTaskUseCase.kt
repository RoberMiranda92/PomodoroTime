package com.pomodorotime.domain.task.usecases

import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.SuspendableUseCase
import com.pomodorotime.domain.task.ITaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DeleteTaskUseCase(
    private val repository: ITaskRepository,
    coroutineDispatcher: CoroutineDispatcher,
    errorHandler: IErrorHandler
) : SuspendableUseCase<DeleteTaskUseCase.DeleteTaskUseCaseParams, Unit>(coroutineDispatcher, errorHandler) {

    override suspend fun execute(parameters: DeleteTaskUseCaseParams) =
        repository.deleteTasks(parameters.idList)

    data class DeleteTaskUseCaseParams(val idList: List<Long>)
}