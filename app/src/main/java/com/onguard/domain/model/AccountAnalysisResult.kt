package com.onguard.domain.model

/**
 * 계좌번호 분석 결과
 *
 * 경찰청 사기계좌 조회 API 결과 및 로컬 패턴 분석 결과를 통합합니다.
 *
 * @property extractedAccounts 텍스트에서 추출된 계좌번호 목록
 * @property fraudAccounts 경찰청 DB에 사기 신고 이력이 있는 계좌번호 목록
 * @property reasons 탐지 사유 목록
 * @property riskScore 종합 위험도 점수 (0.0 ~ 1.0)
 * @property totalFraudCount 총 사기 신고 건수
 */
data class AccountAnalysisResult(
    val extractedAccounts: List<String> = emptyList(),
    val fraudAccounts: List<String> = emptyList(),
    val reasons: List<String> = emptyList(),
    val riskScore: Float = 0f,
    val totalFraudCount: Int = 0
) {
    companion object {
        /** 빈 결과 (계좌번호 없음) */
        val EMPTY = AccountAnalysisResult()

        /** API 호출 실패 시 반환할 결과 */
        fun apiError(extractedAccounts: List<String>) = AccountAnalysisResult(
            extractedAccounts = extractedAccounts,
            reasons = emptyList(),
            riskScore = 0f
        )
    }

    /** 경찰청 DB에 등록된 사기 계좌가 있는지 여부 */
    val hasFraudAccounts: Boolean
        get() = fraudAccounts.isNotEmpty()

    /** 사기 신고 이력이 있는지 여부 */
    val hasFraudHistory: Boolean
        get() = totalFraudCount > 0
}
