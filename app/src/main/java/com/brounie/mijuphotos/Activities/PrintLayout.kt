package com.brounie.mijuphotos.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.print.PrintHelper
import com.brounie.mijuphotos.R
import kotlinx.android.synthetic.main.activity_print_layout.*

class PrintLayout : AppCompatActivity() {

    var pinCode : String? = null
    var totalBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print_layout)

        val mail = intent.getStringExtra("email")
        pinCode = intent.getStringExtra("pinCode")
        if (pinCode != null) pincode_text.text = pinCode

        final_print_btn.setOnClickListener {
            //totalBitmap = screenshot(printable_sheet)
            //doPhotoPrint(totalBitmap!!)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        final_print_add_photo.setOnClickListener {
            val intent = Intent(this, AddPhoto::class.java)
            intent.putExtra("email", mail)
            startActivity(intent)
        }
    }

    private fun screenshot(v: View) : Bitmap {
        var rBitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(rBitmap)
        canvas.drawColor(Color.WHITE)
        v.draw(canvas)

        return rBitmap
    }

    private fun doPhotoPrint(bitmap: Bitmap) {
        this?.also { context ->
            PrintHelper(context).apply {
                scaleMode = PrintHelper.SCALE_MODE_FIT
            }.also { printHelper ->
                printHelper.printBitmap("miju", bitmap)
            }
        }
    }
}
