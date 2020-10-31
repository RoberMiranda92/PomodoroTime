package com.pomodorotime.data.task.datasource.local

import com.pomodorotime.data.CoroutinesRule
import com.pomodorotime.data.task.dataBase.IDataBase
import com.pomodorotime.data.task.dataBase.TaskEntity
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

class TaskLocalDataSourceImplTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var database: IDataBase

    lateinit var dataSource: ITaskLocalDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        dataSource = TaskLocalDataSourceImpl(database)
    }

    @Test
    fun `get all task OK`() = coroutinesRule.runBlockingTest {
        //Given
        val taskList = listOf(Task)

        //When
        coEvery { database.getAllTask() } returns flowOf(taskList)
        val value = dataSource.getAllTasks()

        //Verify
        assertEquals(value.toList().first(), taskList)
        coVerify { database.getAllTask() }
        confirmVerified(database)
    }

    @Test
    fun `insert task OK`() = coroutinesRule.runBlockingTest {
        //Given
        val taskId = -1L

        //When
        coEvery { database.insert(any<TaskEntity>()) } returns taskId
        val value = dataSource.insetTask(Task)

        //Verify
        assertEquals(value, taskId)
        coVerify { database.insert(Task) }
        confirmVerified(database)
    }

    @Test
    fun `get task by id OK`() = coroutinesRule.runBlockingTest {
        //Given
        val taskId = -1L

        //When
        coEvery { database.getTaskById(any()) } returns Task
        val value = dataSource.getTaskById(taskId)

        //Verify
        assertEquals(value, Task)
        coVerify { database.getTaskById(taskId) }
        confirmVerified(database)
    }

    @Test
    fun `delete task OK`() = coroutinesRule.runBlockingTest {
        //Given
        val taskList = listOf(Task).mapNotNull { it.id }

        //When
        coEvery { database.deleteTaskList(any()) } returns Unit

        val value = dataSource.deleteTasks(taskList)

        //Verify
        coVerify { database.deleteTaskList(taskList) }
        confirmVerified(database)
    }

    companion object {
        private val Task = TaskEntity(
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
