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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class TodosReclamosAnalistaFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReclamoAdapter
    private var reclamosList: List<Reclamo> = listOf() // Inicializa la lista vacía
    val selectedCalendar = Calendar.getInstance()
    private var estudianteSeleccionadoId: Long = 0
    private var estadoSeleccionadoId: Long = 0
    private lateinit var fechaConvocatoriaEditText: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configuración inicial de los componentes de la vista
        recyclerView = view.findViewById(R.id.claimsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val estadoSpinner: Spinner = view.findViewById(R.id.estadoSpinner)
        val estudiantesSpinner: Spinner = view.findViewById(R.id.estudiantesSpinner)

        cargarEstadosEnSpinner(estadoSpinner)
        cargarEstudiantesEnSpinner(estudiantesSpinner)
        loadReclamosFiltrados(0,0) // Cargar todos los reclamos inicialmente
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_listado_todos_reclamos, container, false)
    }

    private fun cargarEstadosEnSpinner(spinner: Spinner) {
        // Suponiendo que tienes una función para obtener la lista de estados
        val estados = listOf("Todos","Ingresado", "En Proceso", "Finalizado") // Ejemplo, reemplazar con llamada a la API
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                estadoSeleccionadoId = position.toLong()
                // Implementa la lógica para filtrar los reclamos por estado
                loadReclamosFiltrados(estudianteSeleccionadoId,estadoSeleccionadoId)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun cargarEstudiantesEnSpinner(spinner: Spinner) {
        RetrofitClient.apiService.obtenerEstudiantesConReclamos().enqueue(object : Callback<List<UsuarioDTORest>> {
            override fun onResponse(call: Call<List<UsuarioDTORest>>, response: Response<List<UsuarioDTORest>>) {
                if (response.isSuccessful && response.body() != null) {
                    val estudiantesList = mutableListOf(UsuarioDTORest(
                        id = 0,
                        username = "Todos",
                        password = "Todos",
                        firstName = "Todos",
                        secondName = "Todos",
                        firstSurname = "Todos",
                        secondSurname = "Todos",
                        document = 0,
                        birthDate = "Todos",
                        personalEmail = "Todos",
                        phone = 0,
                        department = 0,
                        locality = 0,
                        institutionalEmail = "Todos",
                        itr = 0,
                        generation = 0,
                        area = null,
                        teacherRole = null,
                        active = false
                    )) // Agrega "Todos" con ID 0
                    estudiantesList.addAll(response.body()!!)
                    val nombresEstudiantes = estudiantesList.map { it.username }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresEstudiantes)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            estudianteSeleccionadoId = estudiantesList[position].id
                            loadReclamosFiltrados(estudianteSeleccionadoId,estadoSeleccionadoId)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
                } else {
                    Toast.makeText(requireContext(), "No se pudieron cargar los estudiantes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UsuarioDTORest>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error al conectar con el servidor: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun loadReclamosFiltrados(estudianteId: Long , estadoSeleccionadoId: Long) {
        RetrofitClient.apiService.getReclamos().enqueue(object : Callback<List<Reclamo>> {
            override fun onResponse(call: Call<List<Reclamo>>, response: Response<List<Reclamo>>) {
                if (response.isSuccessful && response.body() != null) {
                    reclamosList = response.body()!!
                    Log.d("Reclamos", "Reclamos recibidos: $reclamosList")
                    val reclamosFiltrados = if (estadoSeleccionadoId == 0L && estudianteId==0L) {
                        reclamosList
                    } else if (estadoSeleccionadoId != 0L && estudianteId==0L){
                        val estado = when (estadoSeleccionadoId) {
                            1L -> "PENDING"
                            2L -> "IN_PROGRESS"
                            3L -> "COMPLETED"
                            else -> ""
                        }
                        reclamosList.filter { it.status == estado }
                    } else {
                        val estado = when (estadoSeleccionadoId) {
                            1L -> "PENDING"
                            2L -> "IN_PROGRESS"
                            3L -> "COMPLETED"
                            else -> ""
                        }
                        if(estado!=""){
                            reclamosList.filter { it.status == estado && it.userId==estudianteId}
                        }else{
                            reclamosList.filter {it.userId==estudianteId}
                        }

                    }
                    Log.d("Reclamos", "Reclamos filtrados: $reclamosFiltrados")
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



    private fun showReclamoDialog(reclamo: Reclamo) {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_reclamo, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        fechaConvocatoriaEditText = dialogView.findViewById<EditText>(R.id.fechaConvocatoriaEditText)
        fechaConvocatoriaEditText.setOnClickListener {
            onClickScheduledDate(it)
        }

        // Inicializa los EditTexts y el Spinner con los datos del reclamo
        val tituloEditText = dialogView.findViewById<EditText>(R.id.tituloEditText)
        val fechaConvocatoriaEditText = dialogView.findViewById<EditText>(R.id.fechaConvocatoriaEditText)
        val nombreActividadEditText = dialogView.findViewById<EditText>(R.id.nombreActividadEditText)
        val semesterSpinner = dialogView.findViewById<Spinner>(R.id.semesterSpinner)
        val docenteEditText = dialogView.findViewById<EditText>(R.id.docenteEditText)
        val creditosEditText = dialogView.findViewById<EditText>(R.id.creditosEditText)
        val tipoActividadSpinner = dialogView.findViewById<Spinner>(R.id.tipoActividadSpinner)
        val detailsEditText = dialogView.findViewById<EditText>(R.id.detailsEditText)

        tituloEditText.setText(reclamo.title)
        fechaConvocatoriaEditText.setText(reclamo.date)
        if (reclamo.activityApe.isNullOrEmpty()){
            nombreActividadEditText.setText(reclamo.eventVme)
        }else{
            nombreActividadEditText.setText(reclamo.activityApe)
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
        if (reclamo.activityApe.isNullOrEmpty()){
            tipoSpinner.setSelection(0)
        }else{
            tipoSpinner.setSelection(1)
        }

        builder.setNegativeButton("Cerrar", null)

        builder.create().show()
    }


    fun onClickScheduledDate(v:View?){
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
