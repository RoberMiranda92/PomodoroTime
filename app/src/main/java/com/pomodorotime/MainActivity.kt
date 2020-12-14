package com.pomodorotime

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.pomodorotime.core.observeEvent
import com.pomodorotime.core.session.ISessionManager
import com.pomodorotime.databinding.ActivityMainBinding
import com.pomodorotime.koin.NAVIGATOR_NAME
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class MainActivity : AppCompatActivity() {

    private val navigator: RouteNavigator by inject(named(NAVIGATOR_NAME))
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        observeViewModelChanges()
        viewModel.onCreate()
    }

    private fun observeViewModelChanges() {
        viewModel.navigation.observeEvent(this, { _event ->
            when(_event){
                ISessionManager.SessionEvent.LOGIN -> {

                }
                ISessionManager.SessionEvent.LOGOUT -> {
                    navigator.navigateToLogin()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        navigator.bind(findNavController(R.id.nav_host_fragment))
    }

    override fun onStop() {
        super.onStop()
        navigator.unbind()
    }
}