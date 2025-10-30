package com.kashifbhai.customcalendar

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewViewModel : ViewModel() {
    var number = MutableLiveData(0)
    fun addOne() {
        number.value = number.value!! +1
    }
    fun subOne()
    {
        if(number.value !!> 0)
        {
            number.value = number.value!! -1
        }
    }
}