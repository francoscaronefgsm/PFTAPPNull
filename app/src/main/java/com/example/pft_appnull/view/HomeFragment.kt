package com.example.pft_appnull.view

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pft_appnull.R
import com.example.pft_appnull.api.RetrofitClient
import com.example.pft_appnull.model.UsuarioDTORest
import com.example.pft_appnull.utils.PreferenceHelper
import com.example.pft_appnull.utils.PreferenceHelper.set
import com.example.pft_appnull.utils.PreferenceHelper.get
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale


class HomeFragment : Fragment() {

    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(requireContext())
    }

    companion object {
        fun newInstance() = com.example.pft_appnull.view.HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener(){
            performLogout()
        }

        val usuarioJson = preferences.getString("usuarioDTO", "")
        val usuarioDTO = Gson().fromJson(usuarioJson, UsuarioDTORest::class.java)

        val nombre = SpannableString("Nombre: ${usuarioDTO.firstName}")
        nombre.setSpan(StyleSpan(Typeface.BOLD), 0, 7, 0) // "Nombre:" en negrita
        val apellido = SpannableString("Apellido 1: ${usuarioDTO.firstSurname}")
        apellido.setSpan(StyleSpan(Typeface.BOLD), 0, 10, 0) // "Apellido:" en negrita
        val apellido2 = SpannableString("Apellido 2: ${usuarioDTO.secondSurname}")
        apellido2.setSpan(StyleSpan(Typeface.BOLD), 0, 10, 0) // "Apellido:" en negrita
        val documento = SpannableString("Documento: ${usuarioDTO.document}")
        documento.setSpan(StyleSpan(Typeface.BOLD), 0, 10, 0) // "Mail Institucional:" en negrita
        val nombreUsuario = SpannableString("Username: ${usuarioDTO.username}")
        nombreUsuario.setSpan(StyleSpan(Typeface.BOLD), 0, 9, 0) // "Nombre de Usuario:" en negrita

        view.findViewById<TextView>(R.id.nombreTextView).text = nombre
        view.findViewById<TextView>(R.id.firstSurnameTextView).text = apellido
        view.findViewById<TextView>(R.id.secondSurnameTextView).text = apellido2
        view.findViewById<TextView>(R.id.documentoTextView).text = documento
        view.findViewById<TextView>(R.id.nombreUsuarioTextView).text = nombreUsuario


    }
    private fun goToLogin() {
        val activityContext = requireActivity()
        val intent = Intent(activityContext, MainActivity::class.java)
        activityContext.startActivity(intent)
        requireActivity().finish()
    }

    private fun performLogout() {
        val token = preferences["token", ""]
        RetrofitClient.apiService.logout(token).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                clearSessionPreference()
                goToLogin()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Utiliza 'requireContext()' para obtener el contexto del fragmento
                Toast.makeText(requireContext(), "Error en el servidor", Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun clearSessionPreference(){
        preferences["token"] = ""
    }
}
