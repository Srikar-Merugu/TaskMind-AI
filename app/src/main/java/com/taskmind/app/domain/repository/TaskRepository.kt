package com.taskmind.app.domain.repository

import com.taskmind.app.domain.model.Task

interface TaskRepository {
    fun getTasks(userId: String, onTasksLoaded: (List<Task>) -> Unit, onError: (Exception) -> Unit)
    fun addTask(userId: String, task: Task, onSuccess: () -> Unit, onError: (Exception) -> Unit)
    fun updateTask(userId: String, task: Task, onSuccess: () -> Unit, onError: (Exception) -> Unit)
    fun deleteTask(userId: String, taskId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit)
    fun getUserName(userId: String, onNameLoaded: (String) -> Unit, onError: (Exception) -> Unit)
}
