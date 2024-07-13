package com.example.pft_appnull.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pft_appnull.R
import com.example.pft_appnull.api.RetrofitClient
import com.example.pft_appnull.model.Reclamo
import com.example.pft_appnull.model.UsuarioDTORest
import com.example.pft_appnull.utils.PreferenceHelper
import com.example.pft_appnull.view.ReclamoAdapter
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ListadoReclamosEstudianteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReclamoAdapter
    private var reclamosList = listOf<Reclamo>() // Inicializa con una lista vacía
    private var estudianteId: Long = 0
    private var estadoSeleccionadoId: Long = 0
    private lateinit var fechaConvocatoriaEditText: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("ListadoReclamos", "onCreateView")
        return inflater.inflate(R.layout.fragment_listado_reclamos_estudiante, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.claimsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        estudianteId = obtenerIdDelEstudianteLogueado()
        adapter = ReclamoAdapter(reclamosList) { reclamo ->
            showReclamoDialog(reclamo)
        }
        recyclerView.adapter = adapter
        loadReclamosFiltrados(obtenerIdDelEstudianteLogueado())
        val estadoSpinner: Spinner = view.findViewById(R.id.estadoSpinner)
        val estados = arrayOf("Todos","Ingresado", "En Proceso", "Finalizado")
        val estadoAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
        estadoSpinner.adapter = estadoAdapter
        estadoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                estadoSeleccionadoId = position.toLong() // Almacena el estado seleccionado
                loadReclamosFiltrados(obtenerIdDelEstudianteLogueado())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No se seleccionó ningún elemento
            }
        }
    }

    private fun loadReclamosFiltrados(estudianteId: Long) {
        RetrofitClient.apiService.getReclamosPorEstudiante(estudianteId).enqueue(object : Callback<List<Reclamo>> {
            override fun onResponse(call: Call<List<Reclamo>>, response: Response<List<Reclamo>>) {
                if (response.isSuccessful && response.body() != null) {
                    reclamosList = response.body()!!
                    val reclamosFiltrados = if (estadoSeleccionadoId == 0L) {
                        // Estado "Todos"
                        reclamosList
                    } else {
                        // Filtra por estado seleccionado
                        val estado = when (estadoSeleccionadoId) {
                            1L -> "PENDING"
                            2L -> "IN_PROGRESS"
                            3L -> "COMPLETED"
                            else -> ""
                        }
                        reclamosList.filter { it.status == estado }
                    }
                    adapter = ReclamoAdapter(reclamosFiltrados) { reclamo ->
                        showReclamoDialog(reclamo)
                    }
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "No se pudieron cargar los reclamos filtrados", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Reclamo>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun obtenerIdDelEstudianteLogueado(): Long {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        // Obtén la cadena JSON del usuarioDTO guardado
        val usuarioJson = preferences.getString("usuarioDTO", null)
        // Comprueba si el usuarioJson no es nulo
        if (!usuarioJson.isNullOrEmpty()) {
            // Usa Gson para deserializar la cadena en un objeto UsuarioDTORest
            val usuarioDTO = Gson().fromJson(usuarioJson, UsuarioDTORest::class.java)
            Log.e("ListadoReclamos", "ID del usuario logueado: ${usuarioDTO.id}")
            // Devuelve el idUsuario del objeto UsuarioDTORest
            return usuarioDTO.id
        } else {
            // Si no hay datos, devuelve -1 o maneja como consideres apropiado
            Log.e("ListadoReclamos", "No se encontraron datos del usuario logueado.")
            return -1L
        }
    }

    private fun showReclamoDialog(reclamo: Reclamo) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_reclamo, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        fechaConvocatoriaEditText = dialogView.findViewById(R.id.fechaConvocatoriaEditText)
        fechaConvocatoriaEditText.setOnClickListener{
            onClickScheduledDate(it)
        }

        // Inicializa los EditTexts y el Spinner con los datos del reclamo
        val tituloEditText = dialogView.findViewById<EditText>(R.id.tituloEditText)
        val nombreActividadEditText = dialogView.findViewById<EditText>(R.id.nombreActividadEditText)
        val fechaConvocatoriaEditText = dialogView.findViewById<EditText>(R.id.fechaConvocatoriaEditText)
        val semesterSpinner = dialogView.findViewById<Spinner>(R.id.semesterSpinner)
        val docenteEditText = dialogView.findViewById<EditText>(R.id.docenteEditText)
        val creditosEditText = dialogView.findViewById<EditText>(R.id.creditosEditText)
        val detailsEditText = dialogView.findViewById<EditText>(R.id.detailsEditText)

        tituloEditText.setText(reclamo.title)
        fechaConvocatoriaEditText.setText(reclamo.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        if(reclamo.activityApe.isNotEmpty()){
            nombreActividadEditText.setText(reclamo.activityApe)
        }else{
            nombreActividadEditText.setText(reclamo.eventVme)
        }
        docenteEditText.setText(reclamo.teacher)
        creditosEditText.setText(reclamo.credits.toString())
        detailsEditText.setText(reclamo.description)

        // Crear una lista de semestres como Strings
        val listaSemestres = (1..8).map { it.toString() }

        val semestreAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listaSemestres)
        semestreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        semesterSpinner.adapter = semestreAdapter

        // Encuentra el índice del semestre del reclamo en tu lista
        val semestreIndex = listaSemestres.indexOf(reclamo.semester.toString())
        if (semestreIndex >= 0) {
            semesterSpinner.setSelection(semestreIndex)
        }

        // Encuentra el Spinner para el tipo
        val tipoSpinner = dialogView.findViewById<Spinner>(R.id.tipoActividadSpinner)

        // Crear una lista de tipos
        val listaTipos = listOf("VME", "APE")

        val tipoAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listaTipos)
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tipoSpinner.adapter = tipoAdapter

        // Encuentra el índice del tipo del reclamo en tu lista
        if(reclamo.activityApe.isNotEmpty()){
            tipoSpinner.setSelection(1)
        }else{
            tipoSpinner.setSelection(0)
        }


        // Configura el diálogo con botones de acción si lo necesitas
        builder.setPositiveButton("Guardar") { dialog, _ ->
            val titulo = tituloEditText.text.toString()
            val nombreActividad = nombreActividadEditText.text.toString()
            val fechaConvocatoria = fechaConvocatoriaEditText.text.toString()
            val semestre = semesterSpinner.selectedItem.toString().toInt()
            val docente = docenteEditText.text.toString()
            val creditos = creditosEditText.text.toString().toInt()
            val tipo = tipoSpinner.selectedItem.toString()
            val detalle = detailsEditText.text.toString()
            if (validarCamposReclamo(titulo, nombreActividad, fechaConvocatoria, semestre, docente, creditos, tipo, detalle)) {
                var actividadApe:String = ""
                var actividadVME:String = ""
                if(tipo.equals("VME")){
                    actividadVME=nombreActividad;
                }else if (tipo.equals("APE")){
                    actividadApe=nombreActividad;
                }
                val reclamoModificado = Reclamo(
                    id = reclamo.id,
                    title = titulo,
                    description = detalle,
                    created = "",
                    updated = "",
                    eventVme = actividadVME,
                    activityApe = actividadApe,
                    semester = semestre,
                    date=fechaConvocatoria,
                    teacher = docente,
                    credits = creditos,
                    status = "Ingresado",
                    userId = obtenerIdDelEstudianteLogueado()
                )

                RetrofitClient.apiService.modificarReclamo(reclamo.id, reclamoModificado).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            loadReclamosFiltrados(1)
                            Toast.makeText(requireContext(), "Reclamo actualizado con éxito", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Error al actualizar el reclamo", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(requireContext(), "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        builder.setNeutralButton("Eliminar") { dialog, _ ->
            // Lógica para eliminar el reclamo
            eliminarReclamo(reclamo.id)
        }

        builder.setNegativeButton("Cancelar", null)

        builder.create().show()
    }


    private fun eliminarReclamo(reclamoId: Long) {
        RetrofitClient.apiService.eliminarReclamo(reclamoId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Recargar la lista de reclamos tras eliminar uno
                    loadReclamosFiltrados(1) // Usa el estado seleccionado para recargar
                    Toast.makeText(requireContext(), "Reclamo eliminado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "¡No puedes eliminar un reclamo que tiene una acción asociada!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validarCamposReclamo(titulo: String, nombreActividad: String, fechaConvocatoria: String, semestre: Int, docente: String, creditos: Int, tipo: String, detalle: String): Boolean {
        if (titulo.isEmpty() || nombreActividad.isEmpty() || fechaConvocatoria.isEmpty() || semestre == 0 || docente.isEmpty() || creditos == 0 || tipo == "Seleccionar" || detalle.isEmpty()) {
            Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    fun onClickScheduledDate(v:View?){
        val selectedCalendar = Calendar.getInstance()
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)
        val listener = DatePickerDialog.OnDateSetListener{ datePicker, y, m, d ->
            selectedCalendar.set(y,m,d)
            fechaConvocatoriaEditText.setText("$d/$m/$y")
        }
        DatePickerDialog(requireContext(), listener, year, month, dayOfMonth).show()
    }
}
