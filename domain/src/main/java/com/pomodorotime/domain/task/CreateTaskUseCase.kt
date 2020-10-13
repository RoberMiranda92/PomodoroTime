package com.pomodorotime.domain.task

import com.pomodorotime.data.task.ITaskRepository
import com.pomodorotime.data.task.TaskDataModel
import com.pomodorotime.domain.SuspendableUseCase
import java.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CreateTaskUseCase(
    private val repository: ITaskRepository,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SuspendableUseCase<CreateTaskUseCase.CreateTaskParams, Long>(coroutineDispatcher) {

    override suspend fun execute(parameters: CreateTaskParams): Long {
        val task = TaskDataModel(
            name = parameters.name,
            estimatedPomodoros = parameters.estimatedPomodoros,
            donePomodoros = parameters.donePomodoros,
            shortBreaks = calculateShortBreaks(parameters.estimatedPomodoros),
            longBreaks = calculateLongBreaks(parameters.estimatedPomodoros),
            creationDate = Calendar.getInstance().time,
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
        val shortBreaks: Int,
        val longBreaks: Int,
        val completed: Boolean
    )
}