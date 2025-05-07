package com.resigned.yellow.crane.tower.ce.dete

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.graphics.createBitmap
import com.resigned.yellow.crane.tower.databinding.ActivityDetailPhBinding
import com.resigned.yellow.crane.tower.utils.AppData
import java.io.File
import java.io.OutputStream

class DetailPhotoActivity : AppCompatActivity(), DetailPhotoAdapter.OnItemClickListener {
    private val binding by lazy { ActivityDetailPhBinding.inflate(layoutInflater) }
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerView()
        with(binding) {
            imgBack.setOnClickListener {
                finish()
            }
            tvCancel.setOnClickListener {
                llFinish.isVisible = false
                llSelect.isVisible = true
            }
            tvSelect.setOnClickListener {
                saveLayoutAsImage()
            }
            llSelect.setOnClickListener {
                openGallery()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 22334)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 22334 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                imageUri = uri
                startCountdown()
            }
        }
    }

    private fun saveLayoutAsImage() {
        if (!checkPermission()) {
            requestPermission()
            return
        }
        lifecycleScope.launch {
            downloadWallpaper()
        }
    }


    private fun downloadWallpaper() {
        // 获取需要保存的布局视图
        val view = binding.llFinish
        // 创建与布局尺寸相同的 Bitmap
        val bitmap = createBitmap(view.width, view.height).apply {
            val canvas = Canvas(this)
            view.draw(canvas)
        }
        val fileName = "wallpaper_${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + "TenWallPaper"
                )
            }
        }
        val resolver = contentResolver
        var imageUri: Uri? = null
        try {
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            imageUri?.let { uri ->
                val fos: OutputStream? = resolver.openOutputStream(uri)
                fos?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    binding.llDialogRe.isVisible = true
                    binding.tvCancel.isVisible = false
                    binding.tvSelect.isVisible = false
                    binding.llBom.isVisible = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to download Photo", Toast.LENGTH_SHORT).show()
            imageUri?.let { uri ->
                resolver.delete(uri, null, null)
            }
        }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0x234
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0x234) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadWallpaper()
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission denied")
            .setMessage("Storage permissions are required to save pictures. Please enable storage permissions in the settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun startCountdown() {
        // 获取传递的图片 URI
        if (imageUri != null) {
            binding.llSelect.isVisible = false
            binding.llFinish.isVisible = true
            lifecycleScope.launch(Dispatchers.IO) {
                val bitmap = getBitmapFromUri(imageUri!!)
                withContext(Dispatchers.Main) {
                    binding.imgDetail.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val adapter = DetailPhotoAdapter(AppData.allFramesData, this)
        binding.recyPhoto.apply {
            layoutManager =
                LinearLayoutManager(this@DetailPhotoActivity, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    private suspend fun getBitmapFromUri(uri: Uri): Bitmap {
        return withContext(Dispatchers.IO) {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
    }

    override fun onItemClick(position: Int) {
        with(binding) {
            if (position == 0) {
                imgPhoto.isVisible = false
                llBut.isVisible = false
            } else {
                imgPhoto.isVisible = true
                llBut.isVisible = true
                imgPhoto.setImageResource(AppData.allFramesData[position])
            }
        }

    }
}
