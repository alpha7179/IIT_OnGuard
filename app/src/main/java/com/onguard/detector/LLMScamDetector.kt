package com.onguard.detector

import com.onguard.BuildConfig
import com.onguard.domain.model.DetectionMethod
import com.onguard.domain.model.ScamAnalysis
import com.onguard.domain.model.ScamType
import com.onguard.llm.SherpaPhishingAnalyzer
import com.onguard.util.DebugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 온디바이스 LLM 기반 스캠 탐지기.
 *
 * Sherpa-ONNX `OfflineLlm` + SmolLM2 ONNX LLM을 사용하여
 * 채팅 메시지를 분석하고, 스캠 여부·신뢰도·경고 메시지·의심 문구를 생성한다.
 * 모델 파일이 없거나 초기화 실패 시 [analyze]는 null을 반환하며, Rule-based만 사용된다.
 *
 * @param sherpaPhishingAnalyzer Sherpa-ONNX 기반 온디바이스 LLM 분석기
 */
@Singleton
class LLMScamDetector @Inject constructor(
    private val sherpaPhishingAnalyzer: SherpaPhishingAnalyzer
) {
    companion object {
        private const val TAG = "OnGuardLLM"

        // Sherpa-ONNX LLM 결과 포맷:
        // [위험도: 높음/중간/낮음] 이유 한 줄 요약.
        private const val RISK_HIGH = "높음"
        private const val RISK_MEDIUM = "중간"
        private const val RISK_LOW = "낮음"
    }

    private var isInitialized = false

    /**
     * LLM 모델을 초기화한다.
     *
     * Sherpa-ONNX 기반 LLM 모델을 초기화한다.
     * 이미 초기화된 경우 즉시 true를 반환한다.
     *
     * @return 성공 시 true, 모델 없음/예외 시 false
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        if (!BuildConfig.ENABLE_LLM) {
            DebugLog.warnLog(TAG) { "step=init skip reason=disabled" }
            return@withContext false
        }

        if (isInitialized) return@withContext true

        try {
            DebugLog.debugLog(TAG) { "step=init start" }
            val ok = sherpaPhishingAnalyzer.initModel()
            if (ok) {
                isInitialized = true
                DebugLog.debugLog(TAG) { "step=init success" }
            } else {
                DebugLog.warnLog(TAG) { "step=init failed" }
            }
            ok
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to initialize LLM", e)
            false
        }
    }

    /**
     * LLM 모델 사용 가능 여부를 반환한다.
     *
     * @return 초기화 완료 및 인스턴스 존재 시 true
     */
    fun isAvailable(): Boolean = BuildConfig.ENABLE_LLM && isInitialized

    /**
     * 주어진 텍스트를 LLM으로 분석하여 스캠 여부와 상세 결과를 반환한다.
     *
     * @param text 분석할 채팅 메시지
     * @return [ScamAnalysis] 분석 결과. 모델 미사용/빈 응답/파싱 실패 시 null
     */
    suspend fun analyze(text: String): ScamAnalysis? = withContext(Dispatchers.Default) {
        if (!isAvailable()) {
            DebugLog.warnLog(TAG) { "step=analyze skip reason=not_available" }
            return@withContext null
        }

        return@withContext try {
            val masked = DebugLog.maskText(text, maxLen = 30)
            DebugLog.debugLog(TAG) {
                "step=analyze input length=${text.length} masked=\"$masked\""
            }

            val raw = sherpaPhishingAnalyzer.analyze(text)

            if (raw.isBlank()) {
                DebugLog.warnLog(TAG) { "step=analyze empty_response" }
                null
            } else {
                parseSherpaResponse(raw, text)
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error during Sherpa LLM analysis", e)
            null
        }
    }

    /**
     * LLM이 반환한 스캠 유형 문자열을 [ScamType] enum으로 변환한다.
     *
     * @param typeString "투자사기", "중고거래사기", "정상" 등 한글 문자열
     * @return 대응되는 [ScamType]
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
     * LLM 인스턴스를 해제하고 리소스를 반환한다.
     *
     * 앱 종료 또는 탐지기 교체 시 호출 권장.
     */
    fun close() {
        sherpaPhishingAnalyzer.close()
        isInitialized = false
    }

    /**
     * LLM 응답 JSON 파싱용 내부 DTO.
     *
     * @property isScam 스캠 여부
     * @property confidence 신뢰도 0~1
     * @property scamType 한글 유형 문자열
     * @property warningMessage 사용자용 경고 문구
     * @property reasons 위험 요소 목록
     * @property suspiciousParts 의심 문구 인용 목록
     */
    /**
     * Sherpa LLM이 반환한 자연어 응답을 [ScamAnalysis]로 변환한다.
     *
     * 예상 형식: "[위험도: 높음/중간/낮음] 이유 한 줄 요약."
     */
    private fun parseSherpaResponse(response: String, originalText: String): ScamAnalysis? {
        val firstLine = response.lines().firstOrNull { it.isNotBlank() } ?: return null

        // [위험도: 높음] 이유...
        val regex = Regex("""\[위험도:\s*(높음|중간|낮음)]\s*(.*)""")
        val match = regex.find(firstLine) ?: regex.find(response)

        if (match == null) {
            val preview = DebugLog.maskText(response, maxLen = 80)
            DebugLog.warnLog(TAG) { "step=parse risk=unknown preview=\"$preview\"" }
            return null
        }

        val risk = match.groupValues[1].trim()
        val reasonText = match.groupValues[2].ifBlank { firstLine }.trim()

        val confidence = when (risk) {
            RISK_HIGH -> 0.85f
            RISK_MEDIUM -> 0.6f
            RISK_LOW -> 0.3f
            else -> 0.5f
        }.coerceIn(0f, 1f)

        val isScam = risk != RISK_LOW

        val reasons = listOf(reasonText)

        val scamType = parseScamType("$originalText $reasonText")

        val warningMessage = when (risk) {
            RISK_HIGH -> "이 메시지는 피싱/사기일 가능성이 매우 높습니다. ${reasonText}"
            RISK_MEDIUM -> "이 메시지는 피싱/사기일 가능성이 있습니다. ${reasonText}"
            else -> "일부 위험 신호가 감지되었지만 확실하지 않습니다. ${reasonText}"
        }

        val analysis = ScamAnalysis(
            isScam = isScam,
            confidence = confidence,
            reasons = reasons,
            detectedKeywords = emptyList(),
            detectionMethod = DetectionMethod.LLM,
            scamType = scamType,
            warningMessage = warningMessage,
            suspiciousParts = emptyList()
        )

        DebugLog.debugLog(TAG) {
            "step=parse risk=$risk isScam=$isScam confidence=$confidence scamType=$scamType"
        }

        return analysis
    }
}
