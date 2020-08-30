package com.pomodorotime.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<in Event, out State, VM : BaseViewModel<Event, State>, T : ViewBinding> :
    Fragment() {

    protected abstract val viewModel: VM
    protected lateinit var binding: T

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = createBinding(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModelChanges()
        observeBaseViewModelChanges()
        initViews()
    }

    private fun observeBaseViewModelChanges() {
        viewModel.networkError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackBarError("Check conexion", Snackbar.LENGTH_LONG)
            }
        })
    }

    abstract fun createBinding(inflater: LayoutInflater): T

    abstract fun initViews()

    abstract fun observeViewModelChanges()

}