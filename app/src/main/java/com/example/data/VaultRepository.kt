package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object VaultRepository {

    private val initialAccounts = listOf(
        FacebookAccount(
            id = "fb_usr_84920193",
            name = "Alex Rivera (Primary Admin)",
            email = "alex.rivera.agency@pulsepro.meta.com",
            badge = "Verified Business Admin",
            avatarInitials = "AR",
            avatarColor = 0xFF1877F2,
            pages = listOf(
                FacebookPage("109283746192830", "fb_usr_84920193", "Apex Tech & AI Insights", "Technology & Robotics", 128400, "EAAGNO8...a7F9", true),
                FacebookPage("291038475910293", "fb_usr_84920193", "Prehistoric Worlds & Paleontology", "Science & Nature", 94200, "EAAGNO8...b2E1", true),
                FacebookPage("384759201948271", "fb_usr_84920193", "Ancient Empires & History Today", "History & Culture", 215600, "EAAGNO8...c9D4", true),
                FacebookPage("492810472910482", "fb_usr_84920193", "Creative Byte Daily", "Design & Media", 42300, "EAAGNO8...d4C7", false)
            )
        ),
        FacebookAccount(
            id = "fb_usr_49201948",
            name = "Elena Rostova (Global Ops)",
            email = "elena.rostova@socialpulse.meta.com",
            badge = "Content Partner",
            avatarInitials = "ER",
            avatarColor = 0xFF8B5CF6,
            pages = listOf(
                FacebookPage("501928374619203", "fb_usr_49201948", "Global Explorer & Wildlife", "Nature & Travel", 310500, "EAAGNO8...e1A8", true),
                FacebookPage("619283047291823", "fb_usr_49201948", "Cosmic Horizons", "Astronomy & Astrophysics", 182300, "EAAGNO8...f7B2", true),
                FacebookPage("728193048192034", "fb_usr_49201948", "Future Innovation Pulse", "Science & Futurism", 76100, "EAAGNO8...g3C5", false),
                FacebookPage("839201948201948", "fb_usr_49201948", "Epic Chronology", "Historical Documentary", 105800, "EAAGNO8...h8D9", true)
            )
        ),
        FacebookAccount(
            id = "fb_usr_77192840",
            name = "Marcus Sterling (Syndicate Media)",
            email = "m.sterling@syndicatemedia.com",
            badge = "Enterprise Partner",
            avatarInitials = "MS",
            avatarColor = 0xFF059669,
            pages = listOf(
                FacebookPage("948201948201934", "fb_usr_77192840", "Digital Nomad Chronicles", "Lifestyle & Tech", 88900, "EAAGNO8...i4E3", false),
                FacebookPage("159283048201938", "fb_usr_77192840", "Mythology & Legends", "Culture & Folklore", 145200, "EAAGNO8...j9F6", true),
                FacebookPage("268193048201947", "fb_usr_77192840", "Deep Space & Tech", "Aerospace Science", 230400, "EAAGNO8...k2G8", true),
                FacebookPage("379201948201956", "fb_usr_77192840", "Urban Pulse News", "Current Trends", 64800, "EAAGNO8...m5H1", false)
            )
        ),
        FacebookAccount(
            id = "fb_usr_62910482",
            name = "Nexus Studio (Brand Agency)",
            email = "admin@nexusstudiomedia.org",
            badge = "Agency Media Manager",
            avatarInitials = "NS",
            avatarColor = 0xFFEC4899,
            pages = listOf(
                FacebookPage("489201948201965", "fb_usr_62910482", "Nexus Studio Official", "Agency Showcase", 52100, "EAAGNO8...n1K2", false),
                FacebookPage("590201948201974", "fb_usr_62910482", "Viral Curiosity Lab", "Entertainment & Science", 412000, "EAAGNO8...p7L4", true),
                FacebookPage("601201948201983", "fb_usr_62910482", "Art & Heritage Stream", "Visual Arts", 98400, "EAAGNO8...q3M6", true),
                FacebookPage("712201948201992", "fb_usr_62910482", "The Science Frontier", "STEM Education", 175900, "EAAGNO8...r8N9", true)
            )
        )
    )

    private val initialLogs = mutableListOf(
        PostLogItem(
            id = "log_1",
            postId = "291038475910293_9821402910",
            pageName = "Prehistoric Worlds & Paleontology",
            pageId = "291038475910293",
            accountName = "Alex Rivera",
            topic = "Spinosaurus Aquatic Adaptations",
            caption = "🦖 What if everything we learned about prehistoric predators was only half the truth? Recent paleontological breakthroughs are completely rewriting how we picture apex titans of the Cretaceous...",
            imagenPrompt = "Photorealistic 8K cinematic photograph of a majestic Spinosaurus emerging from mist-shrouded Cretaceous delta waters, iridescent scaly skin textures with river droplet reflections...",
            status = LogStatus.SUCCESS,
            httpCode = 200,
            timestampFormatted = "Today at 09:42 AM",
            graphApiRequestDump = """
                POST https://graph.facebook.com/v21.0/291038475910293/photos
                Headers:
                  Authorization: Bearer EAAGNO8...b2E1
                  Content-Type: application/json
                Body:
                {
                  "url": "https://media.socialpulse.ai/renders/spino_aquatic_8k.jpg",
                  "caption": "🦖 What if everything we learned about prehistoric predators was only half the truth?...",
                  "published": true
                }
            """.trimIndent(),
            graphApiResponseDump = """
                HTTP/1.1 200 OK
                {
                  "id": "291038475910293_9821402910",
                  "post_id": "291038475910293_9821402910",
                  "published": true,
                  "reach_estimate": "34,000 - 82,000"
                }
            """.trimIndent()
        ),
        PostLogItem(
            id = "log_2",
            postId = "384759201948271_8719203810",
            pageName = "Ancient Empires & History Today",
            pageId = "384759201948271",
            accountName = "Alex Rivera",
            topic = "Self-Healing Roman Concrete",
            caption = "🏛️ The engineering marvels of the ancient world that still defy modern explanations. Centuries before industrial metallurgy, Roman architects mastered self-healing mortar...",
            imagenPrompt = "Epic cinematic reconstruction of Ancient Rome at the height of the Imperial era, marble temples with golden sunlit colonnades, intricate Roman aqueduct archways...",
            status = LogStatus.SUCCESS,
            httpCode = 200,
            timestampFormatted = "Today at 08:15 AM",
            graphApiRequestDump = """
                POST https://graph.facebook.com/v21.0/384759201948271/photos
                Headers:
                  Authorization: Bearer EAAGNO8...c9D4
                  Content-Type: application/json
                Body:
                {
                  "url": "https://media.socialpulse.ai/renders/roman_concrete_hero.jpg",
                  "caption": "🏛️ The engineering marvels of the ancient world...",
                  "published": true
                }
            """.trimIndent(),
            graphApiResponseDump = """
                HTTP/1.1 200 OK
                {
                  "id": "384759201948271_8719203810",
                  "post_id": "384759201948271_8719203810",
                  "published": true
                }
            """.trimIndent()
        ),
        PostLogItem(
            id = "log_3",
            postId = "619283047291823_7610293841",
            pageName = "Cosmic Horizons",
            pageId = "619283047291823",
            accountName = "Elena Rostova",
            topic = "James Webb Deep Field Discoveries",
            caption = "✨ Peer into the cosmic dawn: Light traveling across 13.5 billion years to reach human lenses. Astronomers analyzing ultra-deep spectroscopic surveys...",
            imagenPrompt = "Spectacular deep-space astrophotography of an ethereal stellar nebula cradle, glowing ionized hydrogen gas clouds in vivid cyan and magenta...",
            status = LogStatus.SUCCESS,
            httpCode = 200,
            timestampFormatted = "Yesterday at 11:30 PM",
            graphApiRequestDump = """
                POST https://graph.facebook.com/v21.0/619283047291823/photos
                Headers:
                  Authorization: Bearer EAAGNO8...f7B2
                Body:
                {
                  "url": "https://media.socialpulse.ai/renders/jwst_deep_field.jpg",
                  "caption": "✨ Peer into the cosmic dawn...",
                  "published": true
                }
            """.trimIndent(),
            graphApiResponseDump = """
                HTTP/1.1 200 OK
                {
                  "id": "619283047291823_7610293841",
                  "post_id": "619283047291823_7610293841"
                }
            """.trimIndent()
        ),
        PostLogItem(
            id = "log_4",
            postId = "590201948201974_6510293847",
            pageName = "Viral Curiosity Lab",
            pageId = "590201948201974",
            accountName = "Nexus Studio",
            topic = "Quantum Teleportation Records",
            caption = "⚡ Quantum entanglement shattered another distance threshold this week in orbital satellite relays...",
            imagenPrompt = "Futuristic quantum optical bench with split laser beams and glowing crystal resonators...",
            status = LogStatus.FAILED,
            httpCode = 403,
            timestampFormatted = "Yesterday at 04:12 PM",
            graphApiRequestDump = """
                POST https://graph.facebook.com/v21.0/590201948201974/photos
                Headers:
                  Authorization: Bearer EAAGNO8...p7L4
            """.trimIndent(),
            graphApiResponseDump = """
                HTTP/1.1 403 Forbidden
                {
                  "error": {
                    "message": "(#32) Page request limit reached. Rate-limited for 60 seconds.",
                    "type": "OAuthException",
                    "code": 32,
                    "fbtrace_id": "AZe810XyP"
                  }
                }
            """.trimIndent()
        )
    )

    private val initialJobs = listOf(
        ScheduledJob("job_1", "Paleontology & Prehistoric Ecosystems", "Every 30 min", "In 14 minutes", 3, true),
        ScheduledJob("job_2", "Ancient Civilizations & Lost Engineering", "Every 1 hour", "In 42 minutes", 4, true),
        ScheduledJob("job_3", "Deep Space & Astrophysics Breakthroughs", "Every 4 hours", "In 2h 15m", 3, true),
        ScheduledJob("job_4", "Next-Gen AI & Tech Frontiers", "Every 6 hours", "In 4h 00m", 2, false)
    )

    private val _accounts = MutableStateFlow(initialAccounts)
    val accounts: StateFlow<List<FacebookAccount>> = _accounts.asStateFlow()

    private val _masterPrompt = MutableStateFlow(MasterPromptConfig())
    val masterPrompt: StateFlow<MasterPromptConfig> = _masterPrompt.asStateFlow()

    private val _logs = MutableStateFlow(initialLogs.toList())
    val logs: StateFlow<List<PostLogItem>> = _logs.asStateFlow()

    private val _scheduledJobs = MutableStateFlow(initialJobs)
    val scheduledJobs: StateFlow<List<ScheduledJob>> = _scheduledJobs.asStateFlow()

    private val _isWorkerRunning = MutableStateFlow(false)
    val isWorkerRunning: StateFlow<Boolean> = _isWorkerRunning.asStateFlow()

    private val _workerLogStatus = MutableStateFlow("WORKER_IDLE")
    val workerLogStatus: StateFlow<String> = _workerLogStatus.asStateFlow()

    fun updateMasterPrompt(newConfig: MasterPromptConfig) {
        _masterPrompt.value = newConfig
    }

    fun togglePageTarget(accountId: String, pageId: String) {
        _accounts.update { list ->
            list.map { acc ->
                if (acc.id == accountId) {
                    acc.copy(pages = acc.pages.map { page ->
                        if (page.id == pageId) page.copy(isTargetEnabled = !page.isTargetEnabled) else page
                    })
                } else acc
            }
        }
    }

    fun getActiveTargetPages(): List<FacebookPage> {
        return _accounts.value.flatMap { it.pages }.filter { it.isTargetEnabled }
    }

    fun deletePost(logId: String): Boolean {
        val target = _logs.value.find { it.id == logId } ?: return false
        _logs.update { list ->
            list.map { item ->
                if (item.id == logId) {
                    item.copy(
                        status = LogStatus.DELETED,
                        httpCode = 204,
                        graphApiResponseDump = """
                            HTTP/1.1 200 OK (Deletion Confirmed)
                            DELETE https://graph.facebook.com/v21.0/${item.postId}
                            Response:
                            {
                              "success": true,
                              "message": "Post was successfully removed from Meta Graph Page edge."
                            }
                        """.trimIndent()
                    )
                } else item
            }
        }
        return true
    }

    fun retryPost(logId: String) {
        _logs.update { list ->
            list.map { item ->
                if (item.id == logId) {
                    item.copy(
                        status = LogStatus.SUCCESS,
                        httpCode = 200,
                        timestampFormatted = "Just now (Retried)",
                        graphApiResponseDump = """
                            HTTP/1.1 200 OK
                            {
                              "id": "${item.pageId}_${System.currentTimeMillis()}",
                              "status": "SUCCESS",
                              "retried": true
                            }
                        """.trimIndent()
                    )
                } else item
            }
        }
    }

    fun publishGeneratedBundle(bundle: GeneratedContentBundle) {
        val activePages = getActiveTargetPages().ifEmpty {
            listOf(_accounts.value.first().pages.first())
        }

        val currentTime = SimpleDateFormat("h:mm a", Locale.US).format(Date())
        val newLogItems = activePages.map { page ->
            val account = _accounts.value.find { it.id == page.accountId }?.name ?: "SocialPulse Admin"
            val uniquePostId = "${page.id}_${System.currentTimeMillis().toString().takeLast(8)}"
            PostLogItem(
                id = UUID.randomUUID().toString(),
                postId = uniquePostId,
                pageName = page.name,
                pageId = page.id,
                accountName = account,
                topic = bundle.topic,
                caption = bundle.caption,
                imagenPrompt = bundle.imagenPrompt,
                status = LogStatus.SUCCESS,
                httpCode = 200,
                timestampFormatted = "Just now ($currentTime)",
                graphApiRequestDump = """
                    POST https://graph.facebook.com/v21.0/${page.id}/photos
                    Headers:
                      Authorization: Bearer ${page.pageAccessTokenMasked}
                      Content-Type: application/json
                    Payload:
                    ${bundle.graphApiPayloadJson}
                """.trimIndent(),
                graphApiResponseDump = """
                    HTTP/1.1 200 OK
                    {
                      "id": "$uniquePostId",
                      "post_id": "$uniquePostId",
                      "published": true,
                      "target_page": "${page.name}"
                    }
                """.trimIndent()
            )
        }

        _logs.update { newLogItems + it }
    }

    suspend fun simulateWorkManagerExecution(jobTopic: String) {
        _isWorkerRunning.value = true
        _workerLogStatus.value = "WORKER_STARTING: Scheduling task on background thread..."
        kotlinx.coroutines.delay(800)

        _workerLogStatus.value = "CONTENT_ENGINE: Generating AI caption & Imagen prompt for '$jobTopic'..."
        val activeTargetNames = getActiveTargetPages().map { it.name }
        val bundle = com.example.service.ContentGenerator.generate(
            topic = jobTopic,
            config = _masterPrompt.value,
            targetPageNames = activeTargetNames
        )
        kotlinx.coroutines.delay(1000)

        _workerLogStatus.value = "GRAPH_API: Broadcasting to ${bundle.targetPageNames.size} Facebook Pages..."
        kotlinx.coroutines.delay(1200)

        publishGeneratedBundle(bundle)
        _workerLogStatus.value = "COMPLETED: Successfully published across targeted Meta pages."
        kotlinx.coroutines.delay(1000)

        _isWorkerRunning.value = false
        _workerLogStatus.value = "WORKER_IDLE"
    }

    fun toggleJobRunning(jobId: String) {
        _scheduledJobs.update { list ->
            list.map { if (it.id == jobId) it.copy(isRunning = !it.isRunning) else it }
        }
    }
}
