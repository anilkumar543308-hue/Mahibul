package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LogStatus
import com.example.data.PostLogItem
import com.example.data.VaultRepository
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.GraphApiInspectorDialog

@Composable
fun LogsScreen() {
    val logs by VaultRepository.logs.collectAsState()
    var selectedFilter by remember { mutableStateOf<LogStatus?>(null) }
    var itemToInspect by remember { mutableStateOf<PostLogItem?>(null) }
    var itemToDelete by remember { mutableStateOf<PostLogItem?>(null) }

    val context = LocalContext.current

    val filteredLogs = remember(logs, selectedFilter) {
        if (selectedFilter == null) logs else logs.filter { it.status == selectedFilter }
    }

    if (itemToInspect != null) {
        GraphApiInspectorDialog(
            item = itemToInspect!!,
            onDismiss = { itemToInspect = null }
        )
    }

    if (itemToDelete != null) {
        DeleteConfirmationDialog(
            item = itemToDelete!!,
            onConfirm = {
                val success = VaultRepository.deletePost(itemToDelete!!.id)
                if (success) {
                    Toast.makeText(context, "Post deleted from Meta Graph Page edge!", Toast.LENGTH_SHORT).show()
                }
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_logs")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            LogsHeaderCard(
                totalLogs = logs.size,
                successCount = logs.count { it.status == LogStatus.SUCCESS },
                failedCount = logs.count { it.status == LogStatus.FAILED }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All Logs (${logs.size})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == LogStatus.SUCCESS,
                    onClick = { selectedFilter = LogStatus.SUCCESS },
                    label = { Text("Success (${logs.count { it.status == LogStatus.SUCCESS }})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == LogStatus.FAILED,
                    onClick = { selectedFilter = LogStatus.FAILED },
                    label = { Text("Failed (${logs.count { it.status == LogStatus.FAILED }})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == LogStatus.DELETED,
                    onClick = { selectedFilter = LogStatus.DELETED },
                    label = { Text("Deleted (${logs.count { it.status == LogStatus.DELETED }})", fontSize = 11.sp) }
                )
            }
        }

        if (filteredLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No logs matching filter",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredLogs, key = { it.id }) { logItem ->
                LogItemCard(
                    item = logItem,
                    onInspect = { itemToInspect = logItem },
                    onDelete = { itemToDelete = logItem },
                    onRetry = {
                        VaultRepository.retryPost(logItem.id)
                        Toast.makeText(context, "Retrying post publish on ${logItem.pageName}...", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun LogsHeaderCard(
    totalLogs: Int,
    successCount: Int,
    failedCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_logs_header"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            "Actionable Audit Log",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Meta Graph API Publishing History & Rollbacks",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "$successCount Success",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "HTTP 200 OK",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEF4444).copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "$failedCount Alerts",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Rate/Token Limits",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "$totalLogs Total",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Tracked Actions",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LogItemCard(
    item: PostLogItem,
    onInspect: () -> Unit,
    onDelete: () -> Unit,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("log_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Status badge & timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = item.status, httpCode = item.httpCode)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        item.pageName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    item.timestampFormatted,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                "Profile: ${item.accountName} • Topic: ${item.topic}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            // Caption preview
            Text(
                text = item.caption,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(8.dp))

            // Post ID snippet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "POST ID: ${item.postId}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onInspect,
                    modifier = Modifier.testTag("btn_inspect_${item.id}"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Inspect API", fontSize = 11.sp)
                }

                Spacer(Modifier.width(8.dp))

                if (item.status == LogStatus.FAILED) {
                    OutlinedButton(
                        onClick = onRetry,
                        modifier = Modifier.testTag("btn_retry_${item.id}"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Retry", fontSize = 11.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                }

                if (item.status != LogStatus.DELETED) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("btn_delete_${item.id}"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Delete", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: LogStatus, httpCode: Int) {
    val (bgColor, textColor, label) = when (status) {
        LogStatus.SUCCESS -> Triple(Color(0xFF10B981).copy(alpha = 0.18f), Color(0xFF10B981), "200 OK")
        LogStatus.FAILED -> Triple(Color(0xFFEF4444).copy(alpha = 0.18f), Color(0xFFEF4444), "403 ERR")
        LogStatus.DELETED -> Triple(Color(0xFF64748B).copy(alpha = 0.18f), Color(0xFF94A3B8), "DELETED")
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = 10.sp
        )
    }
}
