package com.bignerdranch.android.geoquiz

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

//TODO: create a ViewModel for CheatActivity to track user had cheated; still send RESULT_OK
//Idea: When showAnswerBtn is clicked in CheatActivity, it sets isCheater to true
//When user press back btn, setResult sends back this var
//When oriented, setResult never gets called, so find another way to setResult
class CheatViewModel (private val savedStateHandle: SavedStateHandle): ViewModel(){

    var isCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY) ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    fun isCheating(cheatBoolean: Boolean) {
        isCheater = cheatBoolean
    }

}