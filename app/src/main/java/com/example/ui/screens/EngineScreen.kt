package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GeneratedContentBundle
import com.example.data.MasterPromptConfig
import com.example.data.VaultRepository
import com.example.service.ContentGenerator
import com.example.ui.components.EditMasterPromptDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EngineScreen() {
    val masterPrompt by VaultRepository.masterPrompt.collectAsState()
    val activePages = VaultRepository.getActiveTargetPages()

    var topicInput by remember { mutableStateOf("Prehistoric Titans: Spinosaurus Semi-Aquatic Discoveries") }
    var isGenerating by remember { mutableStateOf(false) }
    var currentBundle by remember { mutableStateOf<GeneratedContentBundle?>(null) }
    var showEditPromptDialog by remember { mutableStateOf(false) }
    var selectedResultTab by remember { mutableIntStateOf(0) }
    var isBroadcastSuccess by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val presetTopics = listOf(
        "🦖 Dinosaur Fossil Breakthroughs",
        "🏛️ Ancient Roman Concrete Secrets",
        "✨ James Webb Space Discoveries",
        "⚡ Quantum Supercomputing Frontiers",
        "🌊 Deep Ocean Bioluminescence"
    )

    if (showEditPromptDialog) {
        EditMasterPromptDialog(
            config = masterPrompt,
            onSave = { updated ->
                VaultRepository.updateMasterPrompt(updated)
                showEditPromptDialog = false
                Toast.makeText(context, "Master Prompt Guidelines Updated", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showEditPromptDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_engine")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            MasterPromptSummaryCard(
                config = masterPrompt,
                onEditClick = { showEditPromptDialog = true }
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_topic_input"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Dynamic Topic & Creative Trigger",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Quick Preset Topics:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        presetTopics.forEach { preset ->
                            val cleanPreset = preset.substringAfter(" ")
                            FilterChip(
                                selected = topicInput.contains(cleanPreset.take(8)),
                                onClick = { topicInput = cleanPreset },
                                label = { Text(preset, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = {
                            topicInput = it
                            isBroadcastSuccess = false
                        },
                        label = { Text("Enter Dynamic Topic / Subject") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_dynamic_topic"),
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isGenerating = true
                                isBroadcastSuccess = false
                                try {
                                    val targetNames = activePages.map { it.name }
                                    val bundle = ContentGenerator.generate(
                                        topic = topicInput,
                                        config = masterPrompt,
                                        targetPageNames = targetNames
                                    )
                                    currentBundle = bundle
                                } finally {
                                    isGenerating = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_generate_content"),
                        enabled = !isGenerating && topicInput.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Synthesizing with Gemini & Imagen...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Generate AI Content Bundle")
                        }
                    }
                }
            }
        }

        // Generated Results Card
        currentBundle?.let { bundle ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_generation_results"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Generated Creative Package",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    "Ready to Post",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        TabRow(
                            selectedTabIndex = selectedResultTab,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Tab(
                                selected = selectedResultTab == 0,
                                onClick = { selectedResultTab = 0 },
                                text = { Text("1. Caption", fontSize = 12.sp) }
                            )
                            Tab(
                                selected = selectedResultTab == 1,
                                onClick = { selectedResultTab = 1 },
                                text = { Text("2. Imagen", fontSize = 12.sp) }
                            )
                            Tab(
                                selected = selectedResultTab == 2,
                                onClick = { selectedResultTab = 2 },
                                text = { Text("3. Meta API", fontSize = 12.sp) }
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        when (selectedResultTab) {
                            0 -> {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "SOCIAL MEDIA CAPTION (BRAND ALIGNED)",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(bundle.caption))
                                                Toast.makeText(context, "Caption Copied", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = bundle.caption,
                                            style = MaterialTheme.typography.bodySmall,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                            1 -> {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                "IMAGEN PROMPT SPECIFICATION",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(bundle.imagenPrompt))
                                                Toast.makeText(context, "Imagen Prompt Copied", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = bundle.imagenPrompt,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                color = Color(0xFFA5B4FC)
                                            )
                                        )
                                    }
                                }
                            }
                            2 -> {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Http, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF10B981))
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "GRAPH API PACKAGED POST PAYLOAD",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF10B981)
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = "${bundle.graphApiEndpoint}\n\nPayload:\n${bundle.graphApiPayloadJson}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                color = Color(0xFF67E8F9)
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(Modifier.height(12.dp))

                        // Broadcast Action
                        Button(
                            onClick = {
                                VaultRepository.publishGeneratedBundle(bundle)
                                isBroadcastSuccess = true
                                Toast.makeText(
                                    context,
                                    "Simulated Graph API Post Broadcasted to ${activePages.size} Facebook Pages!",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_broadcast_post"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBroadcastSuccess) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isBroadcastSuccess) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Published to ${activePages.size} Pages! (View in Logs)")
                            } else {
                                Icon(Icons.Default.Send, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Publish to ${activePages.size} Armed Pages (Simulate Graph API)")
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun MasterPromptSummaryCard(
    config: MasterPromptConfig,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_master_prompt_summary"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            "Master Prompt & Brand Engine",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Brand: ${config.brandName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.testTag("btn_edit_master_prompt")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Edit Rules", fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(10.dp))

            PromptRuleItem(label = "Tone", value = config.tone)
            Spacer(Modifier.height(4.dp))
            PromptRuleItem(label = "Language & Format", value = config.languageRule)
            Spacer(Modifier.height(4.dp))
            PromptRuleItem(label = "CTA Rule", value = config.ctaRule)
            Spacer(Modifier.height(4.dp))
            PromptRuleItem(label = "Imagen Spec", value = config.imagenStyle)
        }
    }
}

@Composable
fun PromptRuleItem(label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            "$label: ",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2
        )
    }
}
