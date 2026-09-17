package com.example.data

enum class LogStatus {
    SUCCESS,
    FAILED,
    DELETED
}

data class FacebookPage(
    val id: String,
    val accountId: String,
    val name: String,
    val category: String,
    val followers: Int,
    val pageAccessTokenMasked: String,
    val isTargetEnabled: Boolean = true
)

data class FacebookAccount(
    val id: String,
    val name: String,
    val email: String,
    val badge: String,
    val avatarInitials: String,
    val avatarColor: Long,
    val pages: List<FacebookPage>
)

data class MasterPromptConfig(
    val brandName: String = "SocialPulse Pro Network",
    val brandGuidelines: String = "High retention curiosity hooks, 3 bulleted fascinating revelations, authoritative yet accessible tone, zero fluff, clean bilingual English/Spanish conclusion, strong community call to action.",
    val tone: String = "Authoritative & Captivating",
    val languageRule: String = "Primary English with localized universal appeal",
    val ctaRule: String = "Engage audience with an open question + 4 high-relevance hashtags",
    val imagenStyle: String = "Cinematic 8K, photorealistic, dramatic studio volumetric lighting, Hasselblad 50mm f/1.8 lens, high-detail texture"
)

data class GeneratedContentBundle(
    val id: String,
    val topic: String,
    val caption: String,
    val imagenPrompt: String,
    val graphApiEndpoint: String,
    val graphApiPayloadJson: String,
    val graphApiResponsePreview: String,
    val targetPageNames: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

data class ScheduledJob(
    val id: String,
    val topic: String,
    val intervalLabel: String,
    val nextRunTime: String,
    val targetPagesCount: Int,
    val isRunning: Boolean = true
)

data class PostLogItem(
    val id: String,
    val postId: String,
    val pageName: String,
    val pageId: String,
    val accountName: String,
    val topic: String,
    val caption: String,
    val imagenPrompt: String,
    val status: LogStatus,
    val httpCode: Int,
    val timestampFormatted: String,
    val graphApiRequestDump: String,
    val graphApiResponseDump: String
)
