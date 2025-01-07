package com.example.madcampweek2.makeRoom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcampweek2.R
import com.example.madcampweek2.home.HomeActivity
import com.example.madcampweek2.network.AnalyzeReceiptResponse
import com.example.madcampweek2.network.ApiClient
import com.example.madcampweek2.network.CreateRoomRequest
import com.example.madcampweek2.network.CreateRoomResponse
import com.example.madcampweek2.network.FindUserResponse
import com.example.madcampweek2.network.ReceiptItem
import com.example.madcampweek2.network.ReceiptService
import com.example.madcampweek2.network.UserService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class HostActivity : AppCompatActivity() {

    private lateinit var btnCamera: Button
    private lateinit var btnUpload: Button
    private lateinit var ivReceipt: ImageView
    private lateinit var btnComplete: Button
    private var imageUrl: String? = null // 서버로 전송할 이미지 URL

    private val friendUuidsMap = mutableMapOf<String, String>()

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
                checkAndAddFriend(friendName)
            }
        }

        ivReceipt = findViewById(R.id.ivReceipt)
        btnCamera = findViewById(R.id.btnCamera)
        btnUpload = findViewById(R.id.btnUpload)
        btnComplete = findViewById(R.id.btnComplete)

        btnCamera.setOnClickListener {
            showImageSelectionDialog()
        }

        btnUpload.setOnClickListener {
            uploadReceiptImage()
        }

        btnComplete.setOnClickListener {
            createRoom()
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

    // 여기부터 영수증 분석
    private fun uploadReceiptImage() {
        imageUrl?.let { imageUrlString ->
            // String을 Uri로 변환
            val uri = Uri.parse(imageUrlString)

            // Uri를 File 객체로 변환
            val file = getFileFromUri(uri)

            // File 객체를 RequestBody로 변환
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

            // MultipartBody.Part 객체 생성 (필드 이름: "file")
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            // Retrofit API 호출
            val service = ApiClient.retrofit.create(ReceiptService::class.java)
            service.analyzeReceipt(body).enqueue(object : Callback<AnalyzeReceiptResponse> {
                override fun onResponse(
                    call: Call<AnalyzeReceiptResponse>,
                    response: Response<AnalyzeReceiptResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.data?.let { data ->
                            showAnalysisResults(data.items)
                        }
                    } else {
                        // 서버가 반환한 오류 메시지 출력
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(
                            this@HostActivity,
                            "분석 실패: ${response.code()} - $errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AnalyzeReceiptResponse>, t: Throwable) {
                    Toast.makeText(this@HostActivity, "분석 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // Uri를 File 객체로 변환
    private fun getFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File(cacheDir, "tempImage.jpg") // 임시 파일 생성
        tempFile.outputStream().use { outputStream ->
            inputStream?.copyTo(outputStream) // 파일 복사
        }
        return tempFile
    }


    private fun showAnalysisResults(items: List<ReceiptItem>) {
        val adapter = HostMenuAdapter(items)
        val recyclerView: RecyclerView = findViewById(R.id.rvMenu)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 결과 저장 (로컬 파일로 저장)
        saveAnalysisResults(items)
    }

    private fun saveAnalysisResults(items: List<ReceiptItem>) {
        val json = Gson().toJson(items)
        val file = File(filesDir, "receipt_analysis.json")
        file.writeText(json)
        Toast.makeText(this, "결과가 저장되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val index = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
        val path = cursor?.getString(index ?: 0)
        cursor?.close()
        return path ?: ""
    }

    private fun checkAndAddFriend(friendName: String) {
        val service = ApiClient.retrofit.create(UserService::class.java)
        val call = service.findUserByName(friendName)

        call.enqueue(object : Callback<FindUserResponse> {
            override fun onResponse(call: Call<FindUserResponse>, response: Response<FindUserResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.status == 200) {
                            body.data?.uuid?.uuid?.let { uuid ->
                                Log.d("HostActivity", "친구 초대 성공: 이름 = $friendName, UUID = $uuid")
                                // UUID를 저장하고 레이아웃에 추가
                                friendUuidsMap[friendName] = uuid
                                addFriendToLayout(friendName)
                                Toast.makeText(this@HostActivity, "사용자가 초대되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@HostActivity, "사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@HostActivity, "오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FindUserResponse>, t: Throwable) {
                Toast.makeText(this@HostActivity, "서버 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addFriendToLayout(friendName: String) {
        val invitedFriendsLayout: LinearLayout = findViewById(R.id.invitedFriendsLayout)

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
    }

    private fun createRoom() {
        val etTitle: EditText = findViewById(R.id.etTitle)
        val roomTitle = etTitle.text.toString()

        if (roomTitle.isBlank()) {
            Toast.makeText(this, "방 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val hostUuid = sharedPreferences.getString("user_uuid", null)
        if (hostUuid == null) {
            Toast.makeText(this, "로그인 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val friendUuids = getFriendUuids()
        if (friendUuids.isEmpty()) {
            Toast.makeText(this, "초대할 친구를 추가해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val items = getAnalyzedItems()
        val totalPrice = calculateTotalPrice(items)

        val request = CreateRoomRequest(
            title = roomTitle,
            host_uuid = hostUuid,
            friend_uuids = friendUuids,
            items = items,
            total_price = totalPrice
        )

        val service = ApiClient.retrofit.create(ReceiptService::class.java)
        service.createRoom(request).enqueue(object : Callback<CreateRoomResponse> {
            override fun onResponse(call: Call<CreateRoomResponse>, response: Response<CreateRoomResponse>) {
                if (response.isSuccessful && response.body()?.status == 201) {
                    val roomId = response.body()?.data?.roomId
                    Toast.makeText(this@HostActivity, "방이 성공적으로 생성되었습니다. 방 ID: $roomId", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorMsg = response.body()?.msg ?: "방 생성 실패"
                    Toast.makeText(this@HostActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreateRoomResponse>, t: Throwable) {
                Toast.makeText(this@HostActivity, "서버 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getFriendUuids(): List<String> {
        return friendUuidsMap.values.toList() // 저장된 UUID 리스트 반환
    }

    private fun getAnalyzedItems(): List<Map<String, String>> {
        val items = mutableListOf<Map<String, String>>()
        // 영수증 분석 결과를 리스트로 반환
        return items
    }

    private fun calculateTotalPrice(items: List<Map<String, String>>): String {
        var total = 0
        for (item in items) {
            val price = item["price"]?.replace(",", "")?.toIntOrNull() ?: 0
            total += price
        }
        return total.toString()
    }

}