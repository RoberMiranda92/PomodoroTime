package com.pomodorotime.task.create

import android.view.LayoutInflater
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.pomodorotime.core.bases.BaseFragment
import com.pomodorotime.core.observeEvent
import com.pomodorotime.core.removeErrorOnTyping
import com.pomodorotime.core.showSnackBar
import com.pomodorotime.core.showSnackBarError
import com.pomodorotime.task.R
import com.pomodorotime.task.TaskNavigator
import com.pomodorotime.task.databinding.FragmentCreateTaskBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateTaskFragment :
    BaseFragment<CreateTaskEvent, CreateTaskScreenState, CreateTaskViewModel, FragmentCreateTaskBinding>() {

    override val viewModel: CreateTaskViewModel by viewModel()
    private val navigator: TaskNavigator by inject()

    override fun createBinding(inflater: LayoutInflater) =
        FragmentCreateTaskBinding.inflate(inflater)

    override fun initViews() {
        configureToolbar()
        configureEditText()
        configureCounter()
    }

    override fun initEvent() {
    }

    override fun observeViewModelChanges() {
        viewModel.createTaskError.observeEvent(viewLifecycleOwner) {
            if (it.show) {
                showSnackBarError(it.message, Snackbar.LENGTH_LONG)
            }
        }
    }

    override fun onNewState(state: CreateTaskScreenState) {
        when (state) {
            is CreateTaskScreenState.Initial -> {
                hideLoading()
                showUI()
                binding.tilTaskName.editText?.setText(state.name)
                binding.pcvCounter.count = state.estimated
            }
            is CreateTaskScreenState.InvalidName -> {
                showUI()
                binding.tilTaskName.error = getString(R.string.create_task_invalid_name)
            }
            is CreateTaskScreenState.Loading -> {
                showLoading()
                hideUI()
            }
            is CreateTaskScreenState.Success -> {
                showSnackBar(
                    getString(R.string.create_task_success),
                    Snackbar.LENGTH_LONG,
                    R.color.success_color
                )
                hideLoading()
                showUI()
            }
        }
    }

    private fun configureToolbar() {
        with(binding.toolbar) {
            setNavigationIcon(R.drawable.ic_action_back)
            setNavigationOnClickListener {
                navigator.onBack()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        viewModel.postEvent(CreateTaskEvent.SaveTask)
                    }
                }
                true
            }
        }
    }

    private fun configureEditText() {
        binding.tilTaskName.removeErrorOnTyping()
        binding.txTaskName.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            viewModel.postEvent(
                CreateTaskEvent.EditingTask(
                    text.toString(),
                    binding.pcvCounter.count
                )
            )
        })
    }

    private fun configureCounter() {
        binding.pcvCounter.count = 1
        binding.pcvCounter.onCounterClicked().onEach {
            viewModel.postEvent(
                CreateTaskEvent.EditingTask(
                    binding.txTaskName.text.toString(),
                    it
                )
            )
        }.launchIn(lifecycleScope)
    }

    private fun showLoading() {
        binding.loader.isVisible = true
    }

    private fun hideLoading() {
        binding.loader.isGone = true
    }

    private fun showUI() {
        binding.container.isVisible = true
    }

    private fun hideUI() {
        binding.container.isGone = true
    }
}