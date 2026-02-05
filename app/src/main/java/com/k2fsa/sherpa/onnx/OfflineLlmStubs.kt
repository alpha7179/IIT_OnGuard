package com.k2fsa.sherpa.onnx

/**
 * Minimal stub implementation of the sherpa-onnx OfflineLlm API.
 *
 * This exists so that OnGuard can compile and run even when the real
 * sherpa-onnx Android bindings and native libraries are not present.
 *
 * When you integrate the real sherpa-onnx Android module:
 * - Remove this file
 * - Add the official Java/Kotlin bindings from the sherpa-onnx Android sample
 * - Add the native libraries (libonnxruntime.so, libsherpa-onnx-jni.so)
 *   under app/src/main/jniLibs/<abi>/
 *
 * The API surface is intentionally limited to what OnGuard uses:
 * - OfflineLlmConfig.builder().setModel(String).setTokenizer(String).build()
 * - OfflineLlm(config: OfflineLlmConfig)
 * - OfflineLlm.generate(prompt: String): String
 * - OfflineLlm.release()
 */
class OfflineLlmConfig private constructor(
    val model: String,
    val tokenizer: String,
) {

    class Builder {
        private var model: String = ""
        private var tokenizer: String = ""

        fun setModel(model: String): Builder {
            this.model = model
            return this
        }

        fun setTokenizer(tokenizer: String): Builder {
            this.tokenizer = tokenizer
            return this
        }

        fun build(): OfflineLlmConfig {
            require(model.isNotBlank()) { "model path must not be blank" }
            require(tokenizer.isNotBlank()) { "tokenizer path must not be blank" }
            return OfflineLlmConfig(
                model = model,
                tokenizer = tokenizer,
            )
        }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}

/**
 * Stub implementation of OfflineLlm.
 *
 * The real implementation should:
 * - Load the sherpa-onnx native libraries (e.g., via System.loadLibrary)
 * - Use JNI to call into libsherpa-onnx-jni.so
 *
 * This stub only returns a placeholder message so that higher-level
 * components (like SherpaPhishingAnalyzer) can be developed and tested
 * without the heavy native dependency.
 */
class OfflineLlm(
    @Suppress("UNUSED_PARAMETER")
    private val config: OfflineLlmConfig,
) {

    /**
     * Generate a response for the given prompt.
     *
     * In this stub implementation, we explicitly indicate that the
     * real LLM is not available.
     */
    fun generate(prompt: String): String {
        return buildString {
            append("[LLM 비활성화 상태] 실제 sherpa-onnx 라이브러리가 연결되지 않았습니다.")
            append(" promptLength=")
            append(prompt.length)
        }
    }

    /**
     * Release any resources.
     *
     * The stub has nothing to release, but the method is kept so that
     * call sites match the real API.
     */
    fun release() {
        // no-op in stub
    }
}

