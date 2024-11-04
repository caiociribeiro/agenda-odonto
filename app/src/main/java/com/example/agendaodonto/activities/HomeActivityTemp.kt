package com.example.agendaodonto.activities

import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.agendaodonto.BaseActivity
import com.example.agendaodonto.R
import com.example.agendaodonto.fragments.ListaDentistasFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivityTemp : BaseActivity() {
    lateinit var toolbar: MaterialToolbar
    lateinit var tvPageTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_tmp)

        toolbar = findViewById(R.id.home_toolbar)
        setSupportActionBar(toolbar)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                else -> false
            }
        }
        loadFragment(ListaDentistasFragment(), getString(R.string.lista_dentistas_fragment_pt))
    }

    private fun loadFragment(fragment: Fragment, pageTitle: String) {
        tvPageTitle = findViewById(R.id.tv_page_title)
        tvPageTitle.text = pageTitle

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_paciente_fragment_container, fragment)
            .commit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

}
