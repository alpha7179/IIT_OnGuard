package com.onguard.llm

/**
 * LlamaManager 초기화 결과.
 */
sealed class LlamaInitResult {
    data object Success : LlamaInitResult()
    data class Failure(val cause: Throwable? = null) : LlamaInitResult()
}

/**
 * LlamaManager 추론(분석) 결과.
 */
sealed class LlamaAnalyzeResult {
    data class Success(val text: String) : LlamaAnalyzeResult()
    data object NotInitialized : LlamaAnalyzeResult()
    data object EmptyInput : LlamaAnalyzeResult()
    data class Error(val cause: Throwable? = null) : LlamaAnalyzeResult()
}
