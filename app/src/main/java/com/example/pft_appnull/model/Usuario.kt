package com.example.pft_appnull.model

import java.io.Serializable
import java.time.LocalDate

data class UsuarioDTORest(
    val id : Long,
    val active: Boolean,
    val birth_date: LocalDate,
    val document: String,
    val first_name: String,
    val first_surname: String,
    val institutional_mail: String,
    val password: String,
    val personal_email: String,
    val phone: Int,
    val second_name: String,
    val second_surname: String,
    val username: String,
    val department_id: Long,
    val itr_id: Long,
    val locality_id: Long
) : Serializable

data class LoginRequest(val nombreUsuario: String, val contrasenia: String)

data class LoginResponse(
    val token: String,
    val usuarioDTORest: UsuarioDTORest
) : Serializable