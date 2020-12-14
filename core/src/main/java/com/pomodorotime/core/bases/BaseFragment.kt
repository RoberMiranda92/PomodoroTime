package com.pomodorotime.core.bases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.pomodorotime.core.logger.PomodoroLogger
import com.pomodorotime.core.showSnackBarError
import org.koin.android.ext.android.inject

abstract class BaseFragment<in Event, State, VM : BaseViewModel<Event, State>, T : ViewBinding> :
    Fragment() {

    private lateinit var callback: OnBackPressedCallback
    protected abstract val viewModel: VM
    protected lateinit var binding: T
    protected val logger: PomodoroLogger by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = createBinding(inflater)
        callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModelChanges()
        observeBaseViewModelChanges()
        initViews()
        initEvent()
    }

    private fun observeBaseViewModelChanges() {
        viewModel.networkError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackBarError("Check conexion", Snackbar.LENGTH_LONG)
            }
        })

        viewModel.screenState.observe(viewLifecycleOwner, Observer {
            logger.logDebug("New state $it")
            onNewState(it.peekContent())
        })
    }

    abstract fun createBinding(inflater: LayoutInflater): T

    abstract fun initViews()

    abstract fun initEvent()

    abstract fun observeViewModelChanges()

    abstract fun onNewState(state: State)

    //Override this to manage back in fragments
    protected open fun onBackPressed() {
        callback.isEnabled = false
        requireActivity().onBackPressed()
    }
}