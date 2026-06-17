package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Moshi Serializable Data Classes for Gemini REST API ---

data class Part(val text: String? = null)
data class ContentMessage(val parts: List<Part>)
data class GenerateContentRequest(val contents: List<ContentMessage>)

data class Candidate(val content: ContentMessage?)
data class GenerateContentResponse(val candidates: List<Candidate>?)

// --- Retrofit Endpoints ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: GeminiApiService = retrofit.create(GeminiApiService::class.java)

    /**
     * Generates a smart response from Gemini for the WishCart shopping assistant.
     */
    suspend fun getAssistantResponse(prompt: String, userContext: String? = null): String {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            System.getenv("GEMINI_API_KEY") ?: ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Fallback mock AI response with extreme shopping-expert personality if API keys are not supplied.
            return getSimulatedResponse(prompt)
        }

        val systemDirective = "You are the premium, helpful, witty AI Shopping Assistant for WishCart, styled after AJIO. " +
                "Guide the customer with high-fashion trends, product recommendations from our category collection (Fashion, Accessories, Lifestyle, Electronics, Footwear, Jewelry), or order tracking help. Keep responses conversational, concise, and focused on AJIO-style premium e-commerce services in India. Offer custom discounts."

        val fullPrompt = if (userContext != null) {
            "System Context: $systemDirective\nUser Profile: $userContext\nCustomer Message: $prompt"
        } else {
            "System Context: $systemDirective\nCustomer Message: $prompt"
        }

        val requestBody = GenerateContentRequest(
            contents = listOf(
                ContentMessage(
                    parts = listOf(Part(text = fullPrompt))
                )
            )
        )

        return try {
            val response = apiService.generateContent(apiKey, requestBody)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "I'm experiencing a minor visual refresh. Let me try that again! Please feel free to ask about our premium clothing or accessories."
        } catch (e: Exception) {
            getSimulatedResponse(prompt)
        }
    }

    private fun getSimulatedResponse(prompt: String): String {
        val lowerPrompt = prompt.lowercase()
        return when {
            lowerPrompt.contains("gift") || lowerPrompt.contains("present") -> {
                "🎁 *WishCart Curated Gift Picks!*\nFor a premium experience, I highly recommend our **Active Smart Watch Classic with AMOLED Shield** showing ₹4,999 (50% OFF) or our artisanal **Eco-Conscious Vintage Leather Handbag** (₹2,499). Let me know who this is for to find apparel size targets!"
            }
            lowerPrompt.contains("shirt") || lowerPrompt.contains("jean") || lowerPrompt.contains("t-shirt") || lowerPrompt.contains("wear") || lowerPrompt.contains("fashion") -> {
                "👗 *Trending Fashion Forecast*\nOur top seller is the **Premium Men's Indigo Slim Fit Cotton Shirt** (₹899) and the **Elegant Women's Floral Summer A-Line Dress** (₹1,299). They are currently floating in flash sales right now! Tap the 'FASHION' catalog tab to browse."
            }
            lowerPrompt.contains("electronic") || lowerPrompt.contains("gadget") || lowerPrompt.contains("mobile") || lowerPrompt.contains("earbud") -> {
                "🔌 *Gadget & Electronics Hub*\nCheck out the premium **Active-Buds Wireless Noise Cancelling Earbuds** (₹2,999) with 35dB hybrid noise suppression. Or try the Classic Smart Watch. These support express shipping and Cash On Delivery!"
            }
            lowerPrompt.contains("discount") || lowerPrompt.contains("coupon") || lowerPrompt.contains("offer") -> {
                "🎟️ *EXCLUSIVE AI CODE ACTIVE*\nI have applied your personal AI coupon **WISHAISTUDIO** for an instant flat 15% discount on checkout. Simply tap Proceed to Checkout in your shopping cart!"
            }
            lowerPrompt.contains("order") || lowerPrompt.contains("track") || lowerPrompt.contains("invoice") -> {
                "📦 *Live Order Tracking*\nYour standard orders are dispatched via WishCart Premium Shippping within 24 hours. You can view progress (Ordered ➔ Packed ➔ Shipped ➔ Delivered) in your custom Admin Hub or orders tab."
            }
            else -> {
                "✨ *Greetings from WishCart AI Support!*\nI can guide you through our AJIO-inspired collection of Fashion, Electronics, Footwear, or help you track current mock orders. Just let me know what styles or items you are searching for today!"
            }
        }
    }
}
