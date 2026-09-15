package com.duo.app.data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class MeResponse(
    val id: String,
    val email: String? = null,
    val username: String? = null,
    val image: String? = null,
)
