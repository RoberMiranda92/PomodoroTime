package com.pomodorotime.login

import android.view.LayoutInflater
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.pomodorotime.core.BaseFragment
import com.pomodorotime.login.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding>() {

    override val viewModel by viewModels<LoginViewModel>()

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

        binding.txConfirmPassword.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            viewModel.onConfirmPasswordSet(
                text.toString()
            )
        })

        binding.btnLogin.setOnClickListener {
            viewModel.startSign()
        }

        binding.btnSecondary.setOnClickListener {
            viewModel.toogleMode()

        }
    }

    override fun observeViewModelChanges() {
        viewModel.screenState.observe(viewLifecycleOwner, Observer<@LoginScreenState Int> {

            when (it) {
                LoginScreenState.INITIAL -> {
                    showData()
                }
                LoginScreenState.LOADING -> {
                    showLoading();
                }

                LoginScreenState.SUCCESS -> {
                    showData()
                }
            }
        })

        viewModel.loginMode.observe(viewLifecycleOwner, Observer<@LoginMode Int> {

            when (it) {
                LoginMode.SIGN_UP -> {
                    binding.ilConfirmPassword.isVisible = true
                    binding.btnSecondary.text = "Sign in instead"
                    binding.btnLogin.text = "Sing up"
                }
                LoginMode.SIGN_IN -> {
                    binding.ilConfirmPassword.isGone = true
                    binding.btnLogin.text = "Sing in"
                    binding.btnSecondary.text = "Create an account"
                }
            }
        })
    }

    private fun showLoading() {
        with(binding) {
            loginContainer.isGone = true
            loginLoader.show()
        }
    }

    private fun showData() {
        with(binding) {
            loginContainer.isVisible = true
            loginLoader.hide()
        }
    }


}