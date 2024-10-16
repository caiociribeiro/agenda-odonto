package com.example.agendaodonto

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

abstract class CommonInterfaceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_interface)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        updateNavDrawer(getUserData())


        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_inicio -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_dados_pessoais -> {
                    val intent = Intent(this, DadosPessoaisActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_logout -> {
                    Firebase.auth.signOut()


                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }

                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }

                else -> false
            }
        }

        val navIcon: ImageView = findViewById(R.id.iv_menu)
        navIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


    }

    fun updateNavDrawer(data: Map<String, String?>) {
        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)

        val tvUserName: TextView = headerView.findViewById(R.id.tv_user_name)
        val ivUserAvatar: ImageView = headerView.findViewById(R.id.iv_user_avatar)

        val name = data["name"]
        val avatar = data["avatar"]

        tvUserName.text = name

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

    fun setContent(layoutResID: Int) {
        val frameLayout: FrameLayout = findViewById(R.id.content_frame)
        layoutInflater.inflate(layoutResID, frameLayout, true)
    }

    override fun onStart() {
        super.onStart()
    }
}