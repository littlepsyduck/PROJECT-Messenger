package dev.proptit.kotlinflow.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dev.proptit.kotlinflow.data.repositories.AuthRepositoryImpl
import dev.proptit.kotlinflow.data.repositories.ChatRepositoryImpl
import dev.proptit.kotlinflow.data.repositories.NotificationRepositoryImpl
import dev.proptit.kotlinflow.domain.repositories.IAuthRepository
import dev.proptit.kotlinflow.domain.repositories.IChatRepository
import dev.proptit.kotlinflow.domain.repositories.INotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): IAuthRepository = AuthRepositoryImpl(auth, firestore)

    @Provides
    @Singleton
    fun provideChatRepository(
        firestore: FirebaseFirestore
    ): IChatRepository = ChatRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideNotificationRepository(
        firestore: FirebaseFirestore,
        messaging: FirebaseMessaging
    ): INotificationRepository = NotificationRepositoryImpl(firestore, messaging)
} 