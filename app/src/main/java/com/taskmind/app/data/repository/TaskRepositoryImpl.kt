package com.taskmind.app.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.taskmind.app.domain.model.Task
import com.taskmind.app.domain.repository.TaskRepository

class TaskRepositoryImpl : TaskRepository {

    private val database = FirebaseDatabase.getInstance().reference

    override fun getTasks(
        userId: String,
        onTasksLoaded: (List<Task>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        database.child("users").child(userId).child("tasks")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val taskList = mutableListOf<Task>()
                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        if (task != null) {
                            taskList.add(task)
                        }
                    }
                    onTasksLoaded(taskList)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    override fun addTask(
        userId: String,
        task: Task,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val taskId = database.child("users").child(userId).child("tasks").push().key
        if (taskId != null) {
            val taskWithId = task.copy(id = taskId)
            database.child("users").child(userId).child("tasks").child(taskId)
                .setValue(taskWithId)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
        } else {
            onError(Exception("Failed to generate task ID"))
        }
    }

    override fun updateTask(
        userId: String,
        task: Task,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        database.child("users").child(userId).child("tasks").child(task.id)
            .setValue(task)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun deleteTask(
        userId: String,
        taskId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        database.child("users").child(userId).child("tasks").child(taskId)
            .removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun getUserName(
        userId: String,
        onNameLoaded: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        database.child("users").child(userId).child("name")
            .addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val name = snapshot.getValue(String::class.java) ?: ""
                    onNameLoaded(name)
                }

                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    onError(error.toException())
                }
            })
    }
}
