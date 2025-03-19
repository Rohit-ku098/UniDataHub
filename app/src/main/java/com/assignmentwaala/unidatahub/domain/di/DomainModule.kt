package com.assignmentwaala.unidatahub.domain.di

import android.app.Application
import android.util.Log
import com.assignmentwaala.unidatahub.MainActivity.Companion.TAG
import com.assignmentwaala.unidatahub.data.repository.DocumentRepositoryImpl
import com.assignmentwaala.unidatahub.domain.repository.DocumentRepository
import com.cloudinary.Cloudinary
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Provides
    @Singleton
    fun provideRepository(application: Application, firestore: FirebaseFirestore): DocumentRepository {
        Log.d(TAG, "Domain Module")
        return DocumentRepositoryImpl(application,firestore)
    }
}