package com.pomodorotime.task.tasklist

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.core.Event
import com.pomodorotime.core.IdlingResourcesSync
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.task.TaskEntity
import com.pomodorotime.data.task.TaskRepository
import com.pomodorotime.task.tasklist.list.TaskListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@ExperimentalCoroutinesApi
class TaskViewModel(
    private val taskRepository: TaskRepository,
    idlingResourceWrapper: IdlingResourcesSync? = null
) : BaseViewModel<TaskListEvent, TaskListScreenState>(idlingResourceWrapper) {

    private val _taskList: MutableLiveData<List<TaskListItem>> = MutableLiveData(emptyList())

    private val _navigationToCreateTask: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val navigationToCreateTask: LiveData<Event<Boolean>>
        get() = _navigationToCreateTask

    override fun initialState(): TaskListScreenState = TaskListScreenState.Initial

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setList(task: List<TaskListItem>) {
        _taskList.value = task
    }

    override fun postEvent(event: TaskListEvent) {
        when (event) {
            is TaskListEvent.Load -> loadTaskList()
            is TaskListEvent.EditTaskList -> _screenState.value = Event(TaskListScreenState.Editing)
            is TaskListEvent.AddTaskPressed -> {
                _navigationToCreateTask.value = Event(true)
            }
            is TaskListEvent.DeleteTaskElementsPressed -> deleteElements(event.list)
            is TaskListEvent.EditTaskListFinished -> {
                _screenState.value = Event(TaskListScreenState.DataLoaded(_taskList.value!!))
            }
        }
    }

    private fun loadTaskList() {
        subscribeFlow(
            taskRepository.getAllTasks()
                .onStart {
                    _screenState.value = Event(TaskListScreenState.Loading)
                }.onEach { result ->
                    when (result) {
                        is ResultWrapper.Success -> {
                            manageResult(result.value)
                        }

                        is ResultWrapper.GenericError -> {
                            _screenState.value =
                                Event(TaskListScreenState.Error(result.error.message))
                        }
                    }

                }
        )
    }

    private fun manageResult(task: List<TaskEntity>) {
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

            val result = taskRepository.deleteTasks(tasks.map { it.id })
            val list = _taskList.value!!

            when (result) {
                is ResultWrapper.Success -> {
                    list.filterNot { tasks.contains(it) }.also {
                        _taskList.value = it
                        _screenState.value = if (it.isEmpty()) {
                            Event(TaskListScreenState.EmptyState)
                        } else {
                            Event(TaskListScreenState.DataLoaded(it))
                        }
                    }
                }
                is ResultWrapper.NetworkError -> {
                    onNetworkError()
                    _screenState.value = Event(TaskListScreenState.DataLoaded(list))
                }
                is ResultWrapper.GenericError -> {
                    _screenState.value = Event(TaskListScreenState.Error(result.error.message))
                    _screenState.value = Event(TaskListScreenState.DataLoaded(list))
                }
            }
        }
    }
}