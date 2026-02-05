package com.onguard.llm

/**
 * 온디바이스 LLM 백엔드 계약.
 *
 * 테스트/모킹 시 [LlamaManager] 대신 대체 구현을 주입할 수 있도록 한다.
 */
interface LlamaBackend {

    suspend fun initModel(): LlamaInitResult
    suspend fun analyzeText(input: String): LlamaAnalyzeResult
    fun close()
}
