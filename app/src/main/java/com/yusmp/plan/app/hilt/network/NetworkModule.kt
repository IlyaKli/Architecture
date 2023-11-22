package com.yusmp.plan.app.hilt.network

import android.content.Context
import com.yusmp.plan.BuildConfig
import com.yusmp.data.net.common.Network
import com.yusmp.domain.auth.ClearSessionUseCase
import com.yusmp.domain.auth.GetCurrentSessionUseCase
import com.yusmp.domain.auth.LogoutUseCase
import com.yusmp.domain.auth.UpdateTokensUseCase
import com.yusmp.domain.dataStore.BlockingGetBaseUrlUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun isDebugEnvironment(): Boolean = BuildConfig.DEBUG

    @Singleton
    @Provides
    fun provideJson() = Network.appJson

    @Singleton
    @Provides
    @BaseUrl
    fun provideBaseUrl(blockingGetBaseUrlUseCase: BlockingGetBaseUrlUseCase) = blockingGetBaseUrlUseCase(Unit)

    @Provides
    @Singleton
    fun provideJsonFactory(json: Json): Converter.Factory = Network.getJsonFactory(json)

    @Provides
    @Singleton
    fun provideOkhttpCache() = Network.okHttpCache

    @Provides
    @Singleton
    @LoggingInterceptor
    fun provideLoggingInterceptor(
        isDebugEnvironment: Boolean
    ): Interceptor? = Network.getLoggingInterceptor(isDebugEnvironment)

    @Provides
    @Singleton
    @ChuckerInterceptor
    fun provideChuckerInterceptor(
        isDebugEnvironment: Boolean,
        @ApplicationContext context: Context
    ): Interceptor? = Network.getChuckerInterceptor(isDebugEnvironment, context)

    @Provides
    @Singleton
    @TokenAuthenticator
    fun provideAuthenticator(
        appScope: CoroutineScope,
        getCurrentSessionUseCase: GetCurrentSessionUseCase,
        updateTokensUseCase: UpdateTokensUseCase,
        clearUserDataUseCase: ClearSessionUseCase,
        @BaseUrl
        baseUrl: String,
        serializer: Json,
        isDebugEnvironment: Boolean
    ): Authenticator = Network.getTokenAuthenticator(
        appScope = appScope,
        getCurrentSessionUseCase = getCurrentSessionUseCase,
        updateTokensUseCase = updateTokensUseCase,
        clearUserDataUseCase = clearUserDataUseCase,
        baseUrl = baseUrl,
        serializer = serializer,
        isDebugEnvironment = isDebugEnvironment
    )

    @Provides
    @Singleton
    @HeadersInterceptor
    fun provideHeadersInterceptor(
        getCurrentSessionUseCase: GetCurrentSessionUseCase,
    ): Interceptor = Network.getHeadersInterceptor(
        getCurrentSessionUseCase = getCurrentSessionUseCase,
    )

    @Provides
    @Singleton
    @StatusCodeInterceptor
    fun provideStatusCodeInterceptor(
        appScope: CoroutineScope,
        logoutUseCase: LogoutUseCase,
        deserializer: Json,
    ): Interceptor = Network.getStatusCodeInterceptor(
        appScope = appScope,
        logoutUseCase = logoutUseCase,
        deserializer = deserializer,
    )

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        @LoggingInterceptor loggingInterceptor: Interceptor?,
        @ChuckerInterceptor chuckerInterceptor: Interceptor?,
        @HeadersInterceptor headersInterceptor: Interceptor,
        @StatusCodeInterceptor statusCodeInterceptor: Interceptor,
        @TokenAuthenticator tokenAuthenticator: Authenticator,
    ): OkHttpClient = Network.getHttpClient(
        interceptors = listOfNotNull(headersInterceptor, statusCodeInterceptor, loggingInterceptor, chuckerInterceptor),
        authenticator = tokenAuthenticator,
        cache = cache,
    )

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        converter: Converter.Factory,
        blockingGetBaseUrlUseCase: BlockingGetBaseUrlUseCase,
    ): Retrofit = Network.getRetrofit(
        client = client,
        blockingGetBaseUrlUseCase = blockingGetBaseUrlUseCase,
        converterFactory = converter,
    )
}