package com.pomodorotime.task.taskList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pomodorotime.core.BaseViewModel
import com.pomodorotime.core.Event
import com.pomodorotime.data.ErrorResponse
import com.pomodorotime.data.ResultWrapper
import com.pomodorotime.data.task.TaskRepository
import com.pomodorotime.task.taskList.list.TaskListEvent
import com.pomodorotime.task.taskList.list.TaskListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@ExperimentalCoroutinesApi
class TaskViewModel(private val taskRepository: TaskRepository) :
    BaseViewModel<TaskListEvent, TaskListScreenState>() {

    private val _screenState: MutableLiveData<Event<TaskListScreenState>> =
        MutableLiveData()
    val screenState: LiveData<Event<TaskListScreenState>>
        get() = _screenState
    private val _taskList: MutableLiveData<List<TaskListItem>> = MutableLiveData(emptyList())

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
            taskRepository.getAllTasks().map { tasks ->
                tasks.map {
                    TaskListItem(
                        it.id ?: -1,
                        it.name,
                        it.creationDate,
                        it.estimatedPomodoros.toString()
                    )
                }
            }.onStart {
                _screenState.value = Event(TaskListScreenState.Loading)
            }.catch { error ->
                _screenState.value =
                    Event(TaskListScreenState.Error(ErrorResponse(message = error.message ?: "")))
            }.collect {
                _taskList.value = it
                _screenState.value = if (it.isEmpty()) {
                    Event(TaskListScreenState.EmptyState)
                } else {
                    Event(TaskListScreenState.DataLoaded(it))
                }
            }
        }
    }

    private fun deleteElements(tasks: List<TaskListItem>) {
        executeCoroutine {
            _screenState.value = Event(TaskListScreenState.Loading)

            val result = taskRepository.deleteTasks(tasks.map { it.id })
            val list = _taskList.value

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
                }
                is ResultWrapper.GenericError -> {
                    _screenState.value = Event(TaskListScreenState.Error(result.error))
                }
            }
        }
    }
}