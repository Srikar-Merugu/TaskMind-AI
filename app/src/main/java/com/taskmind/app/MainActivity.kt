package com.taskmind.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.taskmind.app.databinding.ActivityMainBinding
import com.taskmind.app.databinding.DialogAddTaskBinding
import com.taskmind.app.domain.model.Task
import com.taskmind.app.network.RetrofitClient
import com.taskmind.app.ui.LoginActivity
import com.taskmind.app.ui.TaskAdapter
import com.taskmind.app.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter
    private val auth = FirebaseAuth.getInstance()

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // Inform user that that your app will not show notifications.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)

        // Initial Load and Greeting Sync
        auth.currentUser?.uid?.let { uid ->
            viewModel.loadTasks(uid)
            viewModel.fetchUserName(uid)
            askNotificationPermission()
            testNetworkCall()
        } ?: run {
            goToLogin()
        }
        supportActionBar?.title = "TaskMind AI"

        setupRecyclerView()
        observeViewModel()

        binding.fabAddTask.setOnClickListener { view ->
            view.animate()
                .scaleX(0.8f).scaleY(0.8f)
                .setDuration(100)
                .withEndAction {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    showTaskDialog(null)
                }.start()
        }

        binding.fabAiChat.setOnClickListener { view ->
             view.animate()
                .scaleX(0.8f).scaleY(0.8f)
                .setDuration(100)
                .withEndAction {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    val intent = Intent(this, com.taskmind.app.ui.AIChatActivity::class.java)
                    val currentTasks = viewModel.tasks.value ?: emptyList()
                    intent.putParcelableArrayListExtra("TASKS_EXTRA", java.util.ArrayList(currentTasks))
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                }.start()
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Display an educational UI explaining to the user the features that will be enabled
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun testNetworkCall() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getSamplePost()
                if (response.isSuccessful && response.body() != null) {
                    val post = response.body()!!
                    Toast.makeText(this@MainActivity, "API Success: ${post.title}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "API Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "API Exception: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            onTaskChecked = { task, isCompleted ->
                auth.currentUser?.uid?.let { uid ->
                    viewModel.updateTaskStatus(uid, task, isCompleted)
                }
            },
            onTaskDeleteClicked = { task ->
                auth.currentUser?.uid?.let { uid ->
                    viewModel.deleteTask(uid, task.id)
                }
            },
            onTaskClicked = { task ->
                showTaskDialog(task)
            },
            onTaskAiClicked = { task ->
                val intent = Intent(this, com.taskmind.app.ui.AIChatActivity::class.java)
                val currentTasks = viewModel.tasks.value ?: emptyList()
                intent.putParcelableArrayListExtra("TASKS_EXTRA", java.util.ArrayList(currentTasks))
                intent.putExtra("TASK_ID", task.id)
                intent.putExtra("TASK_ACTION", "MENTOR_TASK")
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
        )

        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.tasks.observe(this) { tasks ->
            adapter.submitList(tasks)
            updateProgressRing(tasks)
        }

        viewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }

        viewModel.taskProgress.observe(this) { progress ->
            binding.progressRing.setMaxProgress(100f)
            binding.progressRing.setProgress(progress.percentage.toFloat(), true)
            binding.tvProgressText.text = "${progress.percentage}%"
        }

        viewModel.userName.observe(this) { name ->
            binding.tvDashboardTitle.text = "Hi, ${if (name.isNullOrEmpty()) "User" else name}!"
            binding.tvDashboardSubtitle.text = "Here is your progress for today."
        }
    }

    private fun updateProgressRing(tasks: List<Task>) {
        // Handled reactively by viewModel.taskProgress observer
    }

    private fun showTaskDialog(task: Task?) {
        val dialogBinding = DialogAddTaskBinding.inflate(LayoutInflater.from(this))
        
        task?.let {
            dialogBinding.tvDialogTitle.text = "Edit Task"
            dialogBinding.etTaskTitle.setText(it.title)
            dialogBinding.etTaskDescription.setText(it.description)
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val title = dialogBinding.etTaskTitle.text.toString().trim()
                val description = dialogBinding.etTaskDescription.text.toString().trim()

                if (title.isNotEmpty()) {
                    auth.currentUser?.uid?.let { uid ->
                        if (task == null) {
                            viewModel.addTask(uid, title, description)
                        } else {
                            viewModel.editTask(uid, task, title, description)
                        }
                    }
                } else {
                    Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNeutralButton("AI Assist") { _, _ ->
                 task?.let {
                    val intent = Intent(this, com.taskmind.app.ui.AIChatActivity::class.java)
                    val currentTasks = viewModel.tasks.value ?: emptyList()
                    intent.putParcelableArrayListExtra("TASKS_EXTRA", java.util.ArrayList(currentTasks))
                    intent.putExtra("TASK_ACTION", "SUGGEST_SUBTASKS")
                    intent.putExtra("TASK_ID", it.id)
                    startActivity(intent)
                 } ?: run {
                    Toast.makeText(this, "Save the task first to use AI assist", Toast.LENGTH_SHORT).show()
                 }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 1, 0, "Logout")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            auth.signOut()
            goToLogin()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
