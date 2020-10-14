package com.pomodorotime.task

import com.pomodorotime.task.create.CreateTaskViewModel
import com.pomodorotime.task.tasklist.TaskViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val taskModule = module {

    viewModel {
        TaskViewModel(get(), get())
    }

    viewModel {
        CreateTaskViewModel(get())
    }
}