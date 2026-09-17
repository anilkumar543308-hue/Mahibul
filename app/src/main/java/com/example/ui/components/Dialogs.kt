package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MasterPromptConfig
import com.example.data.PostLogItem

@Composable
fun GraphApiInspectorDialog(
    item: PostLogItem,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_graph_inspector"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text("Meta Graph API Inspector", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Target: ${item.pageName} (ID: ${item.pageId})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Post ID: ${item.postId}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    "Request Payload",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A0F1D), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = item.graphApiRequestDump,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF7DD3FC)
                        )
                    )
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    "Simulated Meta Edge Response (HTTP ${item.httpCode})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (item.httpCode == 200) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A0F1D), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = item.graphApiResponseDump,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = if (item.httpCode == 200) Color(0xFF6EE7B7) else Color(0xFFFCA5A5)
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_close_inspector")
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    item: PostLogItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_delete_post"),
        icon = {
            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        },
        title = {
            Text("Delete Post from Facebook Page?", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("This action will trigger an automated simulated Meta Graph API call:")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        "DELETE https://graph.facebook.com/v21.0/${item.postId}\nAuthorization: Bearer [PAGE_TOKEN]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFFFCA5A5)
                    )
                }
                Text(
                    "The post will be marked as DELETED and removed from the active publishing queue on '${item.pageName}'.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.testTag("btn_confirm_delete")
            ) {
                Text("Delete Post")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditMasterPromptDialog(
    config: MasterPromptConfig,
    onSave: (MasterPromptConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var brandName by remember { mutableStateOf(config.brandName) }
    var guidelines by remember { mutableStateOf(config.brandGuidelines) }
    var tone by remember { mutableStateOf(config.tone) }
    var languageRule by remember { mutableStateOf(config.languageRule) }
    var ctaRule by remember { mutableStateOf(config.ctaRule) }
    var imagenStyle by remember { mutableStateOf(config.imagenStyle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_edit_master_prompt"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text("Master Prompt Configuration", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = brandName,
                    onValueChange = { brandName = it },
                    label = { Text("Brand Name / Network") },
                    modifier = Modifier.fillMaxWidth().testTag("input_brand_name")
                )
                OutlinedTextField(
                    value = tone,
                    onValueChange = { tone = it },
                    label = { Text("Tone of Voice") },
                    modifier = Modifier.fillMaxWidth().testTag("input_tone")
                )
                OutlinedTextField(
                    value = guidelines,
                    onValueChange = { guidelines = it },
                    label = { Text("Brand Guidelines & Persona") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("input_guidelines")
                )
                OutlinedTextField(
                    value = languageRule,
                    onValueChange = { languageRule = it },
                    label = { Text("Bilingual / Language Rule") },
                    modifier = Modifier.fillMaxWidth().testTag("input_language_rule")
                )
                OutlinedTextField(
                    value = ctaRule,
                    onValueChange = { ctaRule = it },
                    label = { Text("Call to Action (CTA)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_cta_rule")
                )
                OutlinedTextField(
                    value = imagenStyle,
                    onValueChange = { imagenStyle = it },
                    label = { Text("Imagen Visual Rendering Style") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("input_imagen_style")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        config.copy(
                            brandName = brandName,
                            brandGuidelines = guidelines,
                            tone = tone,
                            languageRule = languageRule,
                            ctaRule = ctaRule,
                            imagenStyle = imagenStyle
                        )
                    )
                },
                modifier = Modifier.testTag("btn_save_master_prompt")
            ) {
                Text("Save Guidelines")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
