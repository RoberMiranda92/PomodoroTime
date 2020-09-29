package com.pomodorotime.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.core.Event
import com.pomodorotime.core.getTimer
import com.pomodorotime.data.POMODORO_DEFAULT_TIME
import com.pomodorotime.data.POMODORO_LONG_BREAK_DEFAULT_TIME
import com.pomodorotime.data.POMODORO_SMALL_BREAK_DEFAULT_TIME
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.task.TaskEntity
import com.pomodorotime.data.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class TimerViewModel(private val repository: TaskRepository) :
    BaseViewModel<TimerEvents, TimerScreenState>() {

    private val _detail: MutableLiveData<TimeDetail> = MutableLiveData<TimeDetail>(null)
    private val _counter: MutableLiveData<Long> = MutableLiveData(25 * 1000)
    private val _status: MutableLiveData<@TimerStatus Int> =
        MutableLiveData<@TimerStatus Int>(TimerStatus.PAUSE)
    private val _mode: MutableLiveData<@PomodoroMode Int> =
        MutableLiveData<@TimerStatus Int>(PomodoroMode.POMODORO)

    private val _screenState: MutableLiveData<Event<TimerScreenState>> =
        MutableLiveData()
    val screenState: LiveData<Event<TimerScreenState>>
        get() = _screenState

    private var intervalJob: Job? = null

    override fun postEvent(event: TimerEvents) {

        when (event) {
            is TimerEvents.LoadData -> {
                loadData(event.id)
            }
            is TimerEvents.OnPlayStopButtonClicked -> {
                when (_status.value) {
                    TimerStatus.PAUSE -> {
                        playCounter()
                    }
                    TimerStatus.PLAY -> {
                        stopCounter()
                    }
                }
            }
        }
    }

    private fun loadData(id: Int) {
        executeCoroutine {
            _screenState.value = Event(TimerScreenState.Loading)
            val result = repository.getTaskById(id)
            when (result) {
                is ResultWrapper.Success -> {
                    _detail.value = getTaskDetail(result.value).apply {
                        _screenState.value = Event(
                            TimerScreenState.DataLoaded(
                                this,
                                _counter.value!!,
                                _status.value!!,
                                _mode.value!!
                            )
                        )
                    }
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
        return TimeDetail(task.name, task.donePomodoros, task.estimatedPomodoros)
    }

    private fun playCounter() {
        intervalJob = getTimer(_counter.value!!, 1000)
            .map {
                _detail.value?.let { detail ->
                    Event(TimerScreenState.DataLoaded(detail, it, _status.value!!, _mode.value!!))
                }
            }
            .onStart {
                _status.value = TimerStatus.PLAY
                getDataLoadedEvent()
            }
            .onEach {
                _screenState.value = it
            }.onCompletion {
                onPomodoroEnded()
            }
            .flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }

    private fun stopCounter() {
        intervalJob?.cancel()
        _status.value = TimerStatus.PAUSE
        _screenState.value =
            getDataLoadedEvent()
    }

    private fun getDataLoadedEvent(): Event<TimerScreenState.DataLoaded> {
        return Event(
            TimerScreenState.DataLoaded(
                _detail.value!!,
                _counter.value!!,
                _status.value!!,
                _mode.value!!
            )
        )
    }

    private fun onPomodoroEnded() {
        var currentPomodoro = _detail.value!!.donePomodoros
        val totalPomodoros = _detail.value!!.total
        //TODO: MOVE THIS TO A USE CASE
        when (_mode.value) {
            PomodoroMode.POMODORO -> {
                val isPomodoroEnded = currentPomodoro == totalPomodoros
                val isLongBreak = currentPomodoro != 0 && currentPomodoro % 4 == 0

                when {
                    isPomodoroEnded -> {
                        stopCounter()
                    }
                    isLongBreak -> {
                        //Time for a long break
                        _mode.value = PomodoroMode.LONG_BREAK
                        _counter.value = POMODORO_LONG_BREAK_DEFAULT_TIME
                    }
                    else -> {
                        //Time for a small break
                        _mode.value = PomodoroMode.SHORT_BREAK
                        _counter.value = POMODORO_SMALL_BREAK_DEFAULT_TIME
                    }
                }

                currentPomodoro += 1
                _detail.value = _detail.value?.let {
                    TimeDetail(it.name, currentPomodoro, it.total)
                }
            }
            PomodoroMode.SHORT_BREAK,
            PomodoroMode.LONG_BREAK -> {
                _counter.value = POMODORO_DEFAULT_TIME
                _mode.value = PomodoroMode.POMODORO
            }
        }
        stopCounter()
    }

}

