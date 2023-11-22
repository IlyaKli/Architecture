package com.yusmp.plan.presentation.common.extentions

import android.content.Context
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar
import com.google.android.material.textfield.TextInputLayout
import me.saket.bettermovementmethod.BetterLinkMovementMethod

fun View.showKeyboardCompat() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboardCompat() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun TextView.setDrawableStart(@DrawableRes drawable: Int) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        drawable,
        0,
        0,
        0
    )
}

fun TextInputLayout.changeErrorState(error: String?) {
    this.isErrorEnabled = !error.isNullOrBlank()
    this.error = error
}

fun TextView.setTextOrHide(value: String?) {
    isVisible = !value.isNullOrBlank()
    text = value
}

inline var ContentLoadingProgressBar.isLoading: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        if (value) show() else hide()
    }

/**
 * A class representing a clickable part of text.
 *
 * @property text The text to be made clickable.
 * @property clickListener A lambda function to be called when the clickable text is clicked.
 */
data class ClickablePart(
    @StringRes val textId: Int,
    val clickListener: () -> Unit
)


/**
 * Extension function to set parts of text as clickable.
 * @param completeTextId The complete text to be set on the `TextView`.
 * @param clickableParts A list of `ClickablePart` objects containing the text and the click listener.
 * @param highlightColor The color to be used for highlighting the clickable parts (optional).
 */
fun TextView.setClickableParts(
    @StringRes completeTextId: Int,
    clickableParts: List<ClickablePart>,
    @ColorRes highlightColor: Int? = null
) {
    setText(completeTextId)
    val spannableString = SpannableString(text)

    clickableParts.forEach { part ->
        val startIndex = text.indexOf(context.getString(part.textId))
        val endIndex = startIndex + context.getString(part.textId).length

        if (startIndex >= 0 && endIndex <= text.length) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    part.clickListener.invoke()
                }
            }

            spannableString.setSpan(
                /* what = */ clickableSpan,
                /* start = */ startIndex,
                /* end = */ endIndex,
                /* flags = */ Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            highlightColor?.also { colorId ->
                context.getColor(colorId).also {
                    spannableString.setSpan(
                        /* what = */ ForegroundColorSpan(it),
                        /* start = */ startIndex,
                        /* end = */ endIndex,
                        /* flags = */ Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
    }

    setText(spannableString, TextView.BufferType.SPANNABLE)
    movementMethod = BetterLinkMovementMethod.getInstance()
}

fun EditText.setMaxLength(maxLength: Int) {
    filters += arrayOf(InputFilter.LengthFilter(maxLength))
}