package com.yusmp.domain.common.model

data class ApiError(
    val extraMessage: String,
    val code: Int,
    val message: String,
    val violations: List<ErrorItem>,
    val key: String,
    val email: String,
) {
    override fun toString(): String {
        return """
            |code: $code,
            |message: $message,
            |extraMessage: $extraMessage,
            |violations: $violations,
            |key: $key,
            |email: $email
        """
    }
}

data class ErrorItem(
    val propertyPath: String,
    val key: String,
    val message: String
)