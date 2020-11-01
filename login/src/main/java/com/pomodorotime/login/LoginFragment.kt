package com.pomodorotime.login

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.pomodorotime.core.*
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
        viewModel.emailError.observeEvent(viewLifecycleOwner) {
            binding.tilEmail.error = it
        }

        viewModel.passwordError.observeEvent(viewLifecycleOwner) {
            binding.tilPassword.error = it
        }

        viewModel.error.observeEvent(viewLifecycleOwner) {
            if (it.show) {
                showSnackBarError(it.message, Snackbar.LENGTH_SHORT)
            }
        }

        viewModel.saveTokenDialog.observeEvent(viewLifecycleOwner) {
            if (it) {
                showDialog(getString(R.string.login_dialog_title),
                    getString(R.string.login_dialog_message),
                    getString(R.string.login_dialog_possitive),
                    getString(R.string.login_dialog_negative),
                    { viewModel.postEvent(LoginEvent.OnUserPositiveClickPress) },
                    { viewModel.postEvent(LoginEvent.OnNegativePositiveClickPress) }
                )
            }
        }

        viewModel.navigationToDashboard.observeEvent(viewLifecycleOwner) {
            if (it) {
                navigator.navigateOnLoginSuccess()
            }
        }
    }

    override fun onNewState(state: LoginScreenState) {
        when (state) {
            is LoginScreenState.SignIn -> {
                showData()
                configureSignIn()
            }

            is LoginScreenState.SignUp -> {
                showData()
                configureSignUp()
            }
            is LoginScreenState.Loading -> {
                showLoading()
                hideKeyboard()
            }
        }
    }

    private fun configureSignIn() {
        with(binding) {
            tilConfirmPassword.isGone = true
            btnLogin.text = resources.getString(R.string.login_sign_in)
            btnSecondary.text =
                resources.getString(R.string.login_create_an_account)
        }
    }

    private fun configureSignUp() {
        with(binding) {
            tilConfirmPassword.isVisible = true
            btnSecondary.text = resources.getString(R.string.login_sign_in_instead)
            btnLogin.text = resources.getString(R.string.login_sign_up)
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