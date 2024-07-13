package com.example.pft_appnull.api

import com.example.pft_appnull.model.LoginRequest
import com.example.pft_appnull.model.LoginResponse
import com.example.pft_appnull.model.Reclamo
import com.example.pft_appnull.model.UsuarioDTORest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/login")
    fun logout(@Body token:String): Call<Void>

    @POST("api/claim/")
    fun altaReclamo(@Body nuevoReclamo: Reclamo): Call<Void>

    @GET("api/claim/")
    fun getReclamos(): Call<List<Reclamo>>

    @PUT("api/claim/{id}")
    fun modificarReclamo(@Path("id") id: Long, @Body reclamoModificado: Reclamo): Call<Void>

    @DELETE("api/claim/{id}")
    fun eliminarReclamo(@Path("id") id: Long): Call<Void>

    @GET("api/claim/user/{id}")
    fun getReclamosPorEstudiante(@Path("id") id: Long): Call<List<Reclamo>>

    @GET("api/claim/studentsWithClaims")
    fun obtenerEstudiantesConReclamos(): Call<List<UsuarioDTORest>>

}