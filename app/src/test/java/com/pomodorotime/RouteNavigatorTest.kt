package com.pomodorotime

import androidx.navigation.NavController
import com.pomodorotime.login.LoginFragmentDirections
import com.pomodorotime.task.tasklist.TaskListFragmentDirections
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class RouteNavigatorTest {

    @RelaxedMockK
    private lateinit var navController: NavController

    private lateinit var navigator: RouteNavigator

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        navigator = RouteNavigator()
    }


    @After
    fun setDown() {
        navigator.unbind()
    }

    @Test
    fun navigateOnLoginSuccess() {
        //Given
        navigator.bind(navController)

        //When
        navigator.navigateOnLoginSuccess()

        //Verify
        verify { navController.navigate(LoginFragmentDirections.actionLoginFragmentToTaskList()) }
        confirmVerified(navController)
    }

    @Test
    fun navigateOnLoginSuccessNullNavigator() {
        //Given

        //When
        navigator.navigateOnLoginSuccess()

        //Verify
        verify(exactly = 0) { navController.navigate(LoginFragmentDirections.actionLoginFragmentToTaskList()) }
        confirmVerified(navController)
    }


    @Test
    fun navigateOnToCreateTask() {
        //Given
        navigator.bind(navController)

        //When
        navigator.navigateOnToCreateTask()

        //Verify
        verify { navController.navigate(TaskListFragmentDirections.actionTaskListFragmentToCreateTask()) }
        confirmVerified(navController)
    }

    @Test
    fun navigateOnToCreateTaskNullNavigator() {
        //Given

        //When
        navigator.navigateOnToCreateTask()

        //Verify
        verify(exactly = 0) { navController.navigate(TaskListFragmentDirections.actionTaskListFragmentToCreateTask()) }
        confirmVerified(navController)
    }

    @Test
    fun navigateOnToTimer() {
        //Given
        val id = 1L
        val name = "name"
        navigator.bind(navController)

        //When
        navigator.navigateOnToTimer(id, name)

        //Verify
        verify {
            navController.navigate(
                TaskListFragmentDirections.actionTaskListFragmentToTimer(id, name)
            )
        }
        confirmVerified(navController)
    }

    @Test
    fun navigateOnToTimerNullName() {
        //Given
        val id = 1L
        val name = null
        navigator.bind(navController)

        //When
        navigator.navigateOnToTimer(id, name)

        //Verify
        verify {
            navController.navigate(
                TaskListFragmentDirections.actionTaskListFragmentToTimer(id, name)
            )
        }
        confirmVerified(navController)
    }

    @Test
    fun navigateOnToTimerNullNavigator() {
        //Given
        val id = 1L
        val name = "name"

        //When
        navigator.navigateOnToTimer(id, name)

        //Verify
        verify(exactly = 0) {
            navController.navigate(
                TaskListFragmentDirections.actionTaskListFragmentToTimer(id, name)
            )
        }
        confirmVerified(navController)
    }

    @Test
    fun navigateOnBack() {
        //Given
        navigator.bind(navController)

        //When
        navigator.onBack()

        //Verify
        verify { navController.popBackStack() }
        confirmVerified(navController)
    }

    @Test
    fun navigateOnBackNullNavigator() {
        //Given

        //When
        navigator.onBack()

        //Verify
        verify(exactly = 0) { navController.popBackStack() }
        confirmVerified(navController)
    }
}