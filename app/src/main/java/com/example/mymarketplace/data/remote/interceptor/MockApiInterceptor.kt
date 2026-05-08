package com.example.mymarketplace.data.remote.interceptor

import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

class MockApiInterceptor : Interceptor {
    private val gson = Gson()
    private val mockListings = MutableList(200) { i ->
        mapOf(
            "id" to "id_$i",
            "title" to "Listing #$i",
            "description" to "Description for item $i",
            "price" to (10.0 + i),
            "imageUrl" to "https://picsum.photos/seed/$i/320/240",
            "isFavorite" to false,
            "updatedAt" to System.currentTimeMillis()
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        return when {
            path.endsWith("/listings") && method == "GET" -> {
                respond(200, mapOf("listings" to mockListings))
            }
            path.endsWith("/listings") && method == "POST" -> {
                val buffer = Buffer()
                request.body?.writeTo(buffer)
                val bodyMap = try {
                    @Suppress("UNCHECKED_CAST")
                    gson.fromJson(buffer.readUtf8(), Map::class.java) as Map<String, Any>
                } catch (_: Exception) {
                    emptyMap()
                }
                val newListing = mapOf(
                    "id" to "created_${System.currentTimeMillis()}",
                    "title" to (bodyMap["title"] ?: ""),
                    "description" to (bodyMap["description"] ?: ""),
                    "price" to (bodyMap["price"] ?: 0.0),
                    "imageUrl" to bodyMap["imageUrl"],
                    "isFavorite" to false,
                    "updatedAt" to System.currentTimeMillis()
                )
                respond(200, newListing)
            }
            path.contains("/listings/") && method == "PUT" -> {
                val id = path.substringAfterLast("/")
                respond(200, mockListings.find { it["id"] == id } ?: mockListings.first())
            }
            else -> chain.proceed(request)
        }
    }

    private fun respond(code: Int, data: Any): Response {
        val json = gson.toJson(data)
        return Response.Builder()
            .request(Request.Builder().url("https://api.example.com/").build())
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaTypeOrNull()))
            .build()
    }
}
