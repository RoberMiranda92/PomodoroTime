package com.pomodorotime.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.core.Event
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.task.TaskEntity
import com.pomodorotime.data.task.TaskRepository

class TimerViewModel(private val repository: TaskRepository) :
    BaseViewModel<TimerEvents, TimerScreenState>() {

    private val _screenState: MutableLiveData<Event<TimerScreenState>> =
        MutableLiveData()
    val screenState: LiveData<Event<TimerScreenState>>
        get() = _screenState

    override fun postEvent(event: TimerEvents) {

        when (event) {
            is TimerEvents.LoadData -> {
                loadData(event.id)
            }
        }
    }

    private fun loadData(id: Int) {

        executeCoroutine {
            _screenState.value = Event(TimerScreenState.Loading)
            val result = repository.getTaskById(id)
            when (result) {
                is ResultWrapper.Success -> {
                    _screenState.value =
                        Event(TimerScreenState.DataLoaded(getTaskDetail(result.value)))
                }
                is ResultWrapper.NetworkError -> {
                    onNetworkError()

                }
                is ResultWrapper.GenericError ->
                    _screenState.value = Event(TimerScreenState.Error(result.error.message))

            }
        }
    }

    private fun getTaskDetail(task: TaskEntity): TimeDetail {
        return TimeDetail(task.name)
    }
}