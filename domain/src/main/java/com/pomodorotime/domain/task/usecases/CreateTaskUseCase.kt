package com.pomodorotime.domain.task.usecases

import com.pomodorotime.domain.IErrorHandler
import com.pomodorotime.domain.SuspendableUseCase
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.task.ITaskRepository
import java.util.Date
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CreateTaskUseCase(
    private val repository: ITaskRepository,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    errorHandler: IErrorHandler
) : SuspendableUseCase<CreateTaskUseCase.CreateTaskParams, Long>(coroutineDispatcher, errorHandler) {

    override suspend fun execute(parameters: CreateTaskParams): Long {
        val task = Task(
            name = parameters.name,
            estimatedPomodoros = parameters.estimatedPomodoros,
            donePomodoros = parameters.donePomodoros,
            shortBreaks = calculateShortBreaks(parameters.estimatedPomodoros),
            longBreaks = calculateLongBreaks(parameters.estimatedPomodoros),
            creationDate = parameters.creationDate,
            completed = false
        )
        return repository.insetTask(task)
    }

    private fun calculateShortBreaks(estimatedPomodoros: Int) = estimatedPomodoros * 4

    private fun calculateLongBreaks(estimatedPomodoros: Int) = estimatedPomodoros / 4

    data class CreateTaskParams(
        val name: String,
        val creationDate: Date,
        val donePomodoros: Int,
        val estimatedPomodoros: Int,
        val completed: Boolean
    )
}