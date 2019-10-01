package com.brounie.mijuphotos.Auxiliaries

import android.content.Context
import android.widget.Toast

fun shortToast(str: String, context: Context) {
    Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
}

fun longToast(str: String, context: Context) {
    Toast.makeText(context, str, Toast.LENGTH_LONG).show()
}