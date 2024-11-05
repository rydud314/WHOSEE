//package com.example.seesaw
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.net.Uri
//import android.provider.MediaStore
//import android.util.Log
//import com.google.api.gax.core.FixedCredentialsProvider
//import com.google.auth.oauth2.GoogleCredentials
//import com.google.cloud.vision.v1.AnnotateImageRequest
//import com.google.cloud.vision.v1.BatchAnnotateImagesResponse
//import com.google.cloud.vision.v1.Feature
//import com.google.cloud.vision.v1.Image
//import com.google.cloud.vision.v1.ImageAnnotatorClient
//import com.google.cloud.vision.v1.ImageAnnotatorSettings
//import fi.iki.elonen.NanoHTTPD
//import java.io.ByteArrayOutputStream
//import java.io.InputStream
//
//class VisonServer(private val context: Context) : NanoHTTPD(8080) {
//    override fun start() {
//        super.start()
//        Log.i(TAG, "Server started")
//
//        // 갤러리의 최근 이미지로 텍스트 감지 수행
//        processMostRecentImage()
//    }
//
//    private fun processMostRecentImage() {
//        val recentImageUri = getMostRecentImageUri()
//        if (recentImageUri != null) {
//            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, recentImageUri)
//            val detectedText = detectTextFromBitmap(bitmap)
//            Log.i(TAG, "Detected Text: \n$detectedText")
//        } else {
//            Log.e(TAG, "No recent image found in gallery")
//        }
//    }
//
//    private fun getMostRecentImageUri(): Uri? {
//        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        val projection = arrayOf(MediaStore.Images.Media._ID)
//        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
//        context.contentResolver.query(uri, projection, null, null, sortOrder).use { cursor ->
//            if (cursor != null && cursor.moveToFirst()) {
//                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
//                return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
//            }
//        }
//        return null
//    }
//
//    private fun detectTextFromBitmap(bitmap: Bitmap?): String {
//        if (bitmap == null) {
//            Log.e(TAG, "Error: Bitmap is null")
//            return "Error: No image data"
//        }
//
//        val stream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//        val byteArray = stream.toByteArray()
//        val imgBytes = com.google.protobuf.ByteString.copyFrom(byteArray)
//
////        val requests = mutableListOf<AnnotateImageRequest>()
//        val requests = mutableListOf<com.google.cloud.vision.v1.AnnotateImageRequest>()
////        val img = Image.newBuilder().setContent(imgBytes).build()
//        // 명확한 패키지 명시로 수정
//        val img = com.google.cloud.vision.v1.Image.newBuilder()
//            .setContent(imgBytes)
//            .build()
//
////        val feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build()
//        // 명확한 패키지 명시로 수정
//        val feat = com.google.cloud.vision.v1.Feature.newBuilder()
//            .setType(com.google.cloud.vision.v1.Feature.Type.TEXT_DETECTION)
//            .build()
//        val request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build()
//        requests.add(request)
//
//        val credentialsStream: InputStream = context.assets.open("whosee-438207-fa546ccff839.json")
//        val credentials = GoogleCredentials.fromStream(credentialsStream)
//        val settings = ImageAnnotatorSettings.newBuilder()
//            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
//            .build()
//
//        val client = ImageAnnotatorClient.create(settings)
//        val response: BatchAnnotateImagesResponse = client.batchAnnotateImages(requests)
//        client.close()
//
//        val resultText = response.responsesList[0].textAnnotationsList.joinToString("\n") { it.description }
//        credentialsStream.close()
//        return resultText
//    }
//
//    companion object {
//        private const val TAG = "VisionServer"
//    }
//}