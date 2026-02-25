package com.taskmind.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val completed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable
