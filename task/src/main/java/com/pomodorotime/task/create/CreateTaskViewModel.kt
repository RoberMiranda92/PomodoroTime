package com.pomodorotime.task.create

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.core.Event
import com.pomodorotime.core.getCurrentDate
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.task.TaskEntity
import com.pomodorotime.data.task.TaskRepository

class CreateTaskViewModel(private val repository: TaskRepository) :
    BaseViewModel<CreateTaskEvent, CreateTaskScreenState>() {

    private val taskNameLiveData: MutableLiveData<String> = MutableLiveData()
    private val taskPomodorosLiveData: MutableLiveData<Int> = MutableLiveData()

    private val _screenState: MutableLiveData<Event<CreateTaskScreenState>> =
        MutableLiveData(Event(CreateTaskScreenState.Initial()))
    val screenState: LiveData<Event<CreateTaskScreenState>>
        get() = _screenState

    override fun postEvent(event: CreateTaskEvent) {
        when (event) {
            is CreateTaskEvent.EdittingTask -> {
                setTaskName(event.name)
                setPomodoroCounter(event.estimatedPomodoros)
            }
            CreateTaskEvent.SaveTask -> {
                save()
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setTaskName(name: String) {
        taskNameLiveData.value = name
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setPomodoroCounter(pomodoros: Int) {
        taskPomodorosLiveData.value = pomodoros
    }

    private fun save() {
        val taskName = taskNameLiveData.value ?: ""

        if (taskName.isBlank()) {
            _screenState.value = Event(CreateTaskScreenState.InvalidName)
            return
        }

        executeCoroutine {
            _screenState.value = Event(CreateTaskScreenState.Loading)
            val result = repository.insetTask(
                TaskEntity(
                    name = taskName,
                    estimatedPomodoros = taskPomodorosLiveData.value ?: 1,
                    creationDate = getCurrentDate(),
                    completed = false
                )
            )

            when (result) {
                is ResultWrapper.Success -> {
                    _screenState.value = Event(CreateTaskScreenState.Success)
                }
                is ResultWrapper.NetworkError -> {
                    onNetworkError()
                    _screenState.value =
                        Event(
                            CreateTaskScreenState.Initial(
                                taskNameLiveData.value ?: "",
                                taskPomodorosLiveData.value ?: 0
                            )
                        )
                }
                is ResultWrapper.GenericError -> {
                    _screenState.value = Event(CreateTaskScreenState.Error(result.error.message))
                    _screenState.value = Event(
                        CreateTaskScreenState.Initial(
                            taskNameLiveData.value ?: "",
                            taskPomodorosLiveData.value ?: 0
                        )
                    )
                }
            }
        }
    }
}