package com.pomodorotime.timer

import android.view.LayoutInflater
import com.pomodorotime.core.BaseFragment
import com.pomodorotime.timer.databinding.FragmentTimeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TimerFragment :
    BaseFragment<TimerEvents, TimerScreenState, TimerViewModel, FragmentTimeBinding>() {

    override val viewModel by viewModel<TimerViewModel>()

    override fun createBinding(inflater: LayoutInflater) = FragmentTimeBinding.inflate(inflater)

    override fun initViews() {

    }

    override fun observeViewModelChanges() {

    }
}