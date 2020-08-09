package com.pomodorotime.login

import android.view.LayoutInflater
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.pomodorotime.core.BaseFragment
import com.pomodorotime.core.removeErrorOnTyping
import com.pomodorotime.core.showSnackBarError
import com.pomodorotime.login.databinding.FragmentLoginBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding>() {

    override val viewModel by viewModel<LoginViewModel>()

    private val navigator: LoginNavigator by inject()

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

        binding.tilEmail.removeErrorOnTyping()
        binding.tilPassword.removeErrorOnTyping()

        binding.btnLogin.setOnClickListener {
            viewModel.startSign()
        }

        binding.btnSecondary.setOnClickListener {
            viewModel.toogleMode()
        }
    }

    override fun observeViewModelChanges() {
        viewModel.screenState.observe(viewLifecycleOwner, Observer {

            when (it) {
                LoginScreenState.INITIAL -> {
                    showData()
                }
                LoginScreenState.LOADING -> {
                    showLoading();
                }

                LoginScreenState.SUCCESS -> {
                    navigator.navigateOnLoginSuccess()
                }
            }
        })

        viewModel.loginMode.observe(viewLifecycleOwner, Observer {

            when (it) {
                LoginMode.SIGN_UP -> {
                    binding.ilConfirmPassword.isVisible = true
                    binding.btnSecondary.text = resources.getString(R.string.login_sign_in_instead)
                    binding.btnLogin.text = resources.getString(R.string.login_sign_up)
                }
                LoginMode.SIGN_IN -> {
                    binding.ilConfirmPassword.isGone = true
                    binding.btnLogin.text = resources.getString(R.string.login_sign_in)
                    binding.btnSecondary.text =
                        resources.getString(R.string.login_create_an_account)
                }
            }
        })

        viewModel.invalidEmailError.observe(viewLifecycleOwner, Observer {
            binding.tilEmail.error = it
        })

        viewModel.invalidPasswordError.observe(viewLifecycleOwner, Observer {
            binding.tilPassword.error = it
        })

        viewModel.loginError.observe(viewLifecycleOwner, Observer {
            showSnackBarError(it, Snackbar.LENGTH_SHORT)
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