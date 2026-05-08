package com.example.imagegallery.detection

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.imagegallery.model.DetectionResult
import com.example.imagegallery.model.RectF
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.tasks.await

class ObjectDetectorHelper(
    private val context: Context
) {
    private val objOptions = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
        .enableMultipleObjects()
        .enableClassification()
        .build()

    private val objectDetector = ObjectDetection.getClient(objOptions)

    private val labelerOptions = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.4f)
        .build()

    private val imageLabeler = ImageLabeling.getClient(labelerOptions)

    suspend fun detect(bitmap: Bitmap): List<DetectionResult> {
        val results = mutableListOf<DetectionResult>()
        try {
            // Resize bitmap to a reasonable size for AI (max 1024px)
            val scaledBitmap = scaleBitmap(bitmap, 1024)
            val image = InputImage.fromBitmap(scaledBitmap, 0)

            Log.d("ObjectDetector", "Starting analysis on bitmap: ${scaledBitmap.width}x${scaledBitmap.height}")

            // 1. Chạy Object Detection (để lấy Bounding Box)
            val objResults = objectDetector.process(image).await()
            Log.d("ObjectDetector", "Object Detection found: ${objResults.size} objects")

            objResults.forEach { obj ->
                val label = obj.labels.firstOrNull()?.text ?: "Object"
                val score = obj.labels.firstOrNull()?.confidence ?: 1.0f
                
                results.add(
                    DetectionResult(
                        label = label,
                        score = score,
                        boundingBox = RectF(
                            left = obj.boundingBox.left.toFloat() / scaledBitmap.width,
                            top = obj.boundingBox.top.toFloat() / scaledBitmap.height,
                            right = obj.boundingBox.right.toFloat() / scaledBitmap.width,
                            bottom = obj.boundingBox.bottom.toFloat() / scaledBitmap.height
                        )
                    )
                )
            }

            // 2. Chạy Image Labeling (để lấy các nhãn chung nếu Detection bỏ sót)
            val labelResults = imageLabeler.process(image).await()
            Log.d("ObjectDetector", "Image Labeling found: ${labelResults.size} labels")

            val existingLabels = results.map { it.label.lowercase() }.toSet()
            labelResults.forEach { label ->
                if (!existingLabels.contains(label.text.lowercase())) {
                    results.add(
                        DetectionResult(
                            label = label.text,
                            score = label.confidence,
                            boundingBox = null // Nhãn chung không có khung bao
                        )
                    )
                }
            }

        } catch (e: Exception) {
            Log.e("ObjectDetector", "Error during detection", e)
        }
        return results
    }

    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxSize && height <= maxSize) return bitmap

        val aspectRatio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxSize
            newHeight = (maxSize / aspectRatio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * aspectRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
