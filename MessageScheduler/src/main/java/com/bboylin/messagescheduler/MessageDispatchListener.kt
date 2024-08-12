package com.bboylin.messagescheduler

interface MessageDispatchListener {
    fun onDispatchBegin(log:String) {}
    fun onDispatchEnd(log:String) {}
}