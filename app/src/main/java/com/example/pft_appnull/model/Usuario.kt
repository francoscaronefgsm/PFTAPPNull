package com.example.pft_appnull.model

import java.io.Serializable
import java.time.LocalDate

data class UsuarioDTORest(
    val id: Long,
    val username: String,
    val password: String,
    val firstName: String,
    val secondName: String,
    val firstSurname: String,
    val secondSurname: String,
    val document: Long,
    val birthDate: String,
    val personalEmail: String,
    val phone: Long,
    val department: Int,
    val locality: Int,
    val institutionalEmail: String,
    val itr: Int,
    val generation: Int,
    val area: String?,
    val teacherRole: String?,
    val active: Boolean
) : Serializable

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val usuarioDTO: UsuarioDTORest
) : Serializable
