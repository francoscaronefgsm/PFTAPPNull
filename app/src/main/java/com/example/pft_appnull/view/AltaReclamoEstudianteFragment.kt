package com.example.pft_appnull.view


import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.example.pft_appnull.R
import com.example.pft_appnull.api.RetrofitClient
import com.example.pft_appnull.model.Reclamo
import com.example.pft_appnull.model.UsuarioDTORest
import com.example.pft_appnull.utils.PreferenceHelper
import com.google.gson.Gson
import java.util.Calendar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AltaReclamoEstudianteFragment : Fragment() {

    val selectedCalendar = Calendar.getInstance()

    private lateinit var cardViewNext: CardView
    private lateinit var cardViewRegister: CardView
    private lateinit var cardViewResume: CardView

    private lateinit var nextButton: Button
    private lateinit var nextTwoButton: Button
    private lateinit var claimRegisterButton: Button

    private lateinit var claimTitleEditText: EditText
    private lateinit var activityNameEditText: EditText
    private lateinit var professorEditText: EditText
    private lateinit var creditsEditText: EditText
    private lateinit var detailsEditText: EditText
    private lateinit var semesterSpinner: Spinner
    private lateinit var activityDateEditText: EditText
    private lateinit var claimTypeRadioGroup: RadioGroup // New RadioGroup for claim type

    //CARD VIEW DE RESUMEN
    private lateinit var resumeClaimTitleTextView: TextView
    private lateinit var resumeActivityDateTextView: TextView
    private lateinit var resumeActivityNameTextView: TextView
    private lateinit var resumeSemesterTextView: TextView
    private lateinit var resumeProfessorTextView: TextView
    private lateinit var resumeCreditsTextView: TextView
    private lateinit var resumeActivityTypeTextView: TextView
    private lateinit var resumeDetailsTextView: TextView
    private lateinit var resumeClaimTypeTextView: TextView // New TextView for claim type

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reclamos_estudiante, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        claimTypeRadioGroup = view.findViewById(R.id.activityTypeRadioGroup)
        cardViewNext = view.findViewById(R.id.cardViewNext)
        cardViewRegister = view.findViewById(R.id.cardViewRegister)
        cardViewResume = view.findViewById(R.id.cardViewResume)

        nextButton = view.findViewById(R.id.nextButton)
        nextTwoButton = view.findViewById(R.id.nextTwoButton)
        claimRegisterButton = view.findViewById(R.id.claimRegisterButton)

        claimTitleEditText = view.findViewById(R.id.claimTitleEditText)
        activityDateEditText = view.findViewById<EditText>(R.id.activityDateEditText)
        activityNameEditText = view.findViewById<EditText>(R.id.activityNameEditText)
        professorEditText = view.findViewById<EditText>(R.id.professorEditText)
        creditsEditText = view.findViewById<EditText>(R.id.creditsEditText)
        detailsEditText = view.findViewById<EditText>(R.id.detailsEditText)

        semesterSpinner = view.findViewById(R.id.semesterSpinner)// Initialize RadioGroup

        val semesterOptions = arrayOf("Seleccionar","1","2","3","4","5","6","7","8")
        semesterSpinner.adapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_list_item_1, semesterOptions
        )


        nextButton.setOnClickListener {
            if (validarCamposRequeridosCard1()) {
                cardViewNext.visibility = View.GONE
                cardViewRegister.visibility = View.VISIBLE
            }
        }

        nextTwoButton.setOnClickListener{
            if (validarCamposRequeridosCard2()) {
                showClaimDataToConfirm()
                cardViewRegister.visibility = View.GONE
                cardViewResume.visibility = View.VISIBLE
            }
        }

        claimRegisterButton.setOnClickListener {
            enviarDatosAApi()
        }

        activityDateEditText.setOnClickListener{
            onClickScheduledDate(it)
        }
    }

    private fun enviarDatosAApi() {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")

        val selectedDate = selectedCalendar.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val selectedDateFormat : String = selectedDate.format(formatter)

        val selectedClaimType = when (claimTypeRadioGroup.checkedRadioButtonId) {
            R.id.vmeRadioButton -> "VME"
            R.id.apeRadioButton -> "APE"
            else -> ""
        }
            // Suponiendo que fechaActual es un LocalDateTime
            val fechaActual: LocalDateTime = LocalDateTime.now()

            // Formatear la fecha como una cadena
            val formattedFechaActual: String = fechaActual.format(formatter)
        val reclamo = Reclamo(
            id = 0, // El ID debería ser 0 o nulo si es un nuevo reclamo y el servidor asigna el ID
            title = claimTitleEditText.text.toString(),
            description = detailsEditText.text.toString(),
            created = formattedFechaActual,
            updated = formattedFechaActual,
            eventVme = if (selectedClaimType == "VME") "VME" else "",
            activityApe = if (selectedClaimType == "APE") "APE" else "",
            semester = semesterSpinner.selectedItem.toString().toInt(),
            date = selectedDateFormat,
            teacher = professorEditText.text.toString(),
            credits = creditsEditText.text.toString().toInt(),
            status = "Ingresado",
            userId = obtenerIdDelEstudianteLogueado()
        )

        RetrofitClient.apiService.altaReclamo(reclamo).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Reclamo registrado correctamente", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Error al registrar el reclamo: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error en la conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun obtenerIdDelEstudianteLogueado(): Long {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        // Obtén la cadena JSON del usuarioDTO guardado
        val usuarioJson = preferences.getString("usuarioDTO", null)
        // Comprueba si el usuarioJson no es nulo
        return if (!usuarioJson.isNullOrEmpty()) {
            // Usa Gson para deserializar la cadena en un objeto UsuarioDTORest
            val usuarioDTO = Gson().fromJson(usuarioJson, UsuarioDTORest::class.java)
            if (usuarioDTO != null) {
                Log.e("ListadoReclamos", "ID del usuario logueado: ${usuarioDTO.id}")
                // Devuelve el idUsuario del objeto UsuarioDTORest
                usuarioDTO.id
            } else {
                Log.e("ListadoReclamos", "Error al deserializar usuarioDTO.")
                -1L
            }
        } else {
            // Si no hay datos, devuelve -1 o maneja como consideres apropiado
            Log.e("ListadoReclamos", "No se encontraron datos del usuario logueado.")
            -1L
        }
    }

    private fun showClaimDataToConfirm(){
        resumeClaimTypeTextView = requireView().findViewById<TextView>(R.id.resumeActivityTypeTextView)

        resumeClaimTitleTextView = requireView().findViewById<TextView>(R.id.resumeClaimTitleTextView)
        resumeActivityDateTextView = requireView().findViewById<TextView>(R.id.resumeActivityDateTextView)
        resumeActivityNameTextView = requireView().findViewById<TextView>(R.id.resumeActivityNameTextView)
        resumeSemesterTextView = requireView().findViewById<TextView>(R.id.resumeSemesterTextView)
        resumeProfessorTextView = requireView().findViewById<TextView>(R.id.resumeProfessorTextView)
        resumeCreditsTextView = requireView().findViewById<TextView>(R.id.resumeCreditsTextView)
        resumeDetailsTextView = requireView().findViewById<TextView>(R.id.resumeDetailsTextView) // Initialize TextView for claim type

        claimTitleEditText = requireView().findViewById<EditText>(R.id.claimTitleEditText)
        activityDateEditText = requireView().findViewById<EditText>(R.id.activityDateEditText)
        activityNameEditText = requireView().findViewById<EditText>(R.id.activityNameEditText)
        semesterSpinner = requireView().findViewById<Spinner>(R.id.semesterSpinner)
        professorEditText = requireView().findViewById<EditText>(R.id.professorEditText)
        creditsEditText = requireView().findViewById<EditText>(R.id.creditsEditText)
        detailsEditText = requireView().findViewById<EditText>(R.id.detailsEditText)

        resumeClaimTitleTextView.text = claimTitleEditText.text.toString()
        resumeActivityDateTextView.text = activityDateEditText.text.toString()
        resumeActivityNameTextView.text = activityNameEditText.text.toString()
        resumeSemesterTextView.text = semesterSpinner.selectedItem.toString()
        resumeProfessorTextView.text = professorEditText.text.toString()
        resumeCreditsTextView.text = creditsEditText.text.toString()
        resumeDetailsTextView.text = detailsEditText.text.toString()

        val selectedClaimType = when (claimTypeRadioGroup.checkedRadioButtonId) {
            R.id.vmeRadioButton -> "VME"
            R.id.apeRadioButton -> "APE"
            else -> ""
        }
        resumeClaimTypeTextView.text = selectedClaimType // Set claim type to TextView
    }

    fun onClickScheduledDate(v: View?){
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)
        val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            selectedCalendar.set(y, m, d)
            activityDateEditText.setText("$d/$m/$y")
        }
        DatePickerDialog(requireContext(), listener, year, month, dayOfMonth).show()
    }

    private fun validarCamposRequeridosCard1(): Boolean {
        // Verificar que todos los campos requeridos estén completos
        if (claimTitleEditText.text.toString().isEmpty() ||
            activityNameEditText.text.toString().isEmpty() ||
            professorEditText.text.toString().isEmpty() ||
            creditsEditText.text.toString().isEmpty() ||
            semesterSpinner.selectedItem.toString() == "Seleccionar" ||
            activityDateEditText.text.toString().isEmpty()) {
            Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun validarCamposRequeridosCard2(): Boolean {
        // Verificar que todos los campos requeridos estén completos
        if (claimTitleEditText.text.toString().isEmpty() ||
            detailsEditText.text.toString().isEmpty() ||
            claimTypeRadioGroup.checkedRadioButtonId == -1) { // Check if claim type is selected
            Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}