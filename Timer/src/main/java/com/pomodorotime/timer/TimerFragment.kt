package com.pomodorotime.timer

import android.view.LayoutInflater
import androidx.navigation.fragment.navArgs
import com.pomodorotime.core.BaseFragment
import com.pomodorotime.core.observeEvent
import com.pomodorotime.timer.databinding.FragmentTimeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TimerFragment :
    BaseFragment<TimerEvents, TimerScreenState, TimerViewModel, FragmentTimeBinding>() {

    override val viewModel by viewModel<TimerViewModel>()
    private val args: TimerFragmentArgs by navArgs()

    override fun createBinding(inflater: LayoutInflater) = FragmentTimeBinding.inflate(inflater)

    override fun initViews() {

        args.taskName?.let {
            binding.toolbar.title = it
        }
    }

    override fun observeViewModelChanges() {
        viewModel.screenState.observeEvent(this) {

            when (it) {
                is TimerScreenState.DataLoaded -> {

                }

                is TimerScreenState.Loading -> {

                }
            }
        }
    }
}