package com.assignmentwaala.unidatahub.data.repository

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.FileUtils
import android.util.Log
import com.assignmentwaala.unidatahub.MainActivity.Companion.TAG
import com.assignmentwaala.unidatahub.common.CATEGORY_LIST
import com.assignmentwaala.unidatahub.common.ResultState
import com.assignmentwaala.unidatahub.domain.models.CategoryModel
import com.assignmentwaala.unidatahub.domain.models.DocumentModel
import com.assignmentwaala.unidatahub.domain.repository.DocumentRepository
import com.cloudinary.Cloudinary
import com.cloudinary.ProgressCallback
import com.cloudinary.Uploader
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class DocumentRepositoryImpl @Inject constructor(
    private val context: Application, val firestore: FirebaseFirestore): DocumentRepository {

    override fun getDocuments(category: String): Flow<ResultState<List<DocumentModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            val documents = firestore.collection("documents")
                .whereEqualTo("category", category)
                .get()
                .await()
                .toObjects(DocumentModel::class.java)
            trySend(ResultState.Success(documents))
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error fetching documents: ${e.message}")
            trySend(ResultState.Error("Firestore error: ${e.message}"))
        }
        awaitClose { close() }
    }

    override fun addDocument(document: DocumentModel): Flow<ResultState<DocumentModel>> = callbackFlow {
        // Upload To Cloudinary then save metadata to Firestore
        trySend(ResultState.Loading)
        try {
            Log.d(TAG, "Document Repository document: $document")
            val filePath = getFilePathFromUri(context, Uri.parse(document.url)) ?: return@callbackFlow

            // Upload To Cloudinary
            MediaManager.get()
                .upload(filePath)
                .option("folder","UniDataHub/Documents")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d(TAG, "Upload started" )
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        Log.d(TAG, "Upload in progress")
                        trySend(ResultState.Uploading(bytes, totalBytes))
                    }

                    override fun onSuccess(
                        requestId: String?,
                        resultData: MutableMap<Any?, Any?>?
                    ) {
                        Log.d(TAG, "Upload success")
                        Log.d(TAG, "Request Id: $requestId")
                        Log.d(TAG, "Result Data: $resultData")

                        val newDoc = document.copy(url = resultData?.get("url").toString())

                        // Save metadata to Firestore
                        saveMetadataToFirestore(newDoc) { success, message ->
                            if (success) {
                                trySend(ResultState.Success(newDoc))
                            } else {
                                deleteFromCloudinary(resultData?.get("public_id").toString())
                                trySend(ResultState.Error("$message"))
                            }
                        }

                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.d(TAG, "Upload failed")
                        trySend(ResultState.Error("Upload failed: $error"))
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        Log.d(TAG, "Upload rescheduled")
                    }

                }).dispatch()

        } catch (e: Exception) {
            Log.e("Exception", "Error uploading PDF: ${e.message}")
            trySend(ResultState.Error("Error: ${e.localizedMessage}" ))
        }

        awaitClose { close() }
    }

    override fun getCategories(): Flow<ResultState<List<CategoryModel>>> = callbackFlow {
        try {
            // Create a list to store results
            val categoryCountList = mutableListOf<CategoryModel>()

            // Get all distinct categories and their counts in one go
            val querySnapshot = firestore.collection("documents")
                .get()
                .await()

            // Group by category and count
            val categoryMap = mutableMapOf<String, Int>()

            for (document in querySnapshot.documents) {
                val category = document.getString("category") ?: continue
                categoryMap[category] = (categoryMap[category] ?: 0) + 1
            }

            // Convert map to list
            for ((category, count) in categoryMap) {
                categoryCountList.add(CategoryModel(category, count))
            }

            trySend(ResultState.Success(categoryCountList))

        } catch (e: Exception) {
            println("Error getting categories: ${e.message}")
            trySend(ResultState.Error("Error getting categories: ${e.message}"))
        }

        awaitClose { close() }
    }


    private fun deleteFromCloudinary(publicId: String) {
        try {
            MediaManager.get().cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap())
        } catch (e: Exception) {
            Log.e("CloudinaryError", "Error deleting from Cloudinary: ${e.message}")
        }
    }

    private fun saveMetadataToFirestore( document: DocumentModel,onComplete: (Boolean, String?) -> Unit) {
        val metadata = mapOf(
            "title" to document.title,
            "description" to document.description,
            "category" to document.category,
            "url" to document.url,
            "author" to document.author,
            "uploadBy" to document.uploadBy,
            "date" to document.date
        )

        firestore.collection("documents")
            .add(metadata)
            .addOnSuccessListener {
                onComplete(true, "Upload successful and metadata saved")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error saving metadata: ${e.message}")
                onComplete(false, "Firestore error: ${e.message}")
            }
    }


    private fun getFilePathFromUri(context: Context, uri: Uri): String? {
        val file = File(context.cacheDir, "temp_pdf_${System.currentTimeMillis()}.pdf")
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}

