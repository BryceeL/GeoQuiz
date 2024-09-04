package com.bignerdranch.android.geoquiz

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"

//QuizViewModel's data persist even after orientation rotation
//og MainActivity would use QuizViewModel to change its data
//have the new MainActivity (from rotation) refer to QuizViewModel's data
class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    //call constructors and create question list
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_oceans, true),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true))

    var isCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY) ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)
    //get/set value of currentIndex from savedStateHandle via CURRENT_INDEX_KEY (the key value)
    //if there's none, set with default val of 0
    private var currentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)
    private var correctAnswers = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId
    val wasCurrentQuestionAnswered: Boolean
        get() = questionBank[currentIndex].isAnswered

    //MAIN ACTIVITY FUNCTIONS
    fun moveToNextQuestion() {
        currentIndex = (currentIndex + 1) % questionBank.size
        //Log.d(TAG,(currentIndex+1).toString() + " " + questionBank.size.toString())
    }
    fun moveToPreviousQuestion() {
        if ((currentIndex - 1) % questionBank.size < 0) {
            currentIndex = questionBank.size - 1
        } else {
            currentIndex = (currentIndex - 1) % questionBank.size
        }
        //Log.d(TAG,(currentIndex+1).toString() + " " + questionBank.size.toString())
    }
    fun answeredQuestion() {
        questionBank[currentIndex].isAnswered = true
    }
    fun answeredQuestionCorrectly() {
        correctAnswers++
    }
    fun calculateQuizScore() : Float {
        var quizIsComplete = true
        for (question in questionBank) {
            if (!question.isAnswered) {
                quizIsComplete = false
                break
            }
        }
        return if (quizIsComplete) {
            correctAnswers.toFloat() / (questionBank.size) * 100
        } else {
            (-1).toFloat()
        }
    }
    fun cheatedOnQuestion(cheatedBoolean: Boolean) {
        questionBank[currentIndex].hasCheatedOnQuestion = cheatedBoolean
    }

    fun hasCheatedOnQuestion(): Boolean {
        return questionBank[currentIndex].hasCheatedOnQuestion
    }
}