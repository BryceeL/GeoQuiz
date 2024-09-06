package com.bignerdranch.android.geoquiz

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    //lateinit: data type that its not declared but will be used soon
    private lateinit var binding: ActivityMainBinding
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button

    //this initialize and, for every other new activity, returns a QuizViewModel instance in memory
    //Used by the new activity instance, QuizViewModel is destroyed when activity ends
    private val quizViewModel: QuizViewModel by viewModels()


    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        //Handles results based on result code sent from child activity (CheatActivity)
        val a = intent.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)
        Log.d(TAG, a.toString())
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.cheatedOnQuestion(result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) { //App is in memory but not visible
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate(Bundle) called")
        //refer to layout and inflate it
        binding = ActivityMainBinding.inflate(layoutInflater)
        //display UI
        setContentView(binding.root)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)

        binding.trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }

        binding.falseButton.setOnClickListener { view: View -> //listener
            checkAnswer(false)
        }

        binding.cheatButton.setOnClickListener {
            //Intent instance has ActivityManager start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNextQuestion()
            updateQuestion()
        }

        binding.previousButton.setOnClickListener {
            quizViewModel.moveToPreviousQuestion()
            updateQuestion()
        }
        updateQuestion()
    }

    //OTHER ACTIVITY STATE FUNCTIONS
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() { //App is visible, active in foreground
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() { //App is in background
        super.onPause()
        Log.d(TAG, "onPause() called")
    }
    override fun onStop() { //After onPause, App is not in memory, not visible
        super.onStop()
        Log.d(TAG, "onStop() called")
    }
    override fun onDestroy() {//Closing app destroys it
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    //BUTTON FUNCTIONS
    private fun enableAnswerButtons(enableState: Boolean) {
        trueButton.isEnabled = enableState
        falseButton.isEnabled = enableState
    }
    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
        enableAnswerButtons(!quizViewModel.wasCurrentQuestionAnswered)
    }
    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        //display toast if correct/incorrect/cheating
        val messageResId = when {
            quizViewModel.hasCheatedOnQuestion() -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(
                this,
                messageResId,
                Toast.LENGTH_SHORT
        ).show()

        //set question as answered and disable
        enableAnswerButtons(false)
        quizViewModel.answeredQuestion()
        if (userAnswer == correctAnswer) {
            quizViewModel.answeredQuestionCorrectly()
        }
        gradeQuiz()
    }
    private fun gradeQuiz() {
        val score: Float = quizViewModel.calculateQuizScore()
        //if score < 0, then quiz is not complete
        if (score >= 0) {
            Toast.makeText(
                this,
                "Your score: ${"%.0f".format(score)}%",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}