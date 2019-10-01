package com.brounie.mijuphotos.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.brounie.mijuphotos.Auxiliaries.shortToast
import com.brounie.mijuphotos.R
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ByteOrderMark")
class AddPhoto : AppCompatActivity() {

    // Global variables
    private val PERMISSION_REQUEST = 100
    private var email: String? = ""
    private var mPhotoFileIUri: Uri = Uri.EMPTY
    val RESULT_CODE_REQUEST_TAKE_PHOTO = 300
    private var pinCode: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        // get email
        var mail_checking: String?

        var pinFound = false
        val mailQuery = ParseQuery.getQuery<ParseObject>("PhotosEvent")
        mailQuery.orderByAscending("email")
        email = intent.getStringExtra("email")

        mailQuery.whereEqualTo("email", email)
        mailQuery.getFirstInBackground { obj, e ->
            if (e == null) {

                if (obj != null) {
                    pinCode = obj.objectId
                    pinFound = true
                }

            } else {
                if (e.message == "no results found for query") {
                    val new_pObject = ParseObject("PhotosEvent")
                    new_pObject.put("email", email!!)
                    new_pObject.put("email_send", false)
                    new_pObject.saveInBackground().onSuccess {
                        pinCode = new_pObject.objectId
                    }
                } else {
                    Log.d("MAIL", e.message)
                }
            }
        }

        addPhoto_btn.setOnClickListener {
            dispatchTakePictureIntent()
        }

        end_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


    private fun dispatchTakePictureIntent() {
        if (!cameraPermissionIsGranted()) {
            requestCameraPermission()
            return
        }

        if (!writeStoragePermissionIsGranted()) {
            requestStorageWritePermission()
            return
        }

        if (!readStoragePermissionIsGranted()) {
            requestStorageReadPermission()
            return
        }

        val capturePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Create the File where the photo should go
        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
            Log.e("DEBUG","Error occurred while creating the File")
        }

        // Continue only if the File was successfully created
        if (photoFile != null) {
            mPhotoFileIUri = Uri.parse(photoFile.absolutePath)
            val photoURI = FileProvider.getUriForFile(this, "com.brounie.mijuphotos.fileprovider", photoFile)
            capturePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            startActivityForResult(capturePictureIntent, RESULT_CODE_REQUEST_TAKE_PHOTO)
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        return image
    }

    private fun cameraPermissionIsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun writeStoragePermissionIsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun readStoragePermissionIsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), PERMISSION_REQUEST)
    }

    private fun requestStorageWritePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST)
    }

    private fun requestStorageReadPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == RESULT_CODE_REQUEST_TAKE_PHOTO) {

            val intent = Intent(this, PicturePreview::class.java)
            intent.putExtra("imgUri", mPhotoFileIUri.path)
            intent.putExtra("email", email)
            intent.putExtra("pinCode", pinCode)

            startActivity(intent)

        }

    }

}