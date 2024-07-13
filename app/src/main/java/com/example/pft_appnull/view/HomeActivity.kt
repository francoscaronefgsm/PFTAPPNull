package com.example.pft_appnull.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.pft_appnull.R
import com.example.pft_appnull.model.UsuarioDTORest
import com.example.pft_appnull.utils.PreferenceHelper
import com.example.pft_appnull.view.AltaReclamoEstudianteFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson


class HomeActivity : AppCompatActivity() {

    private lateinit var nav : BottomNavigationView

    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isAnalyst()) {
            setContentView(R.layout.activity_home_analista)
            nav = findViewById(R.id.nav)
            nav.setOnNavigationItemSelectedListener { item ->
                when(item.itemId){
                    R.id.home -> { supportFragmentManager.beginTransaction().replace(R.id.container,
                        HomeFragment()
                    ).commit()}
                    R.id.lista_reclamos -> { supportFragmentManager.beginTransaction().replace(R.id.container,
                        TodosReclamosAnalistaFragment()
                    ).commit()}
                }
                true
            }
        } else {
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
        }
        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.container,
                HomeFragment()
            ).commit()}
        }
    private fun isAnalyst(): Boolean {
        val usuarioJson = preferences.getString("usuarioDTO", "")
        if (usuarioJson != null) {
            if (usuarioJson.isNotEmpty()) {
                val usuarioDTO = Gson().fromJson(usuarioJson, UsuarioDTORest::class.java)
                return usuarioDTO.username == "admin"
            }
        }
        return false
    }

}
