package com.pomodorotime.data.task

import com.pomodorotime.data.CoroutinesRule
import com.pomodorotime.data.sync.ISyncManager
import com.pomodorotime.data.task.api.models.ApiTask
import com.pomodorotime.data.task.dataBase.TaskEntity
import com.pomodorotime.data.task.datasource.local.ITaskLocalDataSource
import com.pomodorotime.domain.task.ITaskRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class TaskRepositoryTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var taskRepository: ITaskRepository

    @MockK
    lateinit var localTaskDaSource: ITaskLocalDataSource

    @MockK
    lateinit var syncManager: ISyncManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        taskRepository = TaskRepository(localTaskDaSource, syncManager)
    }

    @Test
    fun `get all Task ok`() = coroutinesRule.runBlockingTest {
        //Given
        val taskList = listOf(TaskEntity, TaskEntity2, TaskEntity3)

        //When
        coEvery { localTaskDaSource.getAllTasks() } returns flowOf(taskList)
        val result = taskRepository.getAllTasks().toList().first()

        //Verify
        Assert.assertEquals(taskList.map { it.toDomainModel() }, result)
        coVerify { localTaskDaSource.getAllTasks() }
        confirmVerified(localTaskDaSource)
        confirmVerified(syncManager)
    }

    @Test
    fun `get task by id ok`() = coroutinesRule.runBlockingTest {
        //Given
        val task = TaskEntity.apply { id = 1 }

        //When
        coEvery { localTaskDaSource.getTaskById(any()) } returns task
        val result = taskRepository.getTaskById(1)

        //Verify
        Assert.assertEquals(result, task.toDomainModel())
        coVerify { localTaskDaSource.getTaskById(1) }
        confirmVerified(localTaskDaSource)
        confirmVerified(syncManager)
    }

    @Test
    fun `insert ok`() = coroutinesRule.runBlockingTest {
        //Given
        val task = TaskEntity.toDomainModel()
        val modelTaskCaptured = slot<TaskEntity>()
        val apiTaskCaptured = slot<ApiTask>()
        //When
        coEvery { localTaskDaSource.insetTask(capture(modelTaskCaptured)) } returns -1
        coEvery { syncManager.performSyncInsertion(capture(apiTaskCaptured)) } returns Unit

        val result = taskRepository.insetTask(task)
        task.apply {
            id = -1
        }
        //Verify
        Assert.assertThat(result, `is`(-1))
        Assert.assertThat(apiTaskCaptured.captured, `is`(task.toApiTaskModel()))
        Assert.assertThat(modelTaskCaptured.captured, `is`(TaskEntity))
        coVerify { localTaskDaSource.insetTask(modelTaskCaptured.captured) }
        coVerify { syncManager.performSyncInsertion(apiTaskCaptured.captured) }
        confirmVerified(localTaskDaSource)
        confirmVerified(syncManager)
    }

    @Test
    fun `delete all list ok`() = coroutinesRule.runBlockingTest {
        //Given
        val taskList = listOf(TaskEntity, TaskEntity2, TaskEntity3).mapNotNull { it.id }
        val modelTaskCaptured = slot<List<Long>>()

        //When
        coEvery { localTaskDaSource.deleteTasks(capture(modelTaskCaptured)) } returns Unit
        coEvery { syncManager.performSyncDeletion(any()) } returns Unit

        taskRepository.deleteTasks(taskList)

        //Verify
        coVerify { localTaskDaSource.deleteTasks(modelTaskCaptured.captured) }
        taskList.forEach { _id ->
            coVerify(exactly = taskList.size) {
                syncManager.performSyncDeletion(_id)
            }
        }
        confirmVerified(localTaskDaSource)
        confirmVerified(syncManager)
    }

    companion object {
        private val TaskEntity = TaskEntity(
            name = "Task1",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )
        private val TaskEntity2 = TaskEntity(
            name = "Task2",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )

        private val TaskEntity3 = TaskEntity(
            name = "Task3",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )
    }
}