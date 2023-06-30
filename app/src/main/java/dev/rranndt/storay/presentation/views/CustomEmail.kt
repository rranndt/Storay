package dev.rranndt.storay.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import dev.rranndt.storay.R

class CustomEmail : AppCompatEditText {

    private lateinit var editTextBackground: Drawable
    private lateinit var editTextErrorBackground: Drawable
    private var isError = false

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

        background = if (isError) editTextErrorBackground else editTextBackground
        addTextChangedListener(onTextChanged = { email, _, _, _ ->
            if (!isValidEmail(email)) {
                error = resources.getString(R.string.error_email)
                isError = true
            } else {
                error = null
                isError = false
            }
        })
    }

    private fun init() {
        editTextBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_edit_text_default) as Drawable
        editTextErrorBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_edit_text_error) as Drawable
    }

    fun isValidEmail(email: CharSequence?) =
        !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

}