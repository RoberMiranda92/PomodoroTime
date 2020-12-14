package com.pomodorotime.login.utils

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun withTextInputLayoutHint(@StringRes resourceId: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {

        override fun matchesSafely(view: View): Boolean {

            return if (view is TextInputLayout) {
                val hint: CharSequence? = view.hint
                val expectedText = view.resources.getString(resourceId)
                hint != null && expectedText == hint
            } else {
                false
            }
        }

        override fun describeTo(description: Description) {
            description.appendText("with string from resource id: ").appendValue(resourceId)
        }
    }
}

fun withTextInputLayoutHint(stringMatcher: Matcher<String>): Matcher<View> {
    return object : TypeSafeMatcher<View>() {

        override fun matchesSafely(view: View): Boolean {

            return if (view is TextInputLayout) {
                val hint: CharSequence? = view.hint
                hint != null && stringMatcher.matches(hint.toString())
            } else {
                false
            }
        }

        override fun describeTo(description: Description) {
            description.appendText("with hint: ")
            stringMatcher.describeTo(description);
        }
    }
}

fun withTextInputError(error: String): Matcher<View> {
    return object : TypeSafeMatcher<View>() {

        override fun matchesSafely(view: View): Boolean {

            return if (view is TextInputLayout) {
                error == view.error
            } else {
                false
            }
        }

        override fun describeTo(description: Description) {
            description.appendText("with error: $error")
        }
    }
}
