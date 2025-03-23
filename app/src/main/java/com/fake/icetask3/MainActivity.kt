package com.fake.icetask3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fake.com.fake.icetask3.QuizeViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private val quizeViewModel: QuizeViewModel by viewModels()
    private lateinit var usernameInput: EditText
    private lateinit var categoryButtons: List<Button>
    private lateinit var questionText: TextView
    private lateinit var progressBar: SeekBar
    private lateinit var correctButton: Button
    private lateinit var wrongButton: Button
    private lateinit var timerText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        usernameInput = findViewById(R.id.usernameInput)
        categoryButtons = listOf(findViewById(R.id.scienceButton), findViewById(R.id.historyButton),
            findViewById(R.id.geographyButton), findViewById(R.id.mathButton))
        questionText = findViewById(R.id.questionText)
        progressBar = findViewById(R.id.progressBar)
        correctButton = findViewById(R.id.correctButton)
        wrongButton = findViewById(R.id.wrongButton)
        timerText = findViewById(R.id.timerText)

        categoryButtons.forEach { button ->
            button.setOnClickListener {
                quizeViewModel.startQuiz(button.text.toString(), usernameInput.text.toString())
            }
        }

        correctButton.setOnClickListener { quizeViewModel.answerQuestion(true) }
        wrongButton.setOnClickListener { quizeViewModel.answerQuestion(false) }

        quizeViewModel.currentQuestion.observe(this) { questionText.text = it }
        quizeViewModel.progress.observe(this) { progressBar.progress = it }
        quizeViewModel.timeElapsed.observe(this) {timerText.text = "Time: $it sec"}



        }
    }
