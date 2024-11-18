package com.example.agendaodonto

import DentistaHomeTabAdapter
import android.os.Bundle
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class HomeDentistaActivity : CommonInterfaceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_home_dentista, findViewById(R.id.content_frame))

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = getString(R.string.home)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        val adapter = DentistaHomeTabAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pendente"
                1 -> "Historico"
                else -> null
            }
        }.attach()
    }

    override fun onStart() {
        super.onStart()

        val auth = Firebase.auth
        val user = auth.currentUser

        if (user != null) gerarDisponibilidadeDentista(user.uid)
        else {
            auth.signOut()
            finish()
        }
    }
}