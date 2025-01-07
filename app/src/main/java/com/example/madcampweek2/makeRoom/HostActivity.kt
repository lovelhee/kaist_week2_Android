package com.example.madcampweek2.makeRoom

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.madcampweek2.R
import com.example.madcampweek2.home.HomeActivity

class HostActivity : AppCompatActivity() {

    private lateinit var btnCamera: Button
    private lateinit var btnUpload: Button
    private lateinit var ivReceipt: ImageView
    private var imageUrl: String? = null // 서버로 전송할 이미지 URL

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        val ivBack: ImageView = findViewById(R.id.ivback)
        ivBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val etInvite: EditText = findViewById(R.id.etInvite)
        val btnInvite: Button = findViewById(R.id.btnInvite)
        val invitedFriendsLayout: LinearLayout = findViewById(R.id.invitedFriendsLayout)

        btnInvite.setOnClickListener {
            val friendName = etInvite.text.toString()

            if (friendName.isNotBlank()) {

                val textView = TextView(this).apply {
                    text = friendName
                    textSize = 16f
                    setTextColor(resources.getColor(android.R.color.black, null))
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 10, 0, 10)
                    }
                    gravity = Gravity.START
                }

                invitedFriendsLayout.addView(textView)

                etInvite.text.clear()
            }
        }

        ivReceipt = findViewById(R.id.ivReceipt)
        btnCamera = findViewById(R.id.btnCamera)
        btnUpload = findViewById(R.id.btnUpload)

        btnCamera.setOnClickListener {
            showImageSelectionDialog()
        }
    }

    private fun showImageSelectionDialog() {
        val options = arrayOf("사진 촬영", "갤러리 선택")
        AlertDialog.Builder(this)
            .setTitle("이미지 선택")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePhoto() // 사진 촬영
                    1 -> pickFromGallery() // 갤러리 선택
                }
            }
            .show()
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun pickFromGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickIntent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    // 갤러리에서 이미지를 선택한 경우
                    val selectedImageUri: Uri? = data?.data
                    if (selectedImageUri != null) {
                        Glide.with(this)
                            .load(selectedImageUri)
                            .into(ivReceipt)
                        btnCamera.visibility = View.GONE
                        btnUpload.visibility = View.VISIBLE

                        // 서버에 전송할 이미지 URL로 변환
                        imageUrl = selectedImageUri.toString()
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    // 카메라로 사진을 촬영한 경우
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    if (imageBitmap != null) {
                        ivReceipt.setImageBitmap(imageBitmap)
                        btnCamera.visibility = View.GONE
                        btnUpload.visibility = View.VISIBLE

                        // 서버 전송을 위해 비트맵을 Uri로 변환
                        val tempUri = getImageUriFromBitmap(imageBitmap)
                        imageUrl = tempUri.toString()
                    }
                }
            }
        }
    }

    // 비트맵을 URI로 변환하는 함수
    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "ReceiptImage", null)
        return Uri.parse(path)
    }
}