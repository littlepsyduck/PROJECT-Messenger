package dev.proptit.kotlinflow.di

import dev.proptit.kotlinflow.domain.repositories.IAuthRepository
import dev.proptit.kotlinflow.domain.repositories.IChatRepository
import dev.proptit.kotlinflow.domain.usecases.auth.LoginUseCase
import dev.proptit.kotlinflow.domain.usecases.auth.RegisterUseCase
import dev.proptit.kotlinflow.domain.usecases.auth.UpdateFCMTokenUseCase
import dev.proptit.kotlinflow.domain.usecases.chat.GetChatsUseCase
import dev.proptit.kotlinflow.domain.usecases.chat.GetMessagesUseCase
import dev.proptit.kotlinflow.domain.usecases.chat.SendMessageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: IAuthRepository
    ): LoginUseCase = LoginUseCase(authRepository)

    @Provides
    @Singleton
    fun provideRegisterUseCase(
        authRepository: IAuthRepository
    ): RegisterUseCase = RegisterUseCase(authRepository)

    @Provides
    @Singleton
    fun provideUpdateFCMTokenUseCase(
        authRepository: IAuthRepository
    ): UpdateFCMTokenUseCase = UpdateFCMTokenUseCase(authRepository)

    @Provides
    @Singleton
    fun provideSendMessageUseCase(
        chatRepository: IChatRepository
    ): SendMessageUseCase = SendMessageUseCase(chatRepository)

    @Provides
    @Singleton
    fun provideGetChatsUseCase(
        chatRepository: IChatRepository
    ): GetChatsUseCase = GetChatsUseCase(chatRepository)

    @Provides
    @Singleton
    fun provideGetMessagesUseCase(
        chatRepository: IChatRepository
    ): GetMessagesUseCase = GetMessagesUseCase(chatRepository)
} 