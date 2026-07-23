package com.esentiele.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "esentiele_user")

class UserPreferences(private val context: Context) {

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val PASSWORD_HASH = stringPreferencesKey("password_hash")
        val STYLE_PREFERENCES = stringPreferencesKey("style_preferences")
        val COLOR_SEASON = stringPreferencesKey("color_season")
        val BODY_TYPE = stringPreferencesKey("body_type")
        val MONTHLY_BUDGET = intPreferencesKey("monthly_budget")
        val PROFILE_INITIAL = stringPreferencesKey("profile_initial")

        fun hashPassword(password: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(password.toByteArray())
            return digest.joinToString("") { "%02x".format(it) }
        }
    }

    val isLoggedIn: Flow<Boolean> = context.userDataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val hasCompletedOnboarding: Flow<Boolean> = context.userDataStore.data.map { it[HAS_COMPLETED_ONBOARDING] ?: false }
    val userName: Flow<String> = context.userDataStore.data.map { it[USER_NAME] ?: "" }
    val userEmail: Flow<String> = context.userDataStore.data.map { it[USER_EMAIL] ?: "" }
    val stylePreferences: Flow<String> = context.userDataStore.data.map { it[STYLE_PREFERENCES] ?: "" }
    val colorSeason: Flow<String> = context.userDataStore.data.map { it[COLOR_SEASON] ?: "Warm Autumn" }
    val bodyType: Flow<String> = context.userDataStore.data.map { it[BODY_TYPE] ?: "" }
    val monthlyBudget: Flow<Int> = context.userDataStore.data.map { it[MONTHLY_BUDGET] ?: 5000 }
    val profileInitial: Flow<String> = context.userDataStore.data.map { it[PROFILE_INITIAL] ?: "E" }

    suspend fun signUp(name: String, email: String, password: String) {
        context.userDataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_EMAIL] = email
            prefs[PASSWORD_HASH] = hashPassword(password)
            prefs[PROFILE_INITIAL] = name.firstOrNull()?.uppercase() ?: "E"
            prefs[IS_LOGGED_IN] = true
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        var success = false
        context.userDataStore.data.collect { prefs ->
            val storedEmail = prefs[USER_EMAIL] ?: ""
            val storedHash = prefs[PASSWORD_HASH] ?: ""
            success = storedEmail.equals(email, ignoreCase = true) && storedHash == hashPassword(password)
            if (success) {
                context.userDataStore.edit { it[IS_LOGGED_IN] = true }
            }
            return@collect
        }
        return success
    }

    suspend fun completeOnboarding(styles: List<String>, season: String, bodyType: String, budget: Int) {
        context.userDataStore.edit { prefs ->
            prefs[HAS_COMPLETED_ONBOARDING] = true
            prefs[STYLE_PREFERENCES] = styles.joinToString(",")
            prefs[COLOR_SEASON] = season
            prefs[BODY_TYPE] = bodyType
            prefs[MONTHLY_BUDGET] = budget
        }
    }

    suspend fun logout() {
        context.userDataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = false
        }
    }

    suspend fun updateProfile(name: String? = null, season: String? = null) {
        context.userDataStore.edit { prefs ->
            name?.let {
                prefs[USER_NAME] = it
                prefs[PROFILE_INITIAL] = it.firstOrNull()?.uppercase() ?: "E"
            }
            season?.let { prefs[COLOR_SEASON] = it }
        }
    }
}
