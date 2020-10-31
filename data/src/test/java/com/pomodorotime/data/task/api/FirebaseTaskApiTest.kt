package com.pomodorotime.data.task.api

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.pomodorotime.data.CoroutinesRule
import com.pomodorotime.data.TASK_MAIN_URL
import com.pomodorotime.data.task.api.models.ApiTask
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

class FirebaseTaskApiTest {

    @get:Rule
    val coroutinesRule = CoroutinesRule()

    @MockK
    lateinit var firebaseDatabase: DatabaseReference

    @RelaxedMockK
    lateinit var taskUnit: Task<Void>

    lateinit var api: FirebaseTaskApi

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        api = FirebaseTaskApi(firebaseDatabase)
    }

    @Test
    fun `insert task is Ok`() = runBlockingTest {
        //Given
        val userId = "userID"
        val task = Task1

        //When
        coEvery { firebaseDatabase.child(any()) } returns firebaseDatabase
        coEvery { firebaseDatabase.setValue(any()) } returns taskUnit
        coEvery { taskUnit.isComplete } returns true
        coEvery { taskUnit.exception } returns null
        coEvery { taskUnit.isCanceled } returns false

        api.insetTask(userId, task)

        //Verify
        verify { firebaseDatabase.child(TASK_MAIN_URL) }
        verify { firebaseDatabase.child(userId) }
        verify { firebaseDatabase.child(task.id.toString()) }
        verify { firebaseDatabase.setValue(task) }

        confirmVerified(firebaseDatabase)
    }

    @Test
    fun `update task is Ok`() = runBlockingTest {
        //Given
        val userId = "userID"
        val task = Task1

        //When
        coEvery { firebaseDatabase.child(any()) } returns firebaseDatabase
        coEvery { firebaseDatabase.setValue(any()) } returns taskUnit
        coEvery { taskUnit.isComplete } returns true
        coEvery { taskUnit.exception } returns null
        coEvery { taskUnit.isCanceled } returns false

        api.updateTask(userId, task)

        //Verify
        verify { firebaseDatabase.child(TASK_MAIN_URL) }
        verify { firebaseDatabase.child(userId) }
        verify { firebaseDatabase.child(task.id.toString()) }
        verify { firebaseDatabase.setValue(task) }

        confirmVerified(firebaseDatabase)
    }

    @Test
    fun `delete task is Ok`() = runBlockingTest {
        //Given
        val userId = "userID"
        val taskId = Task1.id

        //When
        coEvery { firebaseDatabase.child(any()) } returns firebaseDatabase
        coEvery { firebaseDatabase.removeValue() } returns taskUnit
        coEvery { taskUnit.isComplete } returns true
        coEvery { taskUnit.exception } returns null
        coEvery { taskUnit.isCanceled } returns false

        api.deleteTask(userId, taskId)

        //Verify
        verify { firebaseDatabase.child(TASK_MAIN_URL) }
        verify { firebaseDatabase.child(userId) }
        verify { firebaseDatabase.child(taskId.toString()) }
        verify { firebaseDatabase.removeValue() }

        confirmVerified(firebaseDatabase)
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