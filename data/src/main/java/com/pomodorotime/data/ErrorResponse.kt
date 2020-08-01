package com.pomodorotimemiranda.data

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @field:SerializedName("code") var code: Int = -1,
    @field:SerializedName("status_code") var statusCode: Int = -1,
    @field:SerializedName("message") var message: String = "",
    @field:SerializedName("status") var status: String = ""
) {
    override fun toString(): String {
        return message
    }
}