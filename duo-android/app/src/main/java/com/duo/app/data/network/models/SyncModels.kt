package com.duo.app.data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class SyncRequest(
    val activeCourseId: Int? = null,
    val points: Int? = null,
    val hearts: Int? = null,
    val completedChallengeIds: List<Int> = emptyList(),
)

@Serializable
data class SyncResponse(
    val synced: Boolean,
    val user: SyncUserResponse? = null,
)

@Serializable
data class SyncUserResponse(
    val id: String,
    val email: String? = null,
    val username: String? = null,
    val points: Int? = null,
    val hearts: Int? = null,
    val activeCourseId: Int? = null,
)
