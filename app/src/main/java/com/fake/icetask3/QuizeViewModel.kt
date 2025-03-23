package com.fake.com.fake.icetask3

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

class QuizeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val categories = mapOf(
        "Science" to listOf("What is the chemical symbol for water?", "What planet is known as the Red Planet?", "Who developed the theory of relativity?", "What gas do plants absorb from the atmosphere?", "What is the powerhouse of the cell?", "Which element has the atomic number 1?", "What is the speed of light in vacuum?", "Which part of the brain controls balance?", "What is the largest organ in the human body?", "Who invented the telephone?"),
        "History" to listOf("Who was the first president of the United States?", "What year did World War II end?", "Who was known as the Iron Lady?", "Which civilization built the pyramids of Giza?", "Who discovered America?", "When was the Declaration of Independence signed?", "Who was the last Tsar of Russia?", "Which country was the first to land on the moon?", "What event started World War I?", "Who was the first emperor of China?"),
        "Geography" to listOf("What is the capital of France?", "Which is the largest ocean on Earth?", "Which country has the most population?", "Which desert is the largest in the world?", "What is the longest river in the world?", "Which continent is the smallest by land area?", "What is the capital of Japan?", "Which country is known as the Land of the Rising Sun?", "Where are the Great Pyramids located?", "What is the national animal of Canada?"),
        "Math" to listOf("What is 5 + 3 * 2?", "What is the square root of 144?", "What is the value of Pi to two decimal places?", "What is 12% of 150?", "What is the sum of interior angles of a triangle?", "What is 7 factorial?", "If x + 3 = 10, what is x?", "What is the area of a circle with radius 4?", "What is the Pythagorean theorem?", "What is the derivative of x²?")
    )

    private var username = ""
    private var category = ""
    private var score = 0
    private var startTime = 0L

    private val _currentQuestion = MutableLiveData<String>()
    val currentQuestion: LiveData<String> get() = _currentQuestion

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> get() = _progress

    private val _timeElapsed = MutableLiveData<Int>()
    val timeElapsed: LiveData<Int> get() = _timeElapsed

    private var questionIndex = 0
    private var quizJob: Job? = null

    fun startQuiz(selectedCategory: String, user: String) {
        username = user
        category = selectedCategory
        questionIndex = 0
        score = 0
        startTime = System.currentTimeMillis()

        nextQuestion()

        // Start a coroutine to track time
        quizJob?.cancel()
        quizJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                val elapsedTime = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                _timeElapsed.postValue(elapsedTime)
                delay(1000)
            }
        }
    }

    fun answerQuestion(correct: Boolean) {
        if (correct) score += 10

        if (questionIndex + 1 < categories[category]?.size ?: 0) {
            questionIndex++
            nextQuestion()
        } else {
            saveScore()
            quizJob?.cancel() // Stop the timer when the quiz ends
        }
    }

    private fun nextQuestion() {
        categories[category]?.getOrNull(questionIndex)?.let {
            _currentQuestion.postValue(it)
            _progress.postValue(((questionIndex + 1) / 10.0 * 100).toInt())
        }
    }

    private fun saveScore() {
        val elapsedTime = ((System.currentTimeMillis() - startTime) / 1000).toInt()
        db.collection("quizScores").add(
            hashMapOf(
                "username" to username,
                "category" to category,
                "score" to score,
                "time" to elapsedTime
            )
        )
    }
}