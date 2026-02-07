package com.onguard.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * 경찰청 사기계좌 조회 API 응답 DTO
 *
 * API: https://www.police.go.kr/www/security/cyber/cyber04.jsp
 *
 * 응답 예시:
 * ```json
 * {
 *   "ipAdres": null,
 *   "paramMap": {},
 *   "result": true,
 *   "value": [{"result": "OK", "count": "0"}],
 *   "message": ""
 * }
 * ```
 */
data class PoliceFraudResponse(
    /** API 호출 성공 여부 */
    @SerializedName("result")
    val success: Boolean = false,

    /** 조회 결과 값 배열 */
    @SerializedName("value")
    val value: List<PoliceFraudValue>? = null,

    /** 오류 메시지 (실패 시) */
    @SerializedName("message")
    val message: String? = null,

    /** IP 주소 (사용 안 함) */
    @SerializedName("ipAdres")
    val ipAddress: String? = null
)

/**
 * 사기계좌 조회 결과 상세
 */
data class PoliceFraudValue(
    /** 조회 결과 상태 (OK = 성공) */
    @SerializedName("result")
    val result: String? = null,

    /** 사기 신고 건수 (문자열로 반환됨) */
    @SerializedName("count")
    val count: String? = null
) {
    /** 신고 건수를 Int로 변환 (파싱 실패 시 0) */
    val fraudCount: Int
        get() = count?.toIntOrNull() ?: 0

    /** 사기 신고 이력 존재 여부 */
    val hasFraudHistory: Boolean
        get() = fraudCount > 0
}
