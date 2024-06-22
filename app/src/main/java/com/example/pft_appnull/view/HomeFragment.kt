package com.example.pft_appnull.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pft_appnull.R
import com.example.pft_appnull.model.UsuarioDTORest

class HomeFragment : Fragment() {

    private lateinit var nombreTextView: TextView
    private lateinit var secondNameTextView: TextView
    private lateinit var firstSurnameTextView: TextView
    private lateinit var secondSurnameTextView: TextView
    private lateinit var documentoTextView: TextView
    private lateinit var estadoTextView: TextView
    private lateinit var fechaNacimientoTextView: TextView
    private lateinit var mailTextView: TextView
    private lateinit var mailInstitucionalTextView: TextView
    private lateinit var nombreUsuarioTextView: TextView
    private lateinit var telefonoTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inicializar los TextViews
        nombreTextView = view.findViewById(R.id.nombreTextView)
        secondNameTextView = view.findViewById(R.id.secondNameTextView)
        firstSurnameTextView = view.findViewById(R.id.firstSurnameTextView)
        secondSurnameTextView = view.findViewById(R.id.secondSurnameTextView)
        documentoTextView = view.findViewById(R.id.documentoTextView)
        estadoTextView = view.findViewById(R.id.estadoTextView)
        fechaNacimientoTextView = view.findViewById(R.id.fechaNacimientoTextView)
        mailTextView = view.findViewById(R.id.mailTextView)
        mailInstitucionalTextView = view.findViewById(R.id.mailInstitucionalTextView)
        nombreUsuarioTextView = view.findViewById(R.id.nombreUsuarioTextView)
        telefonoTextView = view.findViewById(R.id.telefonoTextView)
        logoutButton = view.findViewById(R.id.logoutButton)

        // Obtener el objeto UsuarioDTORest desde los argumentos
        val usuario = arguments?.getSerializable("usuario") as UsuarioDTORest?

        // Verificar que el usuario no sea null y establecer los datos en los TextViews
        usuario?.let {
            nombreTextView.text = it.first_name
            secondNameTextView.text = it.second_name
            firstSurnameTextView.text = it.first_surname
            secondSurnameTextView.text = it.second_surname
            documentoTextView.text = it.document
            estadoTextView.text = if (it.active) "Activo" else "Inactivo"
            fechaNacimientoTextView.text = it.birth_date.toString()
            mailTextView.text = it.personal_email
            mailInstitucionalTextView.text = it.institutional_mail
            nombreUsuarioTextView.text = it.username
            telefonoTextView.text = it.phone.toString()
        }

        // Manejar el botón de logout
        logoutButton.setOnClickListener {
            // Implementa tu lógica de cierre de sesión aquí
        }

        return view
    }
}
