package com.pomodorotime.data.task.api

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pomodorotime.data.TASK_MAIN_URL
import com.pomodorotime.data.task.api.models.ApiTask
import kotlinx.coroutines.tasks.await

class FirebaseTaskApi : ITaskApi {

    private var database: DatabaseReference = Firebase.database.reference

    override suspend fun getAllTask() {
        TODO("Not yet implemented")
    }

    override suspend fun insetTask(userId: String, task: ApiTask) {
        database.child(TASK_MAIN_URL)
            .child(userId)
            .child(task.id.toString())
            .setValue(task).await()

    }

    override suspend fun updateTask(userId: String, task: ApiTask) {
//        database.child(TASK_MAIN_URL)
//            .child(userId)
//            .child(task.id.toString())
//            .updateChildren(task).await()
    }

    override suspend fun deleteTask(userId: String, id: Long) {
        database.child(TASK_MAIN_URL).child(userId).child(id.toString()).removeValue()
    }
}