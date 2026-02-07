package com.onguard.domain.repository

import com.onguard.data.remote.dto.PoliceFraudResponse

/**
 * 경찰청 사기계좌 조회 Repository 인터페이스
 *
 * 경찰청 API를 통해 계좌번호의 사기 신고 이력을 조회합니다.
 *
 * 구현체는 LRU 캐시를 포함하여 중복 API 호출을 방지합니다.
 */
interface PoliceFraudRepository {

    /**
     * 계좌번호의 사기 신고 이력 조회
     *
     * @param accountNumber 조회할 계좌번호 (하이픈 포함/미포함 모두 가능)
     * @return Result<PoliceFraudResponse> 조회 결과 또는 실패
     *
     * 캐시 전략:
     * - LRU Cache: 100개 항목
     * - TTL: 15분
     * - API 실패 시 빈 결과 반환 (fail-safe)
     */
    suspend fun searchAccount(accountNumber: String): Result<PoliceFraudResponse>

    /**
     * 캐시 초기화
     */
    fun clearCache()

    /**
     * 현재 캐시된 항목 수
     */
    fun getCacheSize(): Int
}
