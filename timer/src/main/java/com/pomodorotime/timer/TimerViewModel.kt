package com.pomodorotime.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.core.Event
import com.pomodorotime.core.getTimer
import com.pomodorotime.domain.POMODORO_DEFAULT_TIME
import com.pomodorotime.domain.POMODORO_LONG_BREAK_DEFAULT_TIME
import com.pomodorotime.domain.POMODORO_SMALL_BREAK_DEFAULT_TIME
import com.pomodorotime.domain.models.ErrorEntity
import com.pomodorotime.domain.models.ResultWrapper
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.timer.GetTaskByIdUseCase
import com.pomodorotime.timer.models.PomodoroMode
import com.pomodorotime.timer.models.TimeDetail
import com.pomodorotime.timer.models.TimerEvents
import com.pomodorotime.timer.models.TimerScreenState
import com.pomodorotime.timer.models.TimerStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@ExperimentalCoroutinesApi
class TimerViewModel(
    private val getTaskByIdUseCase: GetTaskByIdUseCase
) : BaseViewModel<TimerEvents, TimerScreenState>() {

    private val _onBakPressedDialog: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val onBakPressedDialog: LiveData<Event<Boolean>>
        get() = _onBakPressedDialog

    private var _detail: TimeDetail? = null
    private var _counter: Long = POMODORO_DEFAULT_TIME
    private var _status: @TimerStatus Int = TimerStatus.PAUSE
    private var _mode: @PomodoroMode Int = PomodoroMode.POMODORO

    private var intervalJob: Job? = null

    override fun initialState(): TimerScreenState = TimerScreenState.Initial

    override fun postEvent(event: TimerEvents) {
        super.postEvent(event)
        when (event) {
            is TimerEvents.LoadData -> {
                loadData(event.id)
            }
            is TimerEvents.OnPlayStopButtonClicked -> {
                when (_status) {
                    TimerStatus.PAUSE -> {
                        playCounter()
                    }
                    TimerStatus.PLAY -> {
                        stopCounter()
                    }
                }
            }
            is TimerEvents.OnBackPressed -> {
                manageBack()
            }
        }
    }

    private fun manageBack() {
        _onBakPressedDialog.value = Event(
            when (_status) {
                TimerStatus.PLAY -> true
                else -> false
            }
        )
    }

    private fun loadData(id: Long) {
        executeCoroutine {
            _screenState.value = Event(TimerScreenState.Loading)
            val result: ResultWrapper<Task> =
                getTaskByIdUseCase.invoke(GetTaskByIdUseCase.GetTaskByIdParams(id))
            when (result) {
                is ResultWrapper.Success -> {
                    _detail = getTaskDetail(result.value).apply {
                        _screenState.value = Event(
                            TimerScreenState.DataLoaded(
                                this,
                                _counter,
                                calculateProgress(),
                                _status,
                                _mode
                            )
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    when (val error = result.error) {
                        is ErrorEntity.NetworkError -> onNetworkError()
                        is ErrorEntity.GenericError -> {
                            _screenState.value = Event(TimerScreenState.Error(error.message))
                        }
                    }
                }
            }
        }
    }

    private fun getTaskDetail(task: Task): TimeDetail {
        return TimeDetail(task.name, task.donePomodoros, task.estimatedPomodoros)
    }

    private fun playCounter() {
        intervalJob = getTimer(_counter, 1000)
            .map {
                _detail?.let { detail ->
                    _counter = it
                    Event(
                        TimerScreenState.DataLoaded(
                            detail,
                            it,
                            calculateProgress(),
                            _status,
                            _mode
                        )
                    )
                }
            }
            .onStart {
                _status = TimerStatus.PLAY
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
        _status = TimerStatus.PAUSE
        _screenState.value = getDataLoadedEvent()
        intervalJob?.cancel()
    }

    private fun getDataLoadedEvent(): Event<TimerScreenState.DataLoaded> {
        return Event(
            TimerScreenState.DataLoaded(
                _detail!!, _counter, calculateProgress(), _status, _mode
            )
        )
    }

    //TODO MOVE USE CASE OR UTILS
    private fun calculateProgress(): Float {
        val maxTime = when (_mode) {
            PomodoroMode.POMODORO -> POMODORO_DEFAULT_TIME
            PomodoroMode.LONG_BREAK -> POMODORO_LONG_BREAK_DEFAULT_TIME
            PomodoroMode.SHORT_BREAK -> POMODORO_SMALL_BREAK_DEFAULT_TIME
            else -> 0
        }

        return (_counter.times(100) / maxTime).toFloat()
    }

    private fun onPomodoroEnded() {
        var currentPomodoro = _detail!!.donePomodoros
        val totalPomodoros = _detail?.total

        val isPomodoroPaused = _status == TimerStatus.PAUSE
        if (!isPomodoroPaused) {
            //TODO: MOVE THIS TO A USE CASE
            when (_mode) {
                PomodoroMode.POMODORO -> {
                    val isPomodoroEnded = currentPomodoro == totalPomodoros
                    val isLongBreak = currentPomodoro != 0 && currentPomodoro % 4 == 0

                    when {
                        isPomodoroEnded -> {
                            stopCounter()
                        }
                        isLongBreak -> {
                            //Time for a long break
                            _mode = PomodoroMode.LONG_BREAK
                            _counter = POMODORO_LONG_BREAK_DEFAULT_TIME
                        }
                        else -> {
                            //Time for a small break
                            _mode = PomodoroMode.SHORT_BREAK
                            _counter = POMODORO_SMALL_BREAK_DEFAULT_TIME
                        }
                    }

                    currentPomodoro += 1
                    _detail = _detail?.let {
                        TimeDetail(it.name, currentPomodoro, it.total)
                    }
                }
                PomodoroMode.SHORT_BREAK,
                PomodoroMode.LONG_BREAK -> {
                    _counter = POMODORO_DEFAULT_TIME
                    _mode = PomodoroMode.POMODORO
                }
            }
            stopCounter()
        }
    }
}

