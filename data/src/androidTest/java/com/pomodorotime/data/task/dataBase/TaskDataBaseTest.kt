package com.pomodorotime.data.task.dataBase

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.IOException
import java.util.Date
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDataBaseTest {
    private lateinit var taskDao: TaskDao
    private lateinit var db: TaskDataBase

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TaskDataBase::class.java
        ).build()
        taskDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeTaskAnReadInList() = coroutinesRule.runBlockingTest {
        val task = TaskEntity

        val taskId = taskDao.insert(task)
        task.apply {
            id = taskId
        }
        val byId = taskDao.getTaskById(taskId)
        assertEquals(byId, task)
    }

    @Test
    fun writeTaskAnDelete() = coroutinesRule.runBlockingTest {
        val task = TaskEntity

        val list = taskDao.run {
            val taskId = insert(task)
            task.apply {
                id = taskId
            }
            delete(task)
            getAllTask().first()
        }

        assert(list.isEmpty())
    }

    @Test
    fun writeTaskListAndRead() = coroutinesRule.runBlockingTest {
        val taskList = listOf(TaskEntity, TaskEntity2, TaskEntity3)

        val list = taskDao.run {
            insert(taskList)
            getAllTask().first()
        }
        assertEquals(taskList.size, list.size)
    }

    @Test
    fun writeTaskListAndDelete() = coroutinesRule.runBlockingTest {
        val taskList = listOf(TaskEntity, TaskEntity2, TaskEntity3)

        var list = taskDao.run {
            insert(taskList)
            getAllTask().first()
        }
        assertEquals(taskList.size, list.size)
        taskDao.deleteTaskList(list.mapNotNull { it.id })
        list = taskDao.getAllTask().first()

        assert(list.isEmpty())
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