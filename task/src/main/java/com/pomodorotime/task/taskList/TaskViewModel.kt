package com.pomodorotime.task.tasklist

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.core.Event
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.task.TaskEntity
import com.pomodorotime.data.task.TaskRepository
import com.pomodorotime.task.tasklist.list.TaskListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart

@ExperimentalCoroutinesApi
class TaskViewModel(private val taskRepository: TaskRepository) :
    BaseViewModel<TaskListEvent, TaskListScreenState>() {

    private val _screenState: MutableLiveData<Event<TaskListScreenState>> =
        MutableLiveData()
    val screenState: LiveData<Event<TaskListScreenState>>
        get() = _screenState
    private val _taskList: MutableLiveData<List<TaskListItem>> = MutableLiveData(emptyList())

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setList(task: List<TaskListItem>) {
        _taskList.value = task
    }

    override fun postEvent(event: TaskListEvent) {
        when (event) {
            TaskListEvent.Load -> loadTaskList()
            TaskListEvent.EditTaskList -> _screenState.value = Event(TaskListScreenState.Editing)
            TaskListEvent.AddTaskPressed -> _screenState.value =
                Event(TaskListScreenState.NavigateToCreateTask)
            is TaskListEvent.DeleteTaskElementsPressed -> deleteElements(event.list)
            TaskListEvent.EditTaskListFinished -> {
                _screenState.value = Event(TaskListScreenState.DataLoaded(_taskList.value!!))
            }
        }
    }

    private fun loadTaskList() {
        executeCoroutine {
            taskRepository.getAllTasks()
                .onStart {
                    _screenState.value = Event(TaskListScreenState.Loading)
                }.collect { result ->
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
        }
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
                    list?.filterNot { tasks.contains(it) }?.also {
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
                    Event(TaskListScreenState.DataLoaded(list))
                }
                is ResultWrapper.GenericError -> {
                    _screenState.value = Event(TaskListScreenState.Error(result.error.message))
                    Event(TaskListScreenState.DataLoaded(list))
                }
            }
        }
    }
}