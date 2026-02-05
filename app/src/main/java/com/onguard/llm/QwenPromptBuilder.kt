package com.onguard.llm

/**
 * Qwen 2.5 ChatML 형식 프롬프트 생성.
 *
 * 시스템 지시 + 사용자 입력을 ChatML 토큰으로 감싸
 * assistant가 JSON만 출력하도록 유도한다.
 */
object QwenPromptBuilder {

    private const val IM_START = "<|im_start|>"
    private const val IM_END = "<|im_end|>"

    private val defaultSystemInstruction = """
        너는 사기 탐지 전문가야. 사용자가 보내는 메시지(및 선택적 1차 분석 요약)를 보고, 반드시 아래 JSON 형식으로만 답변해. 다른 텍스트는 출력하지 마.
        JSON 형식:
        {"isScam": true 또는 false, "confidence": 0.0~1.0, "scamType": "투자사기" 또는 "중고거래사기" 또는 "피싱" 또는 "정상", "warningMessage": "사용자에게 보여줄 경고 메시지 (한국어, 2문장 이내)", "reasons": ["위험 요소 1", "위험 요소 2"], "suspiciousParts": ["의심되는 문구 인용"]}
    """.trimIndent()

    /**
     * ChatML 형식 프롬프트 생성.
     *
     * @param userInput 사용자 메시지 (Rule/URL 1차 분석 요약 + [메시지] 포함 가능)
     * @param systemInstruction 시스템 지시문. null이면 기본 사기 탐지 JSON 지시 사용
     */
    fun buildPrompt(
        userInput: String,
        systemInstruction: String? = null
    ): String {
        val system = systemInstruction ?: defaultSystemInstruction
        return buildString {
            append(IM_START).append("system\n")
            append(system)
            append("\n").append(IM_END).append("\n")
            append(IM_START).append("user\n")
            append(userInput.trim())
            append("\n").append(IM_END).append("\n")
            append(IM_START).append("assistant\n")
        }
    }
}
