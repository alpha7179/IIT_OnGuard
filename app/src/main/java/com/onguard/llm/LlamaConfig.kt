package com.onguard.llm

/**
 * llama.cpp(java-llama.cpp) 연동에 사용하는 설정 상수.
 *
 * - 모델 경로(assets → filesDir)
 * - 추론 기본값(temperature, maxTokens, stop 등)
 * - 실패 시 폴백 메시지
 */
object LlamaConfig {

    /** assets 내 GGUF 모델 상대 경로 (app/src/main/assets/models/qwen2.5-1.5b-instruct-q4_k_m.gguf) */
    const val ASSET_MODEL_PATH = "models/qwen2.5-1.5b-instruct-q4_k_m.gguf"

    /** filesDir 내 모델 디렉터리명 */
    const val LOCAL_MODEL_DIR = "models"

    /** 복사된 GGUF 파일명 (assets와 동일) */
    const val LOCAL_MODEL_NAME = "qwen2.5-1.5b-instruct-q4_k_m.gguf"

    /** 초기화/추론 실패 시 반환할 기본 메시지 */
    const val FALLBACK_MESSAGE = "분석 실패"

    /** Android에서 GPU 레이어 비활성화(CPU 전용) 권장값 */
    const val GPU_LAYERS_ANDROID = 0

    // --- 추론 기본값 (Qwen 2.5 ChatML) ---

    const val DEFAULT_TEMPERATURE = 0.7f
    const val DEFAULT_MAX_TOKENS = 256
    const val DEFAULT_PENALIZE_NL = true

    /** Qwen ChatML 종료 토큰 (assistant 응답 종료 표시) */
    const val STOP_STRING_IM_END = "<|im_end|>"
}
