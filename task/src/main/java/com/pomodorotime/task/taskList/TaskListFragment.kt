package com.pomodorotime.task.tasklist

import android.view.ActionMode
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pomodorotime.core.BaseFragment
import com.pomodorotime.core.BaseMultiSelectorAdapter
import com.pomodorotime.core.showSnackBarError
import com.pomodorotime.task.R
import com.pomodorotime.task.TaskNavigator
import com.pomodorotime.task.databinding.FragmentTaskListBinding
import com.pomodorotime.task.tasklist.list.TaskListAdapter
import com.pomodorotime.task.tasklist.list.TaskListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class TaskListFragment :
    BaseFragment<TaskListEvent, TaskListScreenState, TaskViewModel, FragmentTaskListBinding>(),
    BaseMultiSelectorAdapter.OnItemClick<TaskListItem> {

    private val navigator: TaskNavigator by inject()
    override val viewModel: TaskViewModel by viewModel()
    private var actionMode: ActionMode? = null
    private val taskListAdapter: TaskListAdapter by lazy {
        TaskListAdapter(this)
    }

    override fun createBinding(inflater: LayoutInflater) = FragmentTaskListBinding.inflate(inflater)

    override fun initViews() {
        configureAddButton()
        configureList()
        configureStatusView()
    }

    override fun observeViewModelChanges() {

    }

    override fun onNewState(state: TaskListScreenState) {
        when (state) {
            is TaskListScreenState.Initial -> {
                viewModel.postEvent(TaskListEvent.Load)
            }

            is TaskListScreenState.Loading -> {
                showLoading()
                hideAddButton()
                hideEmptyState()
                hideList()
                finishActionMode()
            }

            is TaskListScreenState.EmptyState -> {
                hideLoading()
                hideList()
                hideAddButton()
                this.taskListAdapter.submitList(emptyList())
                showEmptyState()
            }

            is TaskListScreenState.DataLoaded -> {
                hideLoading()
                showList()
                showAddButton()
                hideEmptyState()
                finishActionMode()
                this.taskListAdapter.submitList(state.taskList)
                showAddButton()
            }

            is TaskListScreenState.Editing -> {
                hideLoading()
                hideAddButton()
                hideEmptyState()
            }
            is TaskListScreenState.NavigateToCreateTask -> {
                navigator.navigateOnToCreateTask()
                viewModel.postEvent(TaskListEvent.OnNavigationDone)

            }
            is TaskListScreenState.Error -> {
                hideLoading()
                hideAddButton()
                showSnackBarError(state.error, Snackbar.LENGTH_LONG)
            }
        }
    }

    override fun onLongPress(selectedSize: Int) {
        startActionMode(selectedSize)
    }

    override fun onItemSelectedClick(selectedSize: Int) {
        actionMode?.also {
            if (selectedSize > 0) {
                it.title = getMenuTitle(selectedSize)
            } else {
                it.finish()
            }
        }
    }

    override fun onItemClick(element: TaskListItem) {
        navigator.navigateOnToTimer(element.id, element.name)
    }

    private fun manageMenuItemClick(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.delete -> {
                viewModel.postEvent(TaskListEvent.DeleteTaskElementsPressed(taskListAdapter.getSelectedItems()))
            }
        }
    }

    private fun configureAddButton() {
        binding.fbAddTask.setOnClickListener {
            viewModel.postEvent(TaskListEvent.AddTaskPressed)
        }
    }

    private fun configureList() {
        with(binding.rvTasks) {
            this.adapter = taskListAdapter
            this.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun configureStatusView() {
        binding.statusView.setButtonAction {
            viewModel.postEvent(TaskListEvent.AddTaskPressed)
        }
    }

    private fun hideAddButton() {
        binding.fbAddTask.isGone = true
    }

    private fun showAddButton() {
        binding.fbAddTask.isVisible = true
    }

    private fun showLoading() {
        binding.loader.isVisible = true
    }

    private fun hideLoading() {
        binding.loader.isGone = true
    }

    private fun showEmptyState() {
        binding.statusView.isVisible = true
    }

    private fun hideEmptyState() {
        binding.statusView.isGone = true
    }

    private fun showList() {
        binding.rvTasks.isVisible = true
    }

    private fun hideList() {
        binding.rvTasks.isGone = true
    }

    private fun startActionMode(selectedItems: Int) {
        actionMode = ActionModeCallback.Builder()
            .setView(binding.toolbar)
            .setMenu(R.menu.delete_share_menu)
            .setTitle(getMenuTitle(selectedItems))
            .setOnItemClick { item -> manageMenuItemClick(item) }
            .setOnShowActionMode { viewModel.postEvent(TaskListEvent.EditTaskList) }
            .setOnFinisActionMode { viewModel.postEvent(TaskListEvent.EditTaskListFinished) }
            .build()
            .startActionMode()
    }

    private fun finishActionMode() {
        actionMode?.finish()
    }

    private fun getMenuTitle(items: Int): String = getString(R.string.action_menu_title, items)

}