package com.pomodorotime.login

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.pomodorotime.core.BaseFragment
import com.pomodorotime.core.observeEvent
import com.pomodorotime.core.removeErrorOnTyping
import com.pomodorotime.core.showSnackBarError
import com.pomodorotime.login.databinding.FragmentLoginBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment :
    BaseFragment<LoginEvent, LoginScreenState, LoginViewModel, FragmentLoginBinding>() {

    override val viewModel by viewModel<LoginViewModel>()

    private val navigator: LoginNavigator by inject()

    override fun createBinding(inflater: LayoutInflater): FragmentLoginBinding {
        val contextThemeWrapper: Context =
            ContextThemeWrapper(requireContext(), R.style.LoginTheme)
        val localInflater: LayoutInflater = inflater.cloneInContext(contextThemeWrapper)
        return FragmentLoginBinding.inflate(localInflater)
    }

    override fun initViews() {

        binding.txEmail.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            viewModel.postEvent(
                LoginEvent.LoginTyping(
                    text.toString(),
                    binding.txPassword.text.toString(),
                    binding.txConfirmPassword.text.toString()

                )
            )
        })

        binding.txPassword.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            viewModel.postEvent(
                LoginEvent.LoginTyping(
                    binding.txEmail.text.toString(),
                    text.toString(),
                    binding.txConfirmPassword.text.toString()
                )
            )
        })

        binding.txConfirmPassword.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            viewModel.postEvent(
                LoginEvent.LoginTyping(
                    binding.txEmail.text.toString(),
                    binding.txPassword.text.toString(),
                    text.toString()
                )
            )
        })

        binding.tilEmail.removeErrorOnTyping()
        binding.tilPassword.removeErrorOnTyping()

        binding.btnLogin.setOnClickListener {
            viewModel.postEvent(LoginEvent.MainButtonPress)
        }

        binding.btnSecondary.setOnClickListener {
            viewModel.postEvent(LoginEvent.SecondaryButtonPress)
        }
    }

    override fun observeViewModelChanges() {
        viewModel.screenState.observeEvent(viewLifecycleOwner) {

            when (it) {
                is LoginScreenState.SignIn -> {
                    showData()
                    binding.tilConfirmPassword.isGone = true
                    binding.btnLogin.text = resources.getString(R.string.login_sign_in)
                    binding.btnSecondary.text =
                        resources.getString(R.string.login_create_an_account)
                }

                is LoginScreenState.SignUp -> {
                    showData()
                    binding.tilConfirmPassword.isVisible = true
                    binding.btnSecondary.text = resources.getString(R.string.login_sign_in_instead)
                    binding.btnLogin.text = resources.getString(R.string.login_sign_up)
                }
                is LoginScreenState.Loading -> {
                    showLoading()
                }
                is LoginScreenState.EmailError -> {
                    showData()
                    binding.tilEmail.error = it.error
                }
                is LoginScreenState.Error -> {
                    showData()
                    showSnackBarError(it.error, Snackbar.LENGTH_SHORT)
                }
                is LoginScreenState.PasswordError -> {
                    showData()

                    binding.tilPassword.error = it.error
                }
                is LoginScreenState.Success -> {
                    showData()
                    navigator.navigateOnLoginSuccess()
                }
            }
        }
    }

    private fun showLoading() {
        with(binding) {
            loginContainer.isGone = true
            loginLoader.isVisible = true
        }
    }

    private fun showData() {
        with(binding) {
            loginContainer.isVisible = true
            loginLoader.isGone = true
        }
    }


}