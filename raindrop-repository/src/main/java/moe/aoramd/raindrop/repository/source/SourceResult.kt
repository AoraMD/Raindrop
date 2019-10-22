package moe.aoramd.raindrop.repository.source

data class SourceResult<T>(
    val success: Boolean,
    private val resultErrorMsg: String? = null,
    private val resultContent: T? = null
) {
    val errorMsg: String
        get() = resultErrorMsg
            ?: throw IllegalStateException("null error message when request is successful")

    val content: T
        get() = resultContent
            ?: throw IllegalStateException("null value when request is unsuccessful")
}