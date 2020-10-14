package com.pomodorotime.domain.models

data class ErrorResponse(
    var code: Int = -1,
    var statusCode: Int = -1,
    var message: String = "",
    var status: String = ""
)