package org.xmsleep.app.utils

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * 全局共享的 OkHttpClient 单例
 *
 * 多个模块（AudioCacheManager、RemoteAudioLoader、WeatherService、
 * QuoteManager、FileDownloader、UpdateChecker）曾各自创建独立的 OkHttpClient，
 * 导致线程池和连接池大量重复。统一使用此单例，各模块通过 [default]
 * 或 [newBuilder] 派生定制超时配置，共享底层连接池与线程池。
 *
 * 使用方式：
 * ```kotlin
 * // 使用默认配置
 * val client = NetworkClient.default
 *
 * // 自定义超时（共享连接池）
 * val client = NetworkClient.newBuilder()
 *     .readTimeout(90, TimeUnit.SECONDS)
 *     .build()
 * ```
 */
object NetworkClient {

    /**
     * 共享基础客户端（连接超时 15s，读取超时 60s）
     */
    val default: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * 在共享连接池基础上创建 Builder，可按需覆盖超时等配置
     */
    fun newBuilder(): OkHttpClient.Builder = default.newBuilder()
}
