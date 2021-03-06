package com.pomodorotime.data.task.api

import com.google.firebase.database.DatabaseReference
import com.pomodorotime.data.TASK_MAIN_URL
import com.pomodorotime.data.task.api.models.ApiTask
import kotlinx.coroutines.tasks.await

class FirebaseTaskApi(private val database: DatabaseReference) : ITaskApi {

    override suspend fun getAllTask() {

    }

    override suspend fun insetTask(userId: String, task: ApiTask) {
        database.child(TASK_MAIN_URL)
            .child(userId)
            .child(task.id.toString())
            .setValue(task)
            .await()

    }

    override suspend fun updateTask(userId: String, task: ApiTask) {
        database.child(TASK_MAIN_URL)
            .child(userId)
            .child(task.id.toString())
            .setValue(task)
            .await()
    }

    override suspend fun deleteTask(userId: String, id: Long) {
        database.child(TASK_MAIN_URL)
            .child(userId)
            .child(id.toString())
            .removeValue()
            .await()
    }
}