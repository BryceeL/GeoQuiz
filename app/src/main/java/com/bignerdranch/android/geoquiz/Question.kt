package com.bignerdranch.android.geoquiz

import android.util.Log
import androidx.annotation.StringRes

private const val TAG = "Question"

data class Question(@StringRes val textResId: Int, val answer: Boolean) {
    var isAnswered: Boolean = false
    var hasCheatedOnQuestion: Boolean = false
}


