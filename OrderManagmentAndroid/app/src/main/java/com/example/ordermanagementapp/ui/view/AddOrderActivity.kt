package com.example.ordermanagementapp.ui.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ordermanagementapp.databinding.ActivityAddOrderBinding
import com.example.ordermanagementapp.ui.viewmodel.OrderViewModel
import java.io.File
import java.io.FileOutputStream
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddOrderBinding
    private lateinit var orderViewModel: OrderViewModel
    private var selectedImageFile: File? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { uri ->
                selectedImageFile = uriToFile(uri)
                Toast.makeText(this, "Zdjęcie zostało wybrane", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.btnSaveOrder.setOnClickListener {
            val prodName = binding.etProductName.text.toString().trim()
            val qtyStr = binding.etQuantity.text.toString().trim()
            val priceStr = binding.etPrice.text.toString().trim()

            if (prodName.isNotEmpty() && qtyStr.isNotEmpty() && priceStr.isNotEmpty()) {

                val mediaType = MediaType.parse("text/plain")

                val productNamePart = RequestBody.create(mediaType, prodName)
                val quantityPart = RequestBody.create(mediaType, qtyStr)
                val pricePart = RequestBody.create(mediaType, priceStr)

                val imagePart = selectedImageFile?.let { file ->
                    val imageType = MediaType.parse("image/*")
                    val requestFile = RequestBody.create(imageType, file)
                    MultipartBody.Part.createFormData("imageFile", file.name, requestFile)
                }

                orderViewModel.addOrderMultipart(this, productNamePart, quantityPart, pricePart, imagePart) { success, message ->
                    runOnUiThread {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        if (success) {
                            finish()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "temp_order_image.png")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}