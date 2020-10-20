package com.pomodorotime.task.tasklist

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.core.Event
import com.pomodorotime.core.IdlingResourcesSync
import com.pomodorotime.core.SnackBarrError
import com.pomodorotime.domain.models.ErrorEntity
import com.pomodorotime.domain.models.ResultWrapper
import com.pomodorotime.domain.models.Task
import com.pomodorotime.domain.task.usecases.DeleteTaskUseCase
import com.pomodorotime.domain.task.usecases.GetAllTaskUseCase
import com.pomodorotime.task.tasklist.list.TaskListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@ExperimentalCoroutinesApi
class TaskViewModel(
    private val getAllTaskUseCase: GetAllTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    idlingResourceWrapper: IdlingResourcesSync? = null
) : BaseViewModel<TaskListEvent, TaskListScreenState>(idlingResourceWrapper) {

    private var _taskList: List<TaskListItem> = emptyList()

    private val _taskListError: MutableLiveData<Event<SnackBarrError>> = MutableLiveData()
    val taskListError: LiveData<Event<SnackBarrError>>
        get() = _taskListError

    private val _navigationToCreateTask: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val navigationToCreateTask: LiveData<Event<Boolean>>
        get() = _navigationToCreateTask

    override fun initialState(): TaskListScreenState = TaskListScreenState.Initial

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setList(task: List<TaskListItem>) {
        _taskList = task
    }

    override fun postEvent(event: TaskListEvent) {
        super.postEvent(event)
        when (event) {
            is TaskListEvent.Load -> loadTaskList()
            is TaskListEvent.EditTaskList -> _screenState.value = Event(TaskListScreenState.Editing)
            is TaskListEvent.AddTaskPressed -> {
                _navigationToCreateTask.value = Event(true)
            }
            is TaskListEvent.DeleteTaskElementsPressed -> deleteElements(event.list)
            is TaskListEvent.EditTaskListFinished -> {
                _screenState.value = Event(TaskListScreenState.DataLoaded(_taskList))
            }
        }
    }

    private fun loadTaskList() {
        subscribeFlow(
            getAllTaskUseCase.invoke(Unit)
                .onStart {
                    _screenState.value = Event(TaskListScreenState.Loading)
                }.onEach { result ->
                    when (result) {
                        is ResultWrapper.Success<List<Task>> -> {
                            manageResult(result.value)
                        }
                        is ResultWrapper.Error -> {
                            when (val error = result.error) {
                                is ErrorEntity.NetworkError -> onNetworkError()
                                is ErrorEntity.GenericError -> _taskListError.value =
                                    Event(SnackBarrError(true, error.message))
                            }
                            _screenState.value = Event(TaskListScreenState.EmptyState)
                        }

                    }
                }
        )
    }

    private fun manageResult(task: List<Task>) {
        _screenState.value = if (task.isEmpty()) {
            Event(TaskListScreenState.EmptyState)
        } else {
            fromModelToView(task)
                .let {
                    setList(it)
                    Event(TaskListScreenState.DataLoaded(it))
                }
        }
    }

    private fun deleteElements(tasks: List<TaskListItem>) {
        executeCoroutine {
            _screenState.value = Event(TaskListScreenState.Loading)

            val result =
                deleteTaskUseCase.invoke(DeleteTaskUseCase.DeleteTaskUseCaseParams(tasks.map { it.id }))
            when (result) {
                is ResultWrapper.Success<Unit> -> {
                    //Not necessary due to flow :D, list is update automatically
                    _taskList.filterNot { tasks.contains(it) }.also {
                        _taskList = it
                        _screenState.value = if (it.isEmpty()) {
                            Event(TaskListScreenState.EmptyState)
                        } else {
                            Event(TaskListScreenState.DataLoaded(it))
                        }
                    }
                }
                is ResultWrapper.Error -> {
                    when (val error = result.error) {
                        is ErrorEntity.NetworkError -> {
                            onNetworkError()
                        }
                        is ErrorEntity.GenericError -> {
                            _taskListError.value = Event(SnackBarrError(true, error.message))
                        }
                    }
                    _screenState.value = Event(TaskListScreenState.DataLoaded(tasks))
                }
            }
        }
    }
}