package com.pomodorotime.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.test.espresso.matcher.BoundedMatcher
import com.google.android.material.appbar.MaterialToolbar
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
            stringMatcher.describeTo(description)
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

fun withToolbarText(@StringRes resourceId: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {

        override fun matchesSafely(view: View): Boolean {

            return if (view is MaterialToolbar) {
                val title: CharSequence? = view.title
                val expectedText = view.resources.getString(resourceId)
                title != null && expectedText == title
            } else {
                false
            }
        }

        override fun describeTo(description: Description) {
            description.appendText("with string from resource id: ").appendValue(resourceId)
        }
    }
}

//https://gist.github.com/frankiesardo/7490059
fun withBackground(resourceId: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun matchesSafely(view: View): Boolean {
            return sameBitmap(view.context, view.background, resourceId)
        }

        override fun describeTo(description: Description) {
            description.appendText("has background resource $resourceId")
        }
    }
}

//https://gist.github.com/frankiesardo/7490059
fun withCompoundDrawable(resourceId: Int): Matcher<View> {
    return object : BoundedMatcher<View, TextView>(TextView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has compound drawable resource $resourceId")
        }

        override fun matchesSafely(textView: TextView): Boolean {
            for (drawable in textView.compoundDrawables) {
                if (sameBitmap(textView.context, drawable, resourceId)) {
                    return true
                }
            }
            return false
        }
    }
}

//https://gist.github.com/frankiesardo/7490059
fun withImageDrawable(resourceId: Int): Matcher<View> {
    return object : BoundedMatcher<View, ImageView>(ImageView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has image drawable resource $resourceId")
        }

        override fun matchesSafely(imageView: ImageView): Boolean {
            return sameBitmap(imageView.context, imageView.drawable, resourceId)
        }
    }
}

//https://medium.com/@dbottillo/android-ui-test-espresso-matcher-for-imageview-1a28c832626f#.4snjg8frw
private fun sameBitmap(
    context: Context, drawable: Drawable, resourceId: Int
): Boolean {
    val drawableA: Drawable = drawable
    val drawableB: Drawable? = ContextCompat.getDrawable(context, resourceId)

    val stateA = drawableA.constantState
    val stateB = drawableB?.constantState
    // If the constant state is identical, they are using the same drawable resource.
    // However, the opposite is not necessarily true.
    return (stateA != null && stateB != null && stateA == stateB
        || drawableA.getBitmap().sameAs(drawableB?.getBitmap()))
}

private fun Drawable.getBitmap(): Bitmap {
    val result: Bitmap

    if (this is BitmapDrawable) {
        result = this.bitmap
    } else {
        var width = this.intrinsicWidth
        var height = this.intrinsicHeight
        // Some drawables have no intrinsic width - e.g. solid colours.
        if (width <= 0) {
            width = 1
        }
        if (height <= 0) {
            height = 1
        }
        result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        this.setBounds(0, 0, canvas.width, canvas.height)
        this.draw(canvas)
    }
    return result
}
