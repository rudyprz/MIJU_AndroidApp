package com.brounie.mijuphotos.Activities

import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.print.PrintHelper
import com.brounie.mijuphotos.Auxiliaries.longToast
import com.brounie.mijuphotos.Auxiliaries.shortToast
import com.brounie.mijuphotos.R
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.android.synthetic.main.activity_picture_preview.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class PicturePreview : AppCompatActivity() {

    var email: String? = ""
    var imgUri: Uri? = null

    var bitmap: Bitmap? = null

    var pinCode = "no pin code"
    var pinFound = false
    var fileUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_preview)


        // Initialize variables
        email = intent.getStringExtra("email")
        pinCode = intent.getStringExtra("pinCode")
        val filepath = intent.getStringExtra("imgUri")

        if (filepath != null) {
            screenshot(filepath)
        } else {
            longToast("NO FILEPATH", applicationContext)
        }

        prev_end_btn.setOnClickListener {
            val intent = Intent(this, AddPhoto::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        prev_next_btn.setOnClickListener{
            //doPhotoPrint(bitmap!!)
            val intent = Intent(this, PrintLayout::class.java)
            intent.putExtra("pinCode", pinCode)
            intent.putExtra("email", email)
            startActivity(intent)
        }

    }

    fun saveParseFile(parseFile: ParseFile, email: String) {
        longToast("Saving File", applicationContext)


        val emailToSave = ParseObject("PhotosEvent")
        emailToSave!!.put("email", email)
        emailToSave!!.put("image1", parseFile)
        emailToSave!!.put("hidden", false)
        emailToSave!!.put("email_send", true)
        emailToSave!!.saveInBackground()

    }

    private fun screenshot(filepath: String) {
        longToast("TAKING SCREENSHOT", applicationContext)

        total_frame.post(object : Runnable {
            override fun run() {
                // get images as bitmap
                var frameBitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.marcofinal)
                var photoBitmap : Bitmap? = getBitmapFromFile(filepath)
                var qrBitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.frame)

                // scale images
                val scale_hconstant = 300
                val scale_vconstant = 370
                val img_hconst = 50
                val img_vconst = 140
                frameBitmap = Bitmap.createScaledBitmap(frameBitmap, total_frame.width-scale_hconstant,
                    total_frame.height-scale_vconstant, false)

                // For tablet
                longToast("Drawing image and frame", applicationContext)
                val matrix = Matrix()
                matrix.postRotate(90F)
                photoBitmap = Bitmap.createBitmap(photoBitmap!!, 0, 0, photoBitmap.width, photoBitmap.height, matrix, true)
                photoBitmap = Bitmap.createScaledBitmap(photoBitmap!!, total_frame.width-scale_hconstant-img_hconst,
                    total_frame.height-scale_vconstant-img_vconst, false)
                qrBitmap = Bitmap.createScaledBitmap(qrBitmap, prev_frame.width/6, prev_frame.width/6, false)


                var totalBitmap: Bitmap = Bitmap.createBitmap(total_frame.width, total_frame.height, Bitmap.Config.ARGB_8888)
                var canvas = Canvas(totalBitmap)

                longToast("Painting", applicationContext)
                val paint = Paint()
                paint.isAntiAlias = true
                paint.isFilterBitmap = true
                paint.isDither = true

                canvas.drawBitmap(photoBitmap, 20F, 75F, paint)
                canvas.drawBitmap(frameBitmap, 0F, 0F, paint)

                // test
                prev_frame.visibility = View.GONE
                prev_picture.setImageBitmap(totalBitmap)

                // Upload to parse
                val currentDate = SimpleDateFormat("dd_M_yyyyhh_mm_ss").format(Date())
                val path = Environment.getExternalStorageDirectory().path
                val file = File(path, "${currentDate}_wframe.png")

                try {

                    longToast("TOAST", applicationContext)
                    val stream: OutputStream = FileOutputStream(file)
                    totalBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
                    stream.flush()
                    stream.close()

                    longToast("Stream closed", applicationContext)

                    val pFile = ParseFile(file)
                    fileUri = Uri.fromFile(file)
                    saveParseFile(pFile, email!!)

                    if (fileUri != null) {
                        // PRINT IMAGE WITH QR CODE AN PIN
                        longToast("DRAWING QR & PIN", applicationContext)
                        // add pin
                        paint.textSize = 35F
                        canvas.drawText("Pin Code: $pinCode", 200F, 1400F, paint)
                        // add qr
                        canvas.drawBitmap(qrBitmap, 0F, 1320F, paint)
                        // test
                        prev_frame.visibility = View.GONE
                        prev_picture.setImageBitmap(totalBitmap)
                        bitmap = totalBitmap
                    } else {
                        longToast("NO FILE URI", applicationContext)
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    longToast(e.message!!, applicationContext)
                }

            }

        })
    }

    private fun getBitmapFromFile(path: String): Bitmap? {
        val imgFile = File(path)
        var finalBitmap : Bitmap? = null
        val bOptions = BitmapFactory.Options()
        bOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
        try {
            finalBitmap = BitmapFactory.decodeStream(FileInputStream(imgFile), null, bOptions)
        } catch (e: FileNotFoundException) {
            Log.d("getBitmapFromFile ex", e.message)
        }
        return finalBitmap
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