package com.pomodorotime.core

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout?.removeErrorOnTyping() {
    this?.editText?.doOnTextChanged { _, _, _, _ -> error = null }
}

fun Fragment.hideKeyboard() {
    view?.let { _view ->
        val imm: InputMethodManager? =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(_view.windowToken, 0)
    }
}

fun Fragment.showSnackBar(
    message: String,
    duration: Int,
    @ColorRes color: Int
) {
    view?.let {
        val snackBar = Snackbar.make(it, message, duration)
        context?.let {
            snackBar.view.setBackgroundColor(ContextCompat.getColor(it, color))
        }
        snackBar.show()
    }
}

fun Fragment.showSnackBarError(
    message: String,
    duration: Int
) {
    showSnackBar(message, duration, R.color.design_default_color_error)
}

fun Fragment.showDialog(
    title: String?,
    message: String,
    positiveText: String,
    negativeText: String,
    positiveClickAction: () -> Unit,
    negativeClickAction: () -> Unit?
) {
    val builder = MaterialAlertDialogBuilder(requireContext())
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(positiveText) { _, _ ->
        positiveClickAction.invoke()
    }
    builder.setNegativeButton(negativeText) { _, _ ->
        negativeClickAction?.invoke()
    }
    builder.show()
}

private fun showBackDialog() {

}
