package com.pomodorotime.task

import com.pomodorotime.task.create.CreateTaskViewModel
import com.pomodorotime.task.taskList.TaskViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val taskModule = module {

    viewModel {
        TaskViewModel(get())
    }

    viewModel {
        CreateTaskViewModel(get())
    }
}