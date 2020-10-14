package com.pomodorotime.task.create

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.core.Event
import com.pomodorotime.core.IdlingResourcesSync
import com.pomodorotime.core.SnackBarrError
import com.pomodorotime.core.getCurrentDate
import com.pomodorotime.domain.models.ErrorEntity
import com.pomodorotime.domain.models.ResultWrapper
import com.pomodorotime.domain.task.usecases.CreateTaskUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CreateTaskViewModel(
    private val createTaskUseCase: CreateTaskUseCase,
    idlingResourceWrapper: IdlingResourcesSync? = null
) : BaseViewModel<CreateTaskEvent, CreateTaskScreenState>(idlingResourceWrapper) {

    private val _createTaskError: MutableLiveData<Event<SnackBarrError>> = MutableLiveData()
    val createTaskError: LiveData<Event<SnackBarrError>>
        get() = _createTaskError
    private var taskName: String = ""
    private var taskPomodoros: Int = 1

    override fun initialState(): CreateTaskScreenState = CreateTaskScreenState.Initial()

    override fun postEvent(event: CreateTaskEvent) {
        super.postEvent(event)
        when (event) {
            is CreateTaskEvent.EditingTask -> {
                setTaskName(event.name)
                setPomodoroCounter(event.estimatedPomodoros)
            }
            is CreateTaskEvent.SaveTask -> {
                save()
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setTaskName(name: String) {
        taskName = name
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setPomodoroCounter(pomodoros: Int) {
        taskPomodoros = pomodoros
    }

    private fun save() {

        if (taskName.isBlank()) {
            _screenState.value = Event(CreateTaskScreenState.InvalidName)
            return
        }

        executeCoroutine {
            _screenState.value = Event(CreateTaskScreenState.Loading)

            val result = createTaskUseCase.invoke(
                CreateTaskUseCase.CreateTaskParams(
                    name = taskName,
                    estimatedPomodoros = taskPomodoros,
                    donePomodoros = 0,
                    creationDate = getCurrentDate(),
                    completed = false
                )
            )

            when (result) {
                is ResultWrapper.Success<Long> -> {
                    _screenState.value = Event(CreateTaskScreenState.Success)
                }
                is ResultWrapper.Error -> {
                    when (val error = result.error) {
                        is ErrorEntity.NetworkError -> {
                            onNetworkError()
                        }
                        is ErrorEntity.GenericError -> {
                            _createTaskError.value = Event(SnackBarrError(true, error.message))
                        }

                    }
                    _screenState.value =
                        Event(CreateTaskScreenState.Initial(taskName, taskPomodoros))
                }
            }
        }
    }

}