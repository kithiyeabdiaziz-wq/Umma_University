package com.example.umma_university

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository
    private lateinit var ivProfilePic: ImageView
    private var currentPhotoUrl: String = ""

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userRepository = UserRepository()

        val tvProfileEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvProfileReg = findViewById<TextView>(R.id.tvProfileReg)
        val etProfileName = findViewById<EditText>(R.id.etProfileName)
        val etProfileDept = findViewById<EditText>(R.id.etProfileDept)
        val etProfilePhone = findViewById<EditText>(R.id.etProfilePhone)
        val btnUpdateProfile = findViewById<Button>(R.id.btnUpdateProfile)

        ivProfilePic = findViewById(R.id.ivProfilePic)
        val btnChangePhoto = findViewById<Button>(R.id.btnChangePhoto)

        // Fetch current data and fill the fields
        userRepository.getUserProfile { user, errorMsg ->
            runOnUiThread {
                if (user != null) {
                    tvProfileEmail.text = user.email
                    tvProfileReg.text = if (user.regNumber.isEmpty()) "N/A (Staff)" else user.regNumber
                    etProfileName.setText(user.name)
                    etProfileDept.setText(user.department)
                    etProfilePhone.setText(user.phoneNumber)
                    currentPhotoUrl = user.photoUrl

                    // Load image using Glide
                    if (currentPhotoUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(currentPhotoUrl)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(ivProfilePic)
                    }
                } else {
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Open Gallery when "Change Photo" is clicked
        btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Save the updated data
        btnUpdateProfile.setOnClickListener {
            val name = etProfileName.text.toString().trim()
            val dept = etProfileDept.text.toString().trim()
            val phone = etProfilePhone.text.toString().trim()

            if (name.isEmpty() || dept.isEmpty()) {
                Toast.makeText(this, "Name and Department cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to Firestore using the currentPhotoUrl (which may have been updated)
            userRepository.updateProfile(name, dept, phone, currentPhotoUrl) { success, errorMsg ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // NEW: Open Change Password Screen
        val btnGoToChangePassword = findViewById<Button>(R.id.btnGoToChangePassword)
        btnGoToChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
    }

    // Handle the result of picking an image from the gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!

            // 1. Show image immediately on screen
            ivProfilePic.setImageURI(imageUri)

            // 2. Compress the image to save data and upload faster
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Resize to save space
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
                val outputStream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val imageBytes = outputStream.toByteArray()

                // 3. Upload to ImgBB
                Toast.makeText(this, "Uploading photo...", Toast.LENGTH_SHORT).show()
                val uploader = ImgBBUploader()
                uploader.uploadImage(imageBytes) { newUrl ->
                    runOnUiThread {
                        if (newUrl != null) {
                            currentPhotoUrl = newUrl // Save the new URL to be saved to Firestore later
                            Toast.makeText(this, "Photo uploaded! Don't forget to click 'Update Profile'.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Upload failed. Check your ImgBB API key.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}