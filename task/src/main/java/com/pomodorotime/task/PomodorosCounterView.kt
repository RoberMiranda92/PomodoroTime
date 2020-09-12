package com.pomodorotime.task

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import androidx.constraintlayout.widget.ConstraintLayout
import com.pomodorotime.task.databinding.ViewPomodoroCounterActionsBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
class PomodorosCounterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var count: Int = 1
        set(value) {
            binding.count.text = value.toString()
            field = value
        }
    private val binding: ViewPomodoroCounterActionsBinding =
        ViewPomodoroCounterActionsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        count = 1
    }

    fun onCounterClicked() = callbackFlow {
        val decrementListener = OnClickListener {
            count = count.dec()
            offer(count)
        }

        val incrementListener = OnClickListener {
            count = count.inc()
            offer(count)
        }
        binding.decrement.setOnClickListener(decrementListener)
        binding.increment.setOnClickListener(incrementListener)

        awaitClose {
            binding.decrement.setOnClickListener(null)
            binding.increment.setOnClickListener(null)
        }
    }
}