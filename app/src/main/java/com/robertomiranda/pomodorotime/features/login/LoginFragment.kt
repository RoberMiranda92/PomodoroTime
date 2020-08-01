package com.robertomiranda.pomodorotime.features.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.robertomiranda.pomodorotime.LoginScreenState
import com.robertomiranda.pomodorotime.commons.BaseFragment
import com.robertomiranda.pomodorotime.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding>() {


    override fun createBinding(inflater: LayoutInflater) = FragmentLoginBinding.inflate(inflater)

    override fun initViews() {

        binding.txEmail.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            viewModel.onEmailSet(
                text.toString()
            )
        })
        binding.txPassword.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            viewModel.onPasswordSet(
                text.toString()
            )
        })

        binding.btnLogin.setOnClickListener { viewModel.startSignUp() }
    }

    override fun observeViewModelChanges() {
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
}