package com.nutritrack

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

object AuthManager {
    private const val PREFS_NAME = "NutriTrackAuthPrefs"
    private const val KEY_USER_ID = "loggedInUserId"
    private const val KEY_USER_NAME = "loggedInUserName"
    private const val KEY_USER_PHONE = "loggedInUserPhone"

    private lateinit var sharedPreferences: SharedPreferences

    var currentUserId by mutableStateOf<String?>(null)
        private set

    var currentUserName by mutableStateOf<String?>(null)
        private set

    var currentUserPhone by mutableStateOf<String?>(null)
        private set

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getString(KEY_USER_ID, null)
        currentUserName = sharedPreferences.getString(KEY_USER_NAME, null)
        currentUserPhone = sharedPreferences.getString(KEY_USER_PHONE, null)
    }

    fun login(userId: String, userName: String?, phoneNumber: String?) {
        currentUserId = userId
        currentUserName = userName
        currentUserPhone = phoneNumber
        sharedPreferences.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, userName)
            .putString(KEY_USER_PHONE, phoneNumber)
            .apply()
    }

    fun logout() {
        currentUserId = null
        currentUserName = null
        currentUserPhone = null
        sharedPreferences.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_PHONE)
            .apply()
    }

    fun isLoggedIn(): Boolean = currentUserId != null
}

@Composable
fun CsvDataLoader(fileName: String = "CustomerData.csv"): List<UserData> {
    val context = LocalContext.current
    var userDataList by remember { mutableStateOf<List<UserData>>(emptyList()) }

    LaunchedEffect(fileName) {
        val lines = readCsvFromAssets(context, fileName)
        val data = parseCsv(lines)
        userDataList = data
    }
    return userDataList
}