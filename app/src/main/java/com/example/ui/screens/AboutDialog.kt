package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.CopyrightFooter
import com.example.ui.components.PixelSmileyCanvas
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.TagAmberBg
import com.example.ui.theme.TagAmberText
import com.example.ui.theme.TagBlueBg
import com.example.ui.theme.TagBlueText
import com.example.ui.theme.TagPurpleBg
import com.example.ui.theme.TagPurpleText

@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, outlineColor, RoundedCornerShape(22.dp))
                .testTag("about_dialog"),
            color = surfaceColor
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // "cjp sucks" in the corner as requested
                Text(
                    text = "cjp sucks",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = onSurfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TagAmberBg),
                                contentAlignment = Alignment.Center
                            ) {
                                PixelSmileyCanvas(
                                    targetDx = 0f,
                                    targetDy = 0f,
                                    isBlinking = false,
                                    isHappy = true,
                                    pixelColor = onSurface,
                                    accentColor = Color(0xFF34C759),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "About",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = onSurface
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("about_close_button")
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Made by Yash Verma Badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(TagPurpleBg)
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = AccentPink,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Made by Yash Verma",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TagPurpleText
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "A minimalist productivity suite uniting Apple Reminders, Notion Calendar, and Apple Notes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TagPurpleText.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Development status notice
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(TagAmberBg)
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "STATUS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TagAmberText,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "This app is still in development. For queries, report on the info given above.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = TagAmberText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Feedback & Business Inquiries
                    Text(
                        text = "FEEDBACK & BUSINESS INQUIRIES",
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Email Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, outlineColor, RoundedCornerShape(12.dp))
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:yashvermmaaa@gmail.com")
                                }
                                try { context.startActivity(intent) } catch (_: Exception) {}
                            }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(TagBlueBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = TagBlueText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Email",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = onSurfaceVariant
                                )
                                Text(
                                    text = "yashvermmaaa@gmail.com",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Instagram Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, outlineColor, RoundedCornerShape(12.dp))
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/ysh.vrx"))
                                try { context.startActivity(intent) } catch (_: Exception) {}
                            }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(TagPurpleBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "IG",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TagPurpleText
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Instagram",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = onSurfaceVariant
                                )
                                Text(
                                    text = "@ysh.vrx",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Prathna Patra Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, outlineColor, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "FEATURE REQUESTS",
                                style = MaterialTheme.typography.labelSmall,
                                color = onSurfaceVariant,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"if you want a changes to this app then write a prathna patra and give it to me\"",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 20.sp
                                ),
                                fontWeight = FontWeight.Medium,
                                color = onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    CopyrightFooter()

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
