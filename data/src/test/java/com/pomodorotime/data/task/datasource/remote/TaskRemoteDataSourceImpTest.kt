package com.pomodorotime.data.task.datasource.remote

import com.pomodorotime.data.CoroutinesRule
import com.pomodorotime.data.task.api.ITaskApi
import com.pomodorotime.data.task.api.models.ApiTask
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import java.util.Date
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TaskRemoteDataSourceImpTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var api: ITaskApi

    lateinit var dataSource: TaskRemoteDataSourceImp

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        dataSource = TaskRemoteDataSourceImp(api)
    }

    @Test
    fun `insert OK`() = coroutinesRule.runBlockingTest {
        //Given
        val userId = "id"

        //When
        coEvery { api.insetTask(any(), any()) } returns Unit

        dataSource.insetTask(userId, Task1)

        //Verify
        dataSource
        coVerify { api.insetTask(userId, Task1) }
        confirmVerified(api)
    }

    @Test
    fun `delete OK`() = coroutinesRule.runBlockingTest {
        //Given
        val userId = "id"
        val taskId = Task1.id
        //When
        coEvery { api.deleteTask(any(), any()) } returns Unit

        dataSource.deleteTask(userId, taskId)

        //Verify
        dataSource
        coVerify { api.deleteTask(userId, taskId) }
        confirmVerified(api)
    }

    companion object {
        private val Task1 = ApiTask(
            id = 1,
            name = "Task1",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )
    }
}