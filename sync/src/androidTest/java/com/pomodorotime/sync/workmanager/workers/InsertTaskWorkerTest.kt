package com.pomodorotime.sync.workmanager.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.await
import androidx.work.testing.TestListenableWorkerBuilder
import com.pomodorotime.data.task.api.models.ApiTask
import com.pomodorotime.data.task.datasource.remote.ITaskRemoteDataSource
import com.pomodorotime.data.user.IUserLocalDataSource
import com.pomodorotime.sync.workmanager.TaskWorkerFactory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

//More info in https://dev.to/ayevbeosa/writing-tests-workmanager-edition-3aa
@RunWith(AndroidJUnit4::class)
class InsertTaskWorkerTest {

    private lateinit var context: Context

    @MockK
    lateinit var userDataSource: IUserLocalDataSource

    @MockK
    lateinit var taskDataSource: ITaskRemoteDataSource

    private lateinit var factory: TaskWorkerFactory

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        context = ApplicationProvider.getApplicationContext()
        factory = TaskWorkerFactory(userDataSource, taskDataSource)
    }

    private fun buildWorker(data: Data): ListenableWorker {

        return TestListenableWorkerBuilder<InsertTaskWorker>(context)
            .setWorkerFactory(factory)
            .setInputData(data)
            .build()
    }

    private fun buildData(): Data {

        val data = Data.Builder()
        data.putLong(InsertTaskWorker.TASK_ID_ARGS, Task1.id ?: -1L)
        data.putString(InsertTaskWorker.TASK_NAME_ARGS, Task1.name)
        data.putLong(InsertTaskWorker.TASK_CREATION_DATE_ARGS, Task1.creationDate.time)
        data.putInt(InsertTaskWorker.TASK_DONE_POMODOROS_ARGS, Task1.donePomodoros)
        data.putInt(InsertTaskWorker.TASK_ESTIMATED_POMODOROS_ARGS, Task1.estimatedPomodoros)
        data.putInt(InsertTaskWorker.TASK_SHORT_BREAKS_ARGS, Task1.shortBreaks)
        data.putInt(InsertTaskWorker.TASK_LONG_BREAKS_ARGS, Task1.longBreaks)
        data.putBoolean(InsertTaskWorker.TASK_COMPLETED_ARGS, Task1.completed)

        return data.build()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun testInsertTaskWorkerOk() = runBlocking {

        //Given
        val worker: ListenableWorker = buildWorker(buildData())

        //When
        coEvery { userDataSource.getUserId() } returns userId
        coEvery { taskDataSource.insetTask(any(), any()) } returns Unit

        val result = worker.startWork()

        //Verify
        assertThat(result.await(), `is`(ListenableWorker.Result.success()))
        coVerify { userDataSource.getUserId() }
        coVerify { taskDataSource.insetTask(userId, Task1) }

        confirmVerified(userDataSource)
        confirmVerified(taskDataSource)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun testInsertTaskWorkerError() = runBlocking {

        //Given
        val worker: ListenableWorker = buildWorker(buildData())

        //When
        coEvery { userDataSource.getUserId() } returns userId
        coEvery { taskDataSource.insetTask(any(), any()) } throws (Exception("My Error"))

        val result = worker.startWork()

        //Verify
        assertThat(result.await(), `is`(ListenableWorker.Result.retry()))
        coVerify { userDataSource.getUserId() }
        coVerify { taskDataSource.insetTask(userId, Task1) }

        confirmVerified(userDataSource)
        confirmVerified(taskDataSource)
    }

    companion object {
        var userId: String = "userId"

        var Task1: ApiTask = ApiTask(
            1, "Name",
            Date(), 0, 1, 4, 0,
            false
        )
    }
}
