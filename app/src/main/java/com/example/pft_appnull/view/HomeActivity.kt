package com.example.pft_appnull.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pft_appnull.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : AppCompatActivity() {

    private lateinit var nav : BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            setContentView(R.layout.activity_home_estudiante)

            nav = findViewById(R.id.nav)
            nav.setOnNavigationItemSelectedListener { item ->
                when(item.itemId){
                    R.id.home -> { supportFragmentManager.beginTransaction().replace(R.id.container,
                        HomeFragment()
                    ).commit()}
                    R.id.alta_reclamo -> { supportFragmentManager.beginTransaction().replace(R.id.container,
                        AltaReclamoEstudianteFragment()
                    ).commit()}
                    R.id.lista_reclamos -> { supportFragmentManager.beginTransaction().replace(R.id.container,
                        ListadoReclamosEstudianteFragment()
                    ).commit()}
                }
                true
            }

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.container,
                HomeFragment()
            ).commit()}
        }


}
