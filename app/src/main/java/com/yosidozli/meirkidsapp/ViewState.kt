package com.yosidozli.meirkidsapp

sealed class ViewState {
    data class Success<T>(val content: T) : ViewState()
    data class Failure(val t: Throwable) : ViewState()
    object loading : ViewState()
}