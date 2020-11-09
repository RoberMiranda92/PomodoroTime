package com.pomodorotime.data.preferences

interface ISharedPreferences {

    fun putString(key: String, value: String)

    fun putBoolean(key: String, value: Boolean)

    fun putInt(key: String, value: Int)

    fun putFloat(key: String, value: Float)

    fun getString(key: String): String

    fun getBoolean(key: String): Boolean

    fun getInt(key: String): Int

    fun getFloat(key: String): Float
}