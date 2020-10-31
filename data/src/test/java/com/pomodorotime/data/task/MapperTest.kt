package com.pomodorotime.data.task

import com.pomodorotime.data.task.api.models.ApiTask
import com.pomodorotime.data.task.dataBase.TaskEntity
import com.pomodorotime.domain.models.Task
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class MapperTest {

    @Test
    fun `from Task to ApiModel`() {
        val domainTask = Task
        val apiTask = domainTask.toApiTaskModel()

        apiTask.run {
            assertEquals(id, domainTask.id)
            assertEquals(name, domainTask.name)
            assertEquals(creationDate, domainTask.creationDate)
            assertEquals(donePomodoros, domainTask.donePomodoros)
            assertEquals(estimatedPomodoros, domainTask.estimatedPomodoros)
            assertEquals(shortBreaks, domainTask.shortBreaks)
            assertEquals(longBreaks, domainTask.longBreaks)
            assertEquals(completed, domainTask.completed)
        }
    }

    @Test
    fun `from Task to dataModel`() {
        val domainTask = Task
        val dataModel = domainTask.toDataModel()

        dataModel.run {
            assertEquals(id, domainTask.id)
            assertEquals(name, domainTask.name)
            assertEquals(creationDate, domainTask.creationDate)
            assertEquals(donePomodoros, domainTask.donePomodoros)
            assertEquals(estimatedPomodoros, domainTask.estimatedPomodoros)
            assertEquals(shortBreaks, domainTask.shortBreaks)
            assertEquals(longBreaks, domainTask.longBreaks)
            assertEquals(completed, domainTask.completed)
        }
    }

    @Test
    fun `from TaskModel to dataDomainModel`() {
        val dataModel = TaskModel
        val domainTask = dataModel.toDomainModel()

        domainTask.run {
            assertEquals(id, dataModel.id)
            assertEquals(name, dataModel.name)
            assertEquals(creationDate, dataModel.creationDate)
            assertEquals(donePomodoros, dataModel.donePomodoros)
            assertEquals(estimatedPomodoros, dataModel.estimatedPomodoros)
            assertEquals(shortBreaks, dataModel.shortBreaks)
            assertEquals(longBreaks, dataModel.longBreaks)
            assertEquals(completed, dataModel.completed)
        }
    }

    @Test
    fun `from ApiTask to dataDomainModel`() {
        val apiTask = ApiTask
        val domainTask = apiTask.toDomainModel()

        domainTask.run {
            assertEquals(id, apiTask.id)
            assertEquals(name, apiTask.name)
            assertEquals(creationDate, apiTask.creationDate)
            assertEquals(donePomodoros, apiTask.donePomodoros)
            assertEquals(estimatedPomodoros, apiTask.estimatedPomodoros)
            assertEquals(shortBreaks, apiTask.shortBreaks)
            assertEquals(longBreaks, apiTask.longBreaks)
            assertEquals(completed, apiTask.completed)
        }
    }

    companion object {
        private val Task = Task(
            id = 1,
            name = "Task1",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )

        private val TaskModel = TaskEntity(
            id = 1,
            name = "Task1",
            creationDate = Date(),
            donePomodoros = 0,
            estimatedPomodoros = 1,
            shortBreaks = 0,
            longBreaks = 0,
            completed = true
        )

        private val ApiTask = ApiTask(
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