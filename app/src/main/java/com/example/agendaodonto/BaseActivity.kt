package com.example.agendaodonto

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        // Configurar o NavigationView, se necessário
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dados_pessoais -> {
                    val intent = Intent(this, DadosPessoaisActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_logout -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    drawerLayout.closeDrawers()
                    true
                }

                else -> false
            }
        }

        // Set up header avatar click to open the navigation drawer
        val navIcon: ImageView = findViewById(R.id.iv_menu)
        navIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // Open the navigation drawer
        }
    }

    // Função para configurar o layout específico de cada página
    fun setContent(layoutResID: Int) {
        val frameLayout: FrameLayout = findViewById(R.id.content_frame)
        layoutInflater.inflate(layoutResID, frameLayout, true)
    }
}