package com.taskmind.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.taskmind.app.data.repository.TaskRepositoryImpl
import com.taskmind.app.domain.model.Task
import com.taskmind.app.domain.repository.TaskRepository

data class TaskProgress(val completed: Int, val total: Int, val percentage: Int)

class TaskViewModel : ViewModel() {

    private val repository: TaskRepository = TaskRepositoryImpl()

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    val taskProgress: LiveData<TaskProgress> = _tasks.map { list ->
        val tasks = list ?: emptyList()
        val total = tasks.size
        val completed = tasks.count { it.completed }
        // Use Math.round for accurate percentage
        val percentage = if (total > 0) Math.round((completed.toFloat() / total.toFloat()) * 100).toInt() else 0
        Log.d("TaskViewModel", "Progress Updated: Total=$total, Completed=$completed, Percentage=$percentage")
        TaskProgress(completed, total, percentage)
    }

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _userName = MutableLiveData<String>("User")
    val userName: LiveData<String> = _userName

    fun loadTasks(userId: String) {
        repository.getTasks(userId,
            onTasksLoaded = { taskList ->
                _tasks.value = taskList
            },
            onError = { e ->
                _error.value = e.message
            }
        )
    }

    // Repository has a listener that updates _tasks automatically in implementations, 
    // but just in case, we ensured reactive mapping above.

    fun addTask(userId: String, title: String, description: String) {
        val task = Task(title = title, description = description)
        repository.addTask(userId, task,
            onSuccess = { /* Automatically handled by value event listener in repo */ },
            onError = { e -> _error.value = e.message }
        )
    }

    fun updateTaskStatus(userId: String, task: Task, isCompleted: Boolean) {
        val updatedTask = task.copy(completed = isCompleted)
        repository.updateTask(userId, updatedTask,
            onSuccess = { /* Handle success if needed */ },
            onError = { e -> _error.value = e.message }
        )
    }

    fun editTask(userId: String, task: Task, title: String, description: String) {
        val updatedTask = task.copy(title = title, description = description)
        repository.updateTask(userId, updatedTask,
            onSuccess = { },
            onError = { e -> _error.value = e.message }
        )
    }

    fun deleteTask(userId: String, taskId: String) {
        repository.deleteTask(userId, taskId,
            onSuccess = { },
            onError = { e -> _error.value = e.message }
        )
    }

    fun fetchUserName(userId: String) {
        repository.getUserName(userId,
            onNameLoaded = { name -> _userName.value = name },
            onError = { e -> _error.value = e.message }
        )
    }
}
