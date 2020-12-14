package com.pomodorotime.sync.workmanager.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.await
import androidx.work.testing.TestListenableWorkerBuilder
import com.pomodorotime.core.session.ISessionManager
import com.pomodorotime.data.sync.ISyncErrorHandler
import com.pomodorotime.data.sync.SyncError
import com.pomodorotime.data.task.api.models.ApiTask
import com.pomodorotime.data.task.datasource.remote.ITaskRemoteDataSource
import com.pomodorotime.data.user.IUserLocalDataSource
import com.pomodorotime.sync.workmanager.TaskWorkerFactory
import io.mockk.*
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
class DeleteTaskWorkerTest {

    private lateinit var context: Context

    @MockK
    lateinit var userDataSource: IUserLocalDataSource

    @MockK
    lateinit var taskDataSource: ITaskRemoteDataSource

    @MockK
    lateinit var errorHandler: ISyncErrorHandler

    @MockK
    lateinit var syncManager: ISessionManager

    private lateinit var factory: TaskWorkerFactory

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        context = ApplicationProvider.getApplicationContext()
        factory = TaskWorkerFactory(userDataSource, taskDataSource, errorHandler, syncManager)
    }

    private fun buildWorker(data: Data): ListenableWorker {

        return TestListenableWorkerBuilder<DeleteTaskWorker>(context)
            .setWorkerFactory(factory)
            .setInputData(data)
            .build()
    }

    private fun buildData(): Data {

        val data = Data.Builder()
        data.putLong(InsertTaskWorker.TASK_ID_ARGS, Task1.id ?: -1L)

        return data.build()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun testDeleteTaskWorkerOk() = runBlocking {

        //Given
        val worker: ListenableWorker = buildWorker(buildData())

        //When
        coEvery { userDataSource.getToken() } returns userId
        coEvery { taskDataSource.deleteTask(any(), any()) } returns Unit

        val result = worker.startWork()

        //Verify
        assertThat(result.await(), `is`(ListenableWorker.Result.success()))
        coVerifyOrder {
            userDataSource.getToken()
            taskDataSource.deleteTask(userId, Task1.id)
        }

        confirmVerified(userDataSource, taskDataSource, syncManager)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun testDeleteTaskWorkerErrorInvalidUser() = runBlocking {

        //Given
        val exception = Exception("My Error") // We need to use this error to avoid
        val error = SyncError.InvalidUser(-1, exception.message ?: "")
        val worker: ListenableWorker = buildWorker(buildData())

        //When
        coEvery { userDataSource.getToken() } returns InsertTaskWorkerTest.userId
        coEvery { taskDataSource.deleteTask(any(), any()) } throws exception
        coEvery { errorHandler.getSyncError(any()) } returns error
        coEvery { syncManager.onLogout() } just runs
        coEvery { userDataSource.clearToken() } just runs

        val result = worker.startWork()

        //Verify
        assertThat(result.await(), `is`(ListenableWorker.Result.retry()))
        coVerifyOrder {
            userDataSource.getToken()
            taskDataSource.deleteTask(
                InsertTaskWorkerTest.userId,
                Task1.id
            )
            errorHandler.getSyncError(any())
            syncManager.onLogout()
            userDataSource.clearToken()
        }

        confirmVerified(userDataSource, taskDataSource, syncManager)

    }

    @Test
    @ExperimentalCoroutinesApi
    fun testDeleteTaskWorkerErrorDataBaseError() = runBlocking {

        //Given
        val exception = Exception("My Error") // We need to use this error to avoid
        val error = SyncError.DataBaseError(-1, exception.message ?: "")
        val worker: ListenableWorker = buildWorker(buildData())

        //When
        coEvery { userDataSource.getToken() } returns InsertTaskWorkerTest.userId
        coEvery { taskDataSource.deleteTask(any(), any()) } throws exception
        coEvery { errorHandler.getSyncError(any()) } returns error

        val result = worker.startWork()

        //Verify
        assertThat(result.await(), `is`(ListenableWorker.Result.retry()))
        coVerifyOrder {
            userDataSource.getToken()
            taskDataSource.deleteTask(
                InsertTaskWorkerTest.userId,
                Task1.id
            )
            errorHandler.getSyncError(any())
        }

        confirmVerified(userDataSource, taskDataSource, syncManager)

    }

    @Test
    @ExperimentalCoroutinesApi
    fun testDeleteTaskWorkerErrorNetworkError() = runBlocking {

        //Given
        val exception = Exception("My Error") // We need to use this error to avoid
        val error = SyncError.NetworkError
        val worker: ListenableWorker = buildWorker(buildData())

        //When
        coEvery { userDataSource.getToken() } returns InsertTaskWorkerTest.userId
        coEvery { taskDataSource.deleteTask(any(), any()) } throws exception
        coEvery { errorHandler.getSyncError(any()) } returns error

        val result = worker.startWork()

        //Verify
        assertThat(result.await(), `is`(ListenableWorker.Result.retry()))
        coVerifyOrder {
            userDataSource.getToken()
            taskDataSource.deleteTask(
                InsertTaskWorkerTest.userId,
                Task1.id
            )
            errorHandler.getSyncError(any())
        }

        confirmVerified(userDataSource, taskDataSource, syncManager)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun testDeleteTaskWorkerErrorGenericError() = runBlocking {

        //Given
        val exception = Exception("My Error") // We need to use this error to avoid
        val error = SyncError.GenericError(-1, exception.message ?: "")
        val worker: ListenableWorker = buildWorker(buildData())

        //When
        coEvery { userDataSource.getToken() } returns InsertTaskWorkerTest.userId
        coEvery { taskDataSource.deleteTask(any(), any()) } throws exception
        coEvery { errorHandler.getSyncError(any()) } returns error

        val result = worker.startWork()

        //Verify
        assertThat(result.await(), `is`(ListenableWorker.Result.retry()))
        coVerifyOrder {
            userDataSource.getToken()
            taskDataSource.deleteTask(
                InsertTaskWorkerTest.userId,
                Task1.id
            )
            errorHandler.getSyncError(any())
        }

        confirmVerified(userDataSource, taskDataSource, syncManager)
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
