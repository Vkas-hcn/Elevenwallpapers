package com.resigned.yellow.crane.tower.ce.dete

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.resigned.yellow.crane.tower.R
import com.resigned.yellow.crane.tower.databinding.ActivityDetailWallBinding

import android.Manifest
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import androidx.core.graphics.createBitmap

class DetailWallActivity : AppCompatActivity() {
    val binding by lazy { ActivityDetailWallBinding.inflate(layoutInflater) }
    private var imageRes: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        imageRes = intent.getIntExtra("imageRes", 0)
        findViewById<ImageView>(R.id.img_data).setImageResource(imageRes)
        clikFun()
    }

    private fun clikFun() {
        with(binding) {
            imgBack.setOnClickListener {
                finish()
            }

            btnDownload.setOnClickListener {
                checkAndRequestPermission()
            }
            btnApply.setOnClickListener {
                binding.clDialog.isVisible = true
            }
            tvCancel.setOnClickListener {
                binding.clDialog.isVisible = false
            }
            tvLock.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    setWallFun(
                        imageRes,
                        WallpaperManager.FLAG_LOCK,
                        this@DetailWallActivity,
                        {
                            binding.proLoad.isVisible = true
                        },
                        {
                            binding.clDialog.isVisible = false
                            binding.proLoad.isVisible = false
                        }
                    )
                }
            }
            tvHome.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    setWallFun(
                        imageRes,
                        WallpaperManager.FLAG_SYSTEM,
                        this@DetailWallActivity,
                        {
                            binding.proLoad.isVisible = true
                        },
                        {
                            binding.clDialog.isVisible = false
                            binding.proLoad.isVisible = false
                        }
                    )
                }
            }
            tvBoth.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    setWallBothFun(
                        imageRes,
                        this@DetailWallActivity,
                        {
                            binding.proLoad.isVisible = true
                        },
                        {
                            binding.clDialog.isVisible = false
                            binding.proLoad.isVisible = false
                        }
                    )
                }
            }
        }
    }

    private fun checkAndRequestPermission() {
        if (!checkPermission()) {
            requestPermission()
            return
        }
        saveImageToGallery()
    }

    private fun saveImageToGallery() {
        val bitmap = BitmapFactory.decodeResource(resources, imageRes)
        if (bitmap == null) {
            Toast.makeText(this, "Failed to decode image", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "wallpaper_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                try {
                    val outputStream: OutputStream? = contentResolver.openOutputStream(it)
                    outputStream?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val imageFile = File(picturesDirectory, "wallpaper_${System.currentTimeMillis()}.jpg")

            try {
                val outputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = Uri.fromFile(imageFile)
                sendBroadcast(mediaScanIntent)

                Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("You need to grant storage permission to save the image.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            false
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            0x778
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0x778) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToGallery()
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    private suspend fun setWallFun(
        img: Int,
        type: Int,
        context: Context,
        loadBefore: () -> Unit,
        loadAfter: () -> Unit
    ) {
        withContext(Dispatchers.Main) {
            loadBefore()
        }
        val wallpaperManager = WallpaperManager.getInstance(context)
        val bitmap = getBitmapFromVectorDrawable(context, img)
        try {
            wallpaperManager.setBitmap(
                bitmap,
                null,
                false,
                type
            )
            withContext(Dispatchers.Main) {
                loadAfter()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private suspend fun setWallBothFun(
        img: Int,
        context: Context,
        loadBefore: () -> Unit,
        loadAfter: () -> Unit
    ) {
        withContext(Dispatchers.Main) {
            loadBefore()
        }
        val wallpaperManager = WallpaperManager.getInstance(context)
        val bitmap = getBitmapFromVectorDrawable(context, img)
        try {
            wallpaperManager.setBitmap(
                bitmap,
                null,
                false,
                WallpaperManager.FLAG_SYSTEM
            )
            wallpaperManager.setBitmap(
                bitmap,
                null,
                false,
                WallpaperManager.FLAG_LOCK
            )
            withContext(Dispatchers.Main) {
                loadAfter()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        return when (val drawable = ContextCompat.getDrawable(context, drawableId)) {
            is BitmapDrawable -> {
                drawable.bitmap
            }

            is VectorDrawableCompat, is VectorDrawable -> {
                val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }

            else -> {
                throw IllegalArgumentException("unsupported drawable type")
            }
        }
    }
}

