package com.example.flight_tracker.network

/**
 * @author by Idricealy on 08/11/2023
 */
sealed interface RequestListener<out R> {
    data class Success<out T>(val data: String) : RequestListener<T>
    data class Loading<out T>(val loadMessage: String) : RequestListener<T>
    data class Failed<out T>(val message: String) : RequestListener<T>
    data class Error(val exception: Exception) : RequestListener<Nothing>
}