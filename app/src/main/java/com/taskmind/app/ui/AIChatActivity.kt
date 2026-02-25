package com.taskmind.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.taskmind.app.BuildConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.taskmind.app.databinding.ActivityAichatBinding
import com.taskmind.app.viewmodel.AIChatViewModel

class AIChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAichatBinding
    private val viewModel: AIChatViewModel by viewModels()
    private lateinit var adapter: AIChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAichatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        checkApiKey()

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        setupRecyclerView()
        observeViewModel()

        val tasks = intent.getParcelableArrayListExtra<com.taskmind.app.domain.model.Task>("TASKS_EXTRA")
        if (tasks != null) {
            viewModel.setTaskContext(tasks)
            val action = intent.getStringExtra("TASK_ACTION")
            val taskId = intent.getStringExtra("TASK_ID")
            
            if (action == "SUGGEST_SUBTASKS" && taskId != null) {
                val targetTask = tasks.find { it.id == taskId }
                targetTask?.let { viewModel.suggestSubtasks(it) }
            } else if (action == "REWRITE_TASK" && taskId != null) {
                val targetTask = tasks.find { it.id == taskId }
                targetTask?.let { viewModel.rewriteTask(it) }
            } else if (action == "MENTOR_TASK" && taskId != null) {
                val targetTask = tasks.find { it.id == taskId }
                targetTask?.let { viewModel.mentorTask(it) }
            }
        }

        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.etMessage.text.clear()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = AIChatAdapter()
        binding.rvChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.rvChat.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.chatMessages.observe(this) { messages ->
            adapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.rvChat.smoothScrollToPosition(messages.size - 1)
            }
        }

        viewModel.error.observe(this) { errorMsg ->
            com.google.android.material.snackbar.Snackbar.make(binding.root, errorMsg, com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show()
        }
    }

    private fun checkApiKey() {
        if (BuildConfig.GEMINI_API_KEY == "YOUR_GEMINI_API_KEY_HERE") {
            MaterialAlertDialogBuilder(this)
                .setTitle("API Key Required")
                .setMessage("Gemini AI features require an API key. Please add 'gemini.api.key=your_key' to your local.properties file and sync.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}
