package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.AgentTask
import com.example.db.ChatMessage
import com.example.db.DigitalAsset
import com.example.ui.theme.*
import com.example.viewmodel.LilbedViewModel

@Composable
fun LilbedDashboardScreen(viewModel: LilbedViewModel) {
    var selectedTab by remember { mutableStateOf("Home") }

    // States from VM
    val isGenerating by viewModel.isGenerating.collectAsState()
    val statusMsg by viewModel.currentStatusMsg.collectAsState()
    val errorMsg by viewModel.errorMessage.collectAsState()

    // Neon Cyber Space Gradient Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LilbedDarkBg,
                        Color(0xFF04060A)
                    )
                )
            )
    ) {
        // Glowing futuristic network visual overlay (simulated with canvas line grids)
        CanvasGridOverlay()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = { LilbedTopBar(viewModel) },
            bottomBar = {
                LilbedBottomNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "tab_change"
                ) { tab ->
                    when (tab) {
                        "Home" -> HomeScreen(viewModel, onTabSelected = { selectedTab = it })
                        "AI Chat" -> ChatScreen(viewModel)
                        "Generator" -> GenerationScreen(viewModel)
                        "Agents" -> AgentsScreen(viewModel)
                        "Workspace" -> WorkspaceScreen(viewModel)
                    }
                }

                // Global generative progress overlay indicator (Glassmorphism design)
                if (isGenerating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.552f))
                            .clickable(enabled = false) {}, // Intercept clicks
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = LilbedSurface),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .padding(36.dp)
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            LilbedPrimary,
                                            LilbedSecondary
                                        )
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .widthIn(max = 480.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = LilbedPrimary,
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = statusMsg.ifBlank { "Lilbed Superconducting Core Processing..." },
                                    color = LilbedOnBg,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.SansSerif
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Automated Model Harmonizer: Active",
                                    color = LilbedPrimary.copy(alpha = 0.82f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                // Persistent System Error Notification Toast banner
                errorMsg?.let { error ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF261019)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    Color(0xFFEA3B7C),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "info",
                                    tint = Color(0xFFEA3B7C),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = error,
                                    color = Color(0xFFFFAFD2),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CanvasGridOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val gridDp = 36.dp.toPx()
                val width = size.width
                val height = size.height

                // Horizontal Grid lines
                var yPos = 0f
                while (yPos < height) {
                    drawLine(
                        color = LilbedPrimary.copy(alpha = 0.041f),
                        start = Offset(0f, yPos),
                        end = Offset(width, yPos),
                        strokeWidth = 1f
                    )
                    yPos += gridDp
                }

                // Vertical Grid lines
                var xPos = 0f
                while (xPos < width) {
                    drawLine(
                        color = LilbedPrimary.copy(alpha = 0.041f),
                        start = Offset(xPos, 0f),
                        end = Offset(xPos, height),
                        strokeWidth = 1f
                    )
                    xPos += gridDp
                }

                // Dynamic glowing ambient center highlights
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(LilbedSecondary.copy(alpha = 0.082f), Color.Transparent),
                        center = Offset(width * 0.8f, height * 0.2f),
                        radius = width * 0.6f
                    ),
                    center = Offset(width * 0.8f, height * 0.2f),
                    radius = width * 0.6f
                )
            }
    )
}

@Composable
fun LilbedTopBar(viewModel: LilbedViewModel) {
    val searchVal by viewModel.searchQuery.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Identity Brand Label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(LilbedPrimary, LilbedSecondary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cyclone,
                    contentDescription = "Logo",
                    tint = LilbedDarkBg,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "LILBED AI",
                    color = LilbedOnBg,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Global SuperAssistant OS v4.5",
                    color = LilbedPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Quick Command search field block
        OutlinedTextField(
            value = searchVal,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = {
                Text(
                    "Command search...",
                    color = LilbedOnBg.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = LilbedPrimary,
                    modifier = Modifier.size(16.dp)
                )
            },
            modifier = Modifier
                .width(180.dp)
                .height(44.dp)
                .testTag("app_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LilbedSurface.copy(alpha = 0.6f),
                unfocusedContainerColor = LilbedSurface.copy(alpha = 0.3f),
                focusedBorderColor = LilbedPrimary,
                unfocusedBorderColor = LilbedOnBg.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )
    }
}

@Composable
fun LilbedBottomNavigation(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = LilbedSurface.copy(alpha = 0.941f),
        tonalElevation = 8.dp,
        modifier = Modifier
            .navigationBarsPadding()
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LilbedOnBg.copy(alpha = 0.082f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(0.dp)
            )
    ) {
        val navItems = listOf(
            Triple("Home", Icons.Default.Home, "home_tab"),
            Triple("AI Chat", Icons.Default.ChatBubble, "chat_tab"),
            Triple("Generator", Icons.Default.AutoAwesome, "generator_tab"),
            Triple("Agents", Icons.Default.Hub, "agents_tab"),
            Triple("Workspace", Icons.Default.CloudQueue, "workspace_tab")
        )

        navItems.forEach { (name, icon, tag) ->
            val isSelected = selectedTab == name
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(name) },
                modifier = Modifier.testTag(tag),
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = if (isSelected) LilbedDarkBg else LilbedOnBg.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        name,
                        color = if (isSelected) LilbedPrimary else LilbedOnBg.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = LilbedPrimary
                )
            )
        }
    }
}

// ---------------- HOME VIEW ----------------

@Composable
fun HomeScreen(
    viewModel: LilbedViewModel,
    onTabSelected: (String) -> Unit
) {
    val searchVal by viewModel.searchQuery.collectAsState()
    val tasks by viewModel.activeAgentTasks.collectAsState()
    val assets by viewModel.generatedAssets.collectAsState()

    val filteredTasks = tasks.filter {
        it.name.contains(searchVal, ignoreCase = true) || it.description.contains(searchVal, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Futuristic Hero Banner
        item {
            FuturisticHeroBanner()
        }

        // Quick Launch Hub
        item {
            QuickLaunchHubPanel(onTabSelected)
        }

        // Operational Telemetry Metrics Board
        item {
            CyberTelemetryPanel(
                agentCount = tasks.size,
                commsPing = 42,
                satelliteBandwidth = 248,
                generatedCount = assets.size
            )
        }

        // List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ACTIVE ALIGNED NODES",
                    color = LilbedPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Satellite Edge concept live",
                    color = LilbedSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // List of Active Agent Tasks
        if (filteredTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = LilbedSurface.copy(alpha = 0.42f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No operational agent tasks matched in parameters.",
                            color = LilbedOnBg.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredTasks) { task ->
                AgentTaskRow(task)
            }
        }
    }
}

@Composable
fun FuturisticHeroBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            LilbedSecondary.copy(alpha = 0.72f),
                            Color(0xFF0D0A1B)
                        )
                    )
                )
                .border(
                    1.5.dp,
                    Brush.linearGradient(
                        colors = listOf(LilbedPrimary, LilbedSecondary)
                    ),
                    RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LilbedPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        "CORE ECOSYSTEM ONBOARD",
                        color = LilbedPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "THE SUPREMACIST AI",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Ready to materialize education, business, code & advanced space intelligence concepts securely.",
                    color = LilbedOnBg.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.AllInclusive,
                contentDescription = "Cosmic Icon",
                tint = LilbedPrimary.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun QuickLaunchHubPanel(onTabSelected: (String) -> Unit) {
    Column {
        Text(
            "SUPER ASSISTANT PLATFORMS",
            color = LilbedOnBg.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val keys = listOf(
                Pair("Core AI Chat", "AI Chat"),
                Pair("Generator Hub", "Generator"),
                Pair("Multi-Agents", "Agents"),
                Pair("Workspace Core", "Workspace")
            )
            keys.forEach { (name, target) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LilbedSurface)
                        .clickable { onTabSelected(target) }
                        .border(1.dp, LilbedOnBg.copy(alpha = 0.082f), RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name,
                        color = LilbedPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun CyberTelemetryPanel(
    agentCount: Int,
    commsPing: Int,
    satelliteBandwidth: Int,
    generatedCount: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LilbedSurface.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(LilbedPrimary.copy(alpha = 0.2f), Color.Transparent)
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "INTELLIGENT TELEMETRY MONITOR",
                color = LilbedSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TelemetryStatColumn(title = "Agents Active", value = "$agentCount Active")
                TelemetryStatColumn(title = "Comms Ping", value = "$commsPing ms")
                TelemetryStatColumn(title = "Satellite Int.", value = "Aligned")
                TelemetryStatColumn(title = "Synthesized", value = "$generatedCount Assets")
            }
        }
    }
}

@Composable
fun TelemetryStatColumn(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = LilbedOnBg.copy(alpha = 0.5f), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = LilbedPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AgentTaskRow(task: AgentTask) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LilbedSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LilbedPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (task.category.lowercase()) {
                    "research" -> Icons.Default.Search
                    "coding" -> Icons.Default.Code
                    "automation" -> Icons.Default.SettingsSuggest
                    "marketing" -> Icons.Default.TrendingUp
                    else -> Icons.Default.SupportAgent
                }
                Icon(
                    imageVector = icon,
                    contentDescription = task.category,
                    tint = LilbedPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    color = LilbedOnBg,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = task.description,
                    color = LilbedOnBg.copy(alpha = 0.72f),
                    fontSize = 11.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (task.status == "Completed") LilbedPrimary.copy(alpha = 0.15f)
                        else LilbedSecondary.copy(alpha = 0.15f)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = task.status,
                    color = if (task.status == "Completed") LilbedPrimary else LilbedSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// ---------------- ALIGNED CHAT VIEW ----------------

@Composable
fun ChatScreen(viewModel: LilbedViewModel) {
    val history by viewModel.chatHistory.collectAsState()
    var inputStr by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "LILBED CORE DIALOGUE",
                color = LilbedPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
            IconButton(
                onClick = { viewModel.clearAllChat() }
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear Chat",
                    tint = LilbedAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dialogue log
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (history.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "empty",
                            tint = LilbedPrimary.copy(alpha = 0.42f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Dialogue stream initialized. Send an advanced prompt to initiate neural reasoning.",
                            color = LilbedOnBg.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                items(history) { chat ->
                    ChatMessageCard(chat)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Message input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputStr,
                onValueChange = { inputStr = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input"),
                placeholder = {
                    Text(
                        "Formulate next cyber command...",
                        color = LilbedOnBg.copy(alpha = 0.42f),
                        fontSize = 13.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = LilbedSurface,
                    unfocusedContainerColor = LilbedSurface.copy(alpha = 0.6f),
                    focusedBorderColor = LilbedPrimary,
                    unfocusedBorderColor = LilbedOnBg.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputStr.isNotBlank()) {
                            viewModel.sendChatMessage(inputStr)
                            inputStr = ""
                        }
                    }
                ),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = {
                    if (inputStr.isNotBlank()) {
                        viewModel.sendChatMessage(inputStr)
                        inputStr = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("chat_send_btn"),
                shape = CircleShape,
                containerColor = LilbedPrimary,
                contentColor = LilbedDarkBg
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageCard(chat: ChatMessage) {
    val isUser = chat.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(LilbedPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = "Core",
                    tint = LilbedDarkBg,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) LilbedSecondary.copy(alpha = 0.15f) else LilbedSurface
            ),
            shape = RoundedCornerShape(
                topStart = if (isUser) 16.dp else 0.dp,
                topEnd = if (isUser) 0.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            modifier = Modifier
                .widthIn(max = 280.dp)
                .border(
                    width = 1.dp,
                    color = if (isUser) LilbedSecondary.copy(alpha = 0.3f) else LilbedOnBg.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(
                        topStart = if (isUser) 16.dp else 0.dp,
                        topEnd = if (isUser) 0.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = chat.content,
                    color = LilbedOnBg,
                    fontSize = 13.sp,
                    fontFamily = if (chat.content.startsWith("```")) FontFamily.Monospace else FontFamily.SansSerif
                )
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(LilbedSecondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ---------------- CREATIVE GENERATION VIEW ----------------

@Composable
fun GenerationScreen(viewModel: LilbedViewModel) {
    var prompt by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Image") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "LILBED NEURAL SYNTHESIS ENGINE",
                color = LilbedPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "Submit prompt structures to generate digital multi-media layers instantly.",
                color = LilbedOnBg.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }

        // Selection row for types
        item {
            val options = listOf("Image", "App Code", "Music", "Presentation")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(options) { opt ->
                    val isSelected = opt == selectedType
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) LilbedPrimary else LilbedSurface)
                            .clickable { selectedType = opt }
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .border(
                                1.dp,
                                if (isSelected) LilbedPrimary else LilbedOnBg.copy(alpha = 0.1f),
                                RoundedCornerShape(16.dp)
                            )
                    ) {
                        Text(
                            text = opt,
                            color = if (isSelected) LilbedDarkBg else LilbedOnBg,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Prompt input area
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LilbedSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CONCEPT DISPATCH PARAMETERS",
                        color = LilbedSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("gen_prompt_input"),
                        placeholder = {
                            Text(
                                "e.g., Ultra realistic high tech concept dashboard mapping localized satellites",
                                color = LilbedOnBg.copy(alpha = 0.42f),
                                fontSize = 12.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LilbedDarkBg,
                            unfocusedContainerColor = LilbedDarkBg,
                            focusedBorderColor = LilbedPrimary,
                            unfocusedBorderColor = LilbedOnBg.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (prompt.isNotBlank()) {
                                viewModel.triggerGeneration(prompt, selectedType)
                                prompt = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_generation_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = LilbedPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AllInclusive,
                            contentDescription = "generate",
                            tint = LilbedDarkBg,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MATERIALIZE \"$selectedType\"",
                            color = LilbedDarkBg,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

// ---------------- AGENTS WORKFLOW VIEW ----------------

@Composable
fun AgentsScreen(viewModel: LilbedViewModel) {
    var taskName by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    var taskCat by remember { mutableStateOf("Automation") }

    val categories = listOf("Automation", "Research", "Coding", "Marketing")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "LILBED AUTONOMOUS AGENT CONSOLE",
                color = LilbedPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "Instantiate independent smart multi-agent background flows targeted at custom challenges.",
                color = LilbedOnBg.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }

        // Custom task launch dialog area
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LilbedSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "DISPATCH NEW MUTATE-NODE TASK",
                        color = LilbedPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = taskName,
                        onValueChange = { taskName = it },
                        label = { Text("Task Identifier Target", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("agent_task_name_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LilbedDarkBg,
                            unfocusedContainerColor = LilbedDarkBg,
                            focusedBorderColor = LilbedPrimary,
                            unfocusedBorderColor = LilbedOnBg.copy(alpha = 0.05f)
                        )
                    )

                    OutlinedTextField(
                        value = taskDesc,
                        onValueChange = { taskDesc = it },
                        label = { Text("Operational Bounds Instructions", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("agent_task_desc_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LilbedDarkBg,
                            unfocusedContainerColor = LilbedDarkBg,
                            focusedBorderColor = LilbedPrimary,
                            unfocusedBorderColor = LilbedOnBg.copy(alpha = 0.05f)
                        )
                    )

                    // Classification category selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSel = cat == taskCat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) LilbedSecondary else LilbedDarkBg)
                                    .clickable { taskCat = cat }
                                    .padding(vertical = 8.dp, horizontal = 4.dp)
                                    .border(
                                        1.dp,
                                        if (isSel) LilbedSecondary else LilbedOnBg.copy(alpha = 0.05f),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (taskName.isNotBlank() && taskDesc.isNotBlank()) {
                                viewModel.submitCustomAgentTask(taskName, taskDesc, taskCat)
                                taskName = ""
                                taskDesc = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dispatch_agent_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = LilbedSecondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Launch, contentDescription = "dispatch", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DISPATCH AUTONOMOUS CLUSTER",
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// ---------------- SECURE WORKSPACE & CLOUD COLLAB ----------------

@Composable
fun WorkspaceScreen(viewModel: LilbedViewModel) {
    val assets by viewModel.generatedAssets.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "LILBED SECURE ACCREDITED CLOUD WORKSPACE",
                color = LilbedPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "Unified hub displaying cloud stored multi-media artifacts generated dynamically through prompts.",
                color = LilbedOnBg.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }

        if (assets.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = LilbedSurface.copy(alpha = 0.42f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "empty",
                            tint = LilbedPrimary.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Secure cloud library repository empty.",
                            color = LilbedOnBg.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Synthesize items from the Generator tab to inspect complete compiled outputs.",
                            color = LilbedOnBg.copy(alpha = 0.42f),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(assets) { asset ->
                DigitalAssetCard(asset = asset, onDelete = { viewModel.deleteAsset(asset.id) })
            }
        }
    }
}

@Composable
fun DigitalAssetCard(asset: DigitalAsset, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LilbedSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (asset.type) {
                            "Image" -> Icons.Default.Image
                            "App Code" -> Icons.Default.Code
                            "Music" -> Icons.Default.Audiotrack
                            else -> Icons.Default.Dashboard
                        },
                        contentDescription = asset.type,
                        tint = LilbedPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SYNTHESIZED ${asset.type.uppercase()}",
                        color = LilbedPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "delete",
                        tint = LilbedAccent.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Source Parameters: \"${asset.prompt}\"",
                color = LilbedOnBg.copy(alpha = 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LilbedDarkBg)
                    .border(1.dp, LilbedOnBg.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = asset.resultUrlOrBase64,
                    color = LilbedOnBg,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
