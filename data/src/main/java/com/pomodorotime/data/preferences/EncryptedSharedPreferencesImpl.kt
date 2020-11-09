package com.pomodorotime.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class EncryptedSharedPreferencesImpl(context: Context) : ISharedPreferences {

    private val sharedPreferences: SharedPreferences

    init {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        sharedPreferences = EncryptedSharedPreferences
            .create(
                ENCRYPTED_SHARED_PREFERENCES_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    }

    override fun putString(key: String, value: String) {
        sharedPreferences.edit(true) {
            putString(key, value)
        }
    }

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit(true) {
            putBoolean(key, value)
        }
    }

    override fun putInt(key: String, value: Int) {
        sharedPreferences.edit(true) {
            putInt(key, value)
        }
    }

    override fun putFloat(key: String, value: Float) {
        sharedPreferences.edit(true) {
            putFloat(key, value)
        }
    }

    override fun getString(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }

    override fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    override fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, -1)
    }

    override fun getFloat(key: String): Float {
        return sharedPreferences.getFloat(key, -1F)
    }

    companion object {
        const val ENCRYPTED_SHARED_PREFERENCES_NAME = ".pomodoro_preferences_enc"
    }

}