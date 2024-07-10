package com.example.pft_appnull.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pft_appnull.R
import com.example.pft_appnull.api.RetrofitClient
import com.example.pft_appnull.model.LoginRequest
import com.example.pft_appnull.model.LoginResponse
import com.example.pft_appnull.model.UsuarioDTORest
import com.example.pft_appnull.utils.PreferenceHelper
import com.example.pft_appnull.utils.PreferenceHelper.get
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = PreferenceHelper.defaultPrefs(this)
        if (preferences["token", ""].contains(".")) {
            goToHome()
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            performLogin()
        }

    }

    private fun goToHome() {
        val i = Intent(this, HomeActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun createSessionPreference(token: String, usuarioDTO: UsuarioDTORest) {
        val preferences = PreferenceHelper.defaultPrefs(this)
        val gson = Gson()
        val usuarioJson = gson.toJson(usuarioDTO) // Convierte el usuarioDTO a JSON

        with(preferences.edit()) {
            putString("token", token)
            putString("usuarioDTO", usuarioJson) // Guarda el JSON en las preferencias
            apply()
        }
    }


    private fun performLogin() {
        val userEditText = findViewById<EditText>(R.id.usuarioEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val username = userEditText.text.toString()
        val password = passwordEditText.text.toString()
        if (username.isNotEmpty() && password.isNotEmpty()) {
            val loginRequest = LoginRequest(username, password)
            RetrofitClient.apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null) {
                            createSessionPreference(loginResponse.token, loginResponse.usuarioDTO)
                            goToHome()
                        } else {
                            Toast.makeText(applicationContext, "Login fallido", Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Credenciales incorrectas",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        "Error en la solicitud: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            Toast.makeText(applicationContext, "Complete todos los campos", Toast.LENGTH_LONG)
                .show()
        }
    }
}