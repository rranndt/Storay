package dev.rranndt.storay.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import dev.rranndt.storay.R

class CustomPassword : AppCompatEditText, View.OnTouchListener {

    private lateinit var editTextBackground: Drawable
    private lateinit var editTextErrorBackground: Drawable
    private lateinit var startIconDrawable: Drawable
    private lateinit var showPasswordIconDrawable: Drawable

    private var isError = false
    private var isPasswordVisible = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        setButtonDrawables(
            start = startIconDrawable,
        )
        background = if (isError) editTextErrorBackground else editTextBackground
    }

    private fun init() {
        startIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_password) as Drawable
        showPasswordIconDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_toggle_show) as Drawable

        editTextBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_edit_text_default) as Drawable
        editTextErrorBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_edit_text_error) as Drawable

        addTextChangedListener(onTextChanged = { password, _, _, _ ->
            if (password.toString().isNotEmpty()) showToggleButton() else hideToggleButton()
            if (!isValidPassword(password)) {
                error = resources.getString(R.string.error_password)
                isError = true
            } else {
                error = null
                isError = false
            }
        })

        setOnTouchListener(this)
    }

    private fun setButtonDrawables(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null,
    ) {
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
        compoundDrawablePadding = 32
    }

    private fun showToggleButton() {
        setButtonDrawables(
            start = startIconDrawable,
            end = showPasswordIconDrawable
        )
    }

    private fun hideToggleButton() {
        setButtonDrawables(start = startIconDrawable)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val toggleButtonStart: Float
            val toggleButtonEnd: Float
            var isToggleButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                toggleButtonEnd =
                    (showPasswordIconDrawable.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < toggleButtonEnd -> isToggleButtonClicked = true
                }
            } else {
                toggleButtonStart =
                    (width - paddingEnd - showPasswordIconDrawable.intrinsicWidth).toFloat()
                when {
                    event.x > toggleButtonStart -> isToggleButtonClicked = true
                }
            }

            return if (isToggleButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        isPasswordVisible = !isPasswordVisible
                        passwordVisibility()
                        true
                    }

                    MotionEvent.ACTION_DOWN -> {
                        isPasswordVisible = !isPasswordVisible
                        passwordVisibility()
                        true
                    }

                    else -> false
                }
            } else false
        }
        return false
    }

    private fun passwordVisibility() {
        inputType =
            if (isPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        setSelection(text?.length ?: 0)
    }

    fun isValidPassword(password: CharSequence?) =
        !password.isNullOrEmpty() && password.length >= 8

}