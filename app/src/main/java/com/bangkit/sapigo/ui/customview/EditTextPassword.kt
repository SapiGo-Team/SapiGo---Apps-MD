package com.bangkit.sapigo.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.bangkit.sapigo.R
import com.google.android.material.color.utilities.MaterialDynamicColors.background

class EditTextPassword : AppCompatEditText, View.OnTouchListener {

    // ...

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // ...

        // Set the background drawable
        background = ContextCompat.getDrawable(context, R.drawable.custom_form_input)

        // Set the text color
        setTextColor(ContextCompat.getColor(context, com.google.android.material.R.color.m3_ref_palette_black))

        // Set the hint text color
        setHintTextColor(ContextCompat.getColor(context, R.color.md_theme_light_onBackground))

        // ...
    }

    // ...

    override fun onDraw(canvas: Canvas) {
        // Remove the background and text color-related code from here
        // ...

        // Perform other drawing operations
        super.onDraw(canvas)
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        TODO("Not yet implemented")
    }

    // ...
}
