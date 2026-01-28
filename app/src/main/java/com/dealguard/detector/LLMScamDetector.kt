package com.dealguard.detector

import android.content.Context
import android.util.Log
import com.dealguard.domain.model.DetectionMethod
import com.dealguard.domain.model.ScamAnalysis
import com.dealguard.domain.model.ScamType
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * LLM 기반 스캠 탐지기
 *
 * MediaPipe LLM Inference API를 사용하여 Gemma 모델로
 * 채팅 메시지를 분석하고 스캠 여부와 이유를 생성합니다.
 */
@Singleton
class LLMScamDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "LLMScamDetector"
        private const val MODEL_PATH = "models/gemma-2b-it-gpu-int4.bin"
        private const val MAX_TOKENS = 512
        private const val TEMPERATURE = 0.7f
        private const val TOP_K = 40
    }

    private var llmInference: LlmInference? = null
    private val gson = Gson()
    private var isInitialized = false

    /**
     * LLM 모델 초기화
     * 앱 시작 시 한 번만 호출
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext true

        try {
            val modelFile = File(context.filesDir, MODEL_PATH)

            // assets에서 모델 파일 확인
            if (!modelFile.exists()) {
                // assets에서 복사 시도
                val assetPath = MODEL_PATH
                try {
                    context.assets.open(assetPath).use { input ->
                        modelFile.parentFile?.mkdirs()
                        modelFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    Log.d(TAG, "Model copied from assets to: ${modelFile.absolutePath}")
                } catch (e: Exception) {
                    Log.w(TAG, "Model file not found in assets: $assetPath")
                    Log.w(TAG, "LLM detection will be disabled. Please add Gemma model to assets/models/")
                    return@withContext false
                }
            }

            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelFile.absolutePath)
                .setMaxTokens(MAX_TOKENS)
                .setTemperature(TEMPERATURE)
                .setTopK(TOP_K)
                .build()

            llmInference = LlmInference.createFromOptions(context, options)
            isInitialized = true
            Log.d(TAG, "LLM initialized successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize LLM", e)
            false
        }
    }

    /**
     * 모델 사용 가능 여부 확인
     */
    fun isAvailable(): Boolean = isInitialized && llmInference != null

    /**
     * 텍스트 분석 및 스캠 탐지
     *
     * @param text 분석할 채팅 메시지
     * @return ScamAnalysis 분석 결과 (모델 미사용 시 null)
     */
    suspend fun analyze(text: String): ScamAnalysis? = withContext(Dispatchers.Default) {
        if (!isAvailable()) {
            Log.w(TAG, "LLM not available, skipping analysis")
            return@withContext null
        }

        try {
            val prompt = buildPrompt(text)
            val response = llmInference?.generateResponse(prompt)

            if (response.isNullOrBlank()) {
                Log.w(TAG, "Empty response from LLM")
                return@withContext null
            }

            parseResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error during LLM analysis", e)
            null
        }
    }

    /**
     * 스캠 탐지용 프롬프트 생성
     */
    private fun buildPrompt(text: String): String {
        return """
당신은 사기 탐지 전문가입니다. 다음 메시지를 분석하고 JSON 형식으로만 응답하세요.

[탐지 대상]
1. 투자 사기: 고수익 보장, 원금 보장, 긴급 투자 권유, 비공개 정보 제공, 코인/주식 리딩방
2. 중고거래 사기: 선입금 요구, 안전결제 우회, 급매 압박, 타 플랫폼 유도, 직거래 회피, 허위 매물

[메시지]
$text

[응답 형식 - JSON만 출력하세요]
{
  "isScam": true 또는 false,
  "confidence": 0.0부터 1.0 사이 숫자,
  "scamType": "투자사기" 또는 "중고거래사기" 또는 "피싱" 또는 "정상",
  "warningMessage": "사용자에게 보여줄 경고 메시지 (한국어, 2문장 이내)",
  "reasons": ["위험 요소 1", "위험 요소 2"],
  "suspiciousParts": ["의심되는 문구 인용"]
}
""".trimIndent()
    }

    /**
     * LLM 응답 파싱
     */
    private fun parseResponse(response: String): ScamAnalysis? {
        return try {
            // JSON 부분만 추출 (LLM이 추가 텍스트를 생성할 수 있음)
            val jsonStart = response.indexOf('{')
            val jsonEnd = response.lastIndexOf('}')

            if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) {
                Log.w(TAG, "No valid JSON found in response: $response")
                return null
            }

            val jsonString = response.substring(jsonStart, jsonEnd + 1)
            val llmResult = gson.fromJson(jsonString, LLMResponse::class.java)

            ScamAnalysis(
                isScam = llmResult.isScam,
                confidence = llmResult.confidence.coerceIn(0f, 1f),
                reasons = llmResult.reasons,
                detectedKeywords = emptyList(),
                detectionMethod = DetectionMethod.LLM,
                scamType = parseScamType(llmResult.scamType),
                warningMessage = llmResult.warningMessage,
                suspiciousParts = llmResult.suspiciousParts
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse LLM response: $response", e)
            null
        }
    }

    /**
     * 스캠 유형 문자열 → enum 변환
     */
    private fun parseScamType(typeString: String): ScamType {
        return when {
            typeString.contains("투자") -> ScamType.INVESTMENT
            typeString.contains("중고") || typeString.contains("거래") -> ScamType.USED_TRADE
            typeString.contains("피싱") -> ScamType.PHISHING
            typeString.contains("사칭") -> ScamType.IMPERSONATION
            typeString.contains("로맨스") -> ScamType.ROMANCE
            typeString.contains("대출") -> ScamType.LOAN
            typeString.contains("정상") -> ScamType.SAFE
            else -> ScamType.UNKNOWN
        }
    }

    /**
     * 리소스 해제
     */
    fun close() {
        llmInference?.close()
        llmInference = null
        isInitialized = false
    }

    /**
     * LLM 응답 JSON 파싱용 데이터 클래스
     */
    private data class LLMResponse(
        @SerializedName("isScam")
        val isScam: Boolean = false,

        @SerializedName("confidence")
        val confidence: Float = 0f,

        @SerializedName("scamType")
        val scamType: String = "정상",

        @SerializedName("warningMessage")
        val warningMessage: String = "",

        @SerializedName("reasons")
        val reasons: List<String> = emptyList(),

        @SerializedName("suspiciousParts")
        val suspiciousParts: List<String> = emptyList()
    )
}
