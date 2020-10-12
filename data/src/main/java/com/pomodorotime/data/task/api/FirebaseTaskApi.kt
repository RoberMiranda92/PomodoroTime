package com.pomodorotime.data.task.api

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pomodorotime.data.task.api.models.ApiTask

class FirebaseTaskApi : ITaskApi {

    private var database: DatabaseReference = Firebase.database.reference

    override fun getAllTask() {
        TODO("Not yet implemented")
    }

    override fun insetTask(task: ApiTask) {
        database.child("task").setValue(task)
    }

    override fun updateTask(task: ApiTask) {
    }

    override fun deleteTask(id: Int) {
        //database.child("task").removeValue(id)
    }
}