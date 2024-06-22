package com.example.pft_appnull.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.pft_appnull.R
import com.example.pft_appnull.api.RetrofitClient
import com.example.pft_appnull.utils.PreferenceHelper
import com.example.pft_appnull.utils.PreferenceHelper.set
import com.example.pft_appnull.utils.PreferenceHelper.get
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {



    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)


        try{
            val apellido = preferences.getString("apellido","nada")
            val userNameTextView = findViewById<TextView>(R.id.usuarioEditText)
            userNameTextView.text = apellido
            //Toast.makeText(applicationContext, "username: ${userNameTextView}", Toast.LENGTH_LONG).show()

            val logoutButton = findViewById<Button>(R.id.cerrarSesionButton)
            logoutButton.setOnClickListener(){
                performLogout()
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Error en el servidor: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }



    private fun performLogout(){
        val token = preferences["token",""]

        RetrofitClient.apiService.logout(token).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                clearSessionPreference()

            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(applicationContext, "Error en el servidor", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clearSessionPreference(){
        preferences["token"] = ""
    }

}