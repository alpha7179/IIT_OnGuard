package com.onguard.data.remote.api

import com.onguard.data.remote.dto.PoliceFraudResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * 경찰청 사기계좌 조회 API
 *
 * 계좌번호의 사기 신고 이력을 조회합니다.
 *
 * Base URL: https://www.police.go.kr/
 * Endpoint: user/cyber/fraud.do
 *
 * 사용 흐름:
 * 1. initSession() - GET으로 페이지 로드하여 세션 쿠키 획득 (선택사항)
 * 2. searchAccount() - POST로 계좌번호 조회 (form-urlencoded)
 *
 * 요청 형식:
 * - key: P (피해신고 조회)
 * - no: 계좌번호 (하이픈 제거)
 * - ftype: A (계좌 유형)
 *
 * 응답 형식:
 * {"result":true,"value":[{"result":"OK","count":"0"}],"message":""}
 * - count < 3: 3건 미만 신고 (위험도 낮음)
 * - count >= 3: 3건 이상 신고 (위험도 높음)
 */
interface PoliceFraudApi {

    /**
     * 세션 초기화 (페이지 로드하여 세션 쿠키 획득)
     *
     * CookieJar가 자동으로 Set-Cookie 헤더에서 쿠키를 저장합니다.
     * 참고: 실제 API 호출 시 세션 없이도 동작하지만, 안정성을 위해 유지
     *
     * @return Response<ResponseBody> HTML 페이지 (무시)
     */
    @GET("www/security/cyber/cyber04.jsp")
    suspend fun initSession(): Response<ResponseBody>

    /**
     * 계좌번호 사기 신고 이력 조회
     *
     * @param key 조회 유형 (P = 피해신고 조회)
     * @param no 조회할 계좌번호 (하이픈 없이)
     * @param ftype 형식 유형 (A = 계좌)
     * @return PoliceFraudResponse 조회 결과
     */
    @FormUrlEncoded
    @POST("user/cyber/fraud.do")
    suspend fun searchAccount(
        @Field("key") key: String = "P",
        @Field("no") no: String,
        @Field("ftype") ftype: String = "A"
    ): PoliceFraudResponse
}
