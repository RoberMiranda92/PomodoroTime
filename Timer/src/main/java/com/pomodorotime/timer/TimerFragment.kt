package com.pomodorotime.timer

import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.pomodorotime.core.BaseFragment
import com.pomodorotime.timer.databinding.FragmentTimeBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TimerFragment :
    BaseFragment<TimerEvents, TimerScreenState, TimerViewModel, FragmentTimeBinding>() {

    override val viewModel by viewModel<TimerViewModel>()
    private val navigator: TimeNavigator by inject()
    private val args: TimerFragmentArgs by navArgs()

    override fun createBinding(inflater: LayoutInflater) = FragmentTimeBinding.inflate(inflater)

    override fun initViews() {
        args.taskName?.let {
            setUpToolbar(it)
        }


    }

    private fun setUpPlayButton(@TimerStatus mode: Int) {
        binding.playButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), when (mode) {
                    TimerStatus.PLAY -> {
                        R.drawable.ic_baseline_pause_24
                    }
                    TimerStatus.PAUSE -> {
                        R.drawable.ic_baseline_play_arrow
                    }
                    else -> R.drawable.ic_baseline_play_arrow
                }
            )
        )
        binding.playButton.setOnClickListener { viewModel.postEvent(TimerEvents.OnPlayStopButtonClicked) }
    }

    override fun observeViewModelChanges() {

    }

    override fun onNewState(state: TimerScreenState) {
        when (state) {
            is TimerScreenState.Initial -> {
                viewModel.postEvent(TimerEvents.LoadData(args.taskId))
            }

            is TimerScreenState.DataLoaded -> {
                setUptDetail(state.taskDetail, state.mode)
                setUpTimer(state.time)
                setUpPlayButton(state.status)
            }

            is TimerScreenState.Loading -> {

            }
        }
    }

    private fun setUpToolbar(it: String) {
        with(binding.toolbar) {
            title = it
            setNavigationIcon(R.drawable.ic_action_back)
            setNavigationOnClickListener {
                navigator.onBack()
            }
        }
    }

    private fun setUptDetail(taskDetail: TimeDetail, @PomodoroMode mode: Int) {
        binding.txPomodoros.text = when (mode) {
            PomodoroMode.POMODORO -> {
                getString(
                    R.string.timer_done_pomodoros,
                    taskDetail.donePomodoros,
                    taskDetail.total
                )
            }
            PomodoroMode.SHORT_BREAK -> {
                "Time to have a break"
            }

            PomodoroMode.LONG_BREAK -> {
                "Time to have a Long break"
            }

            else -> getString(
                R.string.timer_done_pomodoros,
                taskDetail.donePomodoros,
                taskDetail.total
            )
        }
    }

    private fun setUpTimer(time: Long) {
        val minutes = time / 1000 / 60
        val seconds = time / 1000 % 60
        binding.txTimer.text = getString(R.string.timer_timer, minutes, seconds)
    }

    companion object {
        const val POMODORO_DEFAULT_TIMER: Long = 1500000
    }
}