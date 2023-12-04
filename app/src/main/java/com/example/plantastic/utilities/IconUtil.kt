package com.example.plantastic.utilities

import android.content.Context
import com.amulyakhare.textdrawable.TextDrawable
import com.example.plantastic.R

class IconUtil(private val context: Context) {

    val colorList = arrayListOf(
        context.resources.getColor(R.color.pastel_blue_dark),
        context.resources.getColor(R.color.pastel_red_var),
        context.resources.getColor(R.color.pastel_hot_pink),
        context.resources.getColor(R.color.pastel_green_var),
        context.resources.getColor(R.color.pastel_teal),
        context.resources.getColor(R.color.pastel_lime_green),
        context.resources.getColor(R.color.pastel_blue),
        context.resources.getColor(R.color.pastel_indigo),
        context.resources.getColor(R.color.pastel_lilac),
        context.resources.getColor(R.color.pastel_hot_pink_dark),
        context.resources.getColor(R.color.pastel_green_dark),
        context.resources.getColor(R.color.pastel_teal_dark),
        context.resources.getColor(R.color.pastel_lime_green_dark),
        context.resources.getColor(R.color.pastel_blue_dark),
        context.resources.getColor(R.color.pastel_indigo_dark),
        context.resources.getColor(R.color.pastel_purple_dark),
        context.resources.getColor(R.color.pastel_lilac_dark),
        context.resources.getColor(R.color.pastel_salmon),
        context.resources.getColor(R.color.pastel_mint),
        context.resources.getColor(R.color.pastel_coral),
        context.resources.getColor(R.color.pastel_mauve),
        context.resources.getColor(R.color.pastel_olive),
        context.resources.getColor(R.color.pastel_gray),
        context.resources.getColor(R.color.pastel_turquoise),
        context.resources.getColor(R.color.pastel_violet),
        context.resources.getColor(R.color.pastel_mint_dark),
        context.resources.getColor(R.color.pastel_coral_dark),
        context.resources.getColor(R.color.pastel_sky_blue_dark),
        context.resources.getColor(R.color.pastel_mauve_dark),
        context.resources.getColor(R.color.pastel_olive_dark),
        context.resources.getColor(R.color.pastel_gray_dark),
        context.resources.getColor(R.color.pastel_turquoise_dark),
        context.resources.getColor(R.color.pastel_violet_dark),


        context.resources.getColor(R.color.pastel_blue),
        context.resources.getColor(R.color.pastel_indigo),
        context.resources.getColor(R.color.pastel_mauve_dark),
        context.resources.getColor(R.color.pastel_olive_dark),
        context.resources.getColor(R.color.pastel_gray_dark),

    )

    fun getRandomColour(): Int {
        return (0 until colorList.size).random()
    }


    fun concatenateFirstLetters(firstString: String, secondString: String): String {

        // Get the first letter of each string
        val firstLetterFirstString = if (firstString.isNotEmpty()) firstString[0].toString() else ""
        val firstLetterSecondString =
            if (secondString.isNotEmpty()) secondString[0].toString() else ""

        return "$firstLetterFirstString$firstLetterSecondString"
    }

    fun getIcon(firstString: String, secondString: String, colourPos: Int): TextDrawable? {
        return TextDrawable.Builder()
            .setColor(colorList[colourPos])
            .setShape(TextDrawable.SHAPE_ROUND_RECT)
            .setText(concatenateFirstLetters(firstString, secondString))
            .setRadius(10000)
            .build()
    }

}