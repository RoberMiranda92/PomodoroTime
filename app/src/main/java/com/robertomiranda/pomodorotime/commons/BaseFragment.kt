package com.robertomiranda.pomodorotime.commons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VM : BaseViewModel, T : ViewBinding> : Fragment() {

    protected val viewModel by viewModels(VM::class)
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
        initViews()
    }

    abstract fun createBinding(inflater: LayoutInflater): T

    abstract fun initViews()

    abstract fun observeViewModelChanges()

}