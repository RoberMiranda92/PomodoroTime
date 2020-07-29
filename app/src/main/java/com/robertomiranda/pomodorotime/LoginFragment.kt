package com.robertomiranda.pomodorotime

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.robertomiranda.pomodorotime.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        // Inflate the layout for this fragment
        observeViewModelChanges()
        return binding.root

    }

    private fun observeViewModelChanges() {
        viewModel.screenState.observe(viewLifecycleOwner, Observer<@LoginScreenState Int> {

            when (it) {
                LoginScreenState.INITIAL -> {
                    Log.d("LoginFragment", "state: initial")

                }
                LoginScreenState.LOADING -> {
                    Log.d("LoginFragment", "state: loading")
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txEmail.addTextChangedListener(onTextChanged = { text, start, before, count ->
            viewModel.onEmailSet(
                text.toString()
            )
        })
        binding.txPassword.addTextChangedListener(onTextChanged = { text, start, before, count ->
            viewModel.onPasswordSet(
                text.toString()
            )
        })

        binding.btnLogin.setOnClickListener { viewModel.startSignUp() }

    }
}