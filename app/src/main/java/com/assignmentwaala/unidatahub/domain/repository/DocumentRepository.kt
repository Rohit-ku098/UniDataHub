package com.assignmentwaala.unidatahub.domain.repository

import com.assignmentwaala.unidatahub.common.ResultState
import com.assignmentwaala.unidatahub.domain.models.CategoryModel
import com.assignmentwaala.unidatahub.domain.models.DocumentModel
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun getDocuments(category: String): Flow<ResultState<List<DocumentModel>>>
    fun addDocument(document: DocumentModel) : Flow<ResultState<DocumentModel>>
    fun getCategories(): Flow<ResultState<List<CategoryModel>>>
}