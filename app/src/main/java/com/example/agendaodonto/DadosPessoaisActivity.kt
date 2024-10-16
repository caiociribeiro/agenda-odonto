package com.example.agendaodonto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView

class DadosPessoaisActivity : CommonInterfaceActivity() {
    private lateinit var profileImageView: ImageView
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_dados_pessoais, findViewById(R.id.content_frame))

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = getString(R.string.dados_pessoais)

        profileImageView = findViewById(R.id.profile_image)
        profileImageView.setOnClickListener {
            selectImage()
        }

        val userData = getUserData()
        updateUI(userData)
    }

    private fun selectImage() {
        val intent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            profileImageView.setImageURI(selectedImage)
        }
    }

    fun selectImage(view: View) {}

    private fun updateUI(userData: Map<String, String?>) {
        val ivUserAvatar: ImageView = findViewById(R.id.profile_image)
        val tvUserName: TextView = findViewById(R.id.tv_name)
        val tvUserType: TextView = findViewById(R.id.tv_user_type)
        val etName: EditText = findViewById(R.id.et_name)
        val etEmail: EditText = findViewById(R.id.et_email)
        val etDob: EditText = findViewById(R.id.et_dob)
        val etPhoneNumber: EditText = findViewById(R.id.et_phone)

        val name = userData["name"]
        val email = userData["email"]
        val userType = userData["userType"]
        val dob = userData["etDob"]
        val avatar = userData["avatar"]
        val phoneNumber = userData["phoneNumber"]

        tvUserName.text = name
        tvUserType.text = userType
        etName.setText(name)
        etEmail.setText(email)
        etDob.setText(dob)
        etPhoneNumber.setText(phoneNumber)

        if (avatar != null) {
            if (avatar.isNotEmpty()) {
                Glide.with(this)
                    .load(avatar)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .circleCrop()
                    .into(ivUserAvatar)
            } else {
                ivUserAvatar.setImageResource(R.drawable.user)
            }
        }
    }
}