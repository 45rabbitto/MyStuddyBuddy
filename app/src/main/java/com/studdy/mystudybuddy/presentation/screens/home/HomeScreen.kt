package com.studdy.mystudybuddy.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.studdy.mystudybuddy.presentation.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userName = "Alex" // Would come from DataStore

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello, $userName! 👋",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Ready to learn today?",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Navigate to profile */ }) {
                        Badge(
                            containerColor = SecondaryCoral
                        ) {
                            Text("3")
                        }
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = PrimaryPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Welcome Card with Gradient
            WelcomeCard()

            // Quick Actions Row
            QuickActionsRow(navController)

            // Today's Progress Section
            ProgressSection(uiState.todayProgress)

            // Recommended for You
            RecommendedSection(navController, uiState.recommendations)

            // Recent Documents
            RecentDocumentsSection(navController, uiState.recentDocuments)
        }
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(PrimaryPurple, SecondaryCoral)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🌟 Daily Challenge",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Column {
                    Text(
                        text = "Complete 3 quizzes today",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "+50 XP bonus reward!",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                LinearProgressIndicator(
                    progress = 0.33f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun QuickActionsRow(navController: NavController) {
    val actions = listOf(
        QuickAction("Upload", Icons.Default.Upload, PrimaryPurple) {
            navController.navigate("summary")
        },
        QuickAction("Quiz", Icons.Default.Quiz, SecondaryCoral) {
            navController.navigate("quiz")
        },
        QuickAction("Chat", Icons.Default.Chat, SecondaryTeal) {
            navController.navigate("chatbot")
        },
        QuickAction("Progress", Icons.Default.TrendingUp, SecondaryPeach) {
            navController.navigate("progress")
        }
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(actions.size) { index ->
            QuickActionButton(actions[index])
        }
    }
}

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun QuickActionButton(action: QuickAction) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = action.onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(action.color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    action.icon,
                    contentDescription = action.title,
                    tint = action.color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                action.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextGray
            )
        }
    }
}

@Composable
fun ProgressSection(progress: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Progress",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Text(
                    text = "$progress%",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = progress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = PrimaryPurple,
                trackColor = PrimaryPurple.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("⚡ XP", "1,250", SuccessGreen)
                StatItem("🔥 Streak", "7 days", SecondaryCoral)
                StatItem("📚 Materials", "12", SecondaryTeal)
            }
        }
    }
}

@Composable
fun StatItem(icon: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
    }
}

@Composable
fun RecommendedSection(navController: NavController, recommendations: List<String>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📖 Recommended for You",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            TextButton(onClick = {}) {
                Text("See All", color = PrimaryPurple)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(3) { index ->
                RecommendationCard(
                    title = "Android Basics",
                    description = "Learn fundamentals",
                    duration = "15 min",
                    level = "Beginner"
                )
            }
        }
    }
}

@Composable
fun RecommendationCard(
    title: String,
    description: String,
    duration: String,
    level: String
) {
    Card(
        modifier = Modifier.width(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryPurple.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("📘", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(description, fontSize = 12.sp, color = TextGray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("⏱️ $duration", fontSize = 11.sp, color = TextLight)
                Text("🎯 $level", fontSize = 11.sp, color = PrimaryPurple)
            }
        }
    }
}

@Composable
fun RecentDocumentsSection(navController: NavController, documents: List<String>) {
    Column {
        Text(
            text = "📄 Recent Documents",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextDark,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        documents.take(3).forEach { doc ->
            DocumentItem(doc)
        }
    }
}

@Composable
fun DocumentItem(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📄", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                Text("Last opened 2 days ago", fontSize = 12.sp, color = TextGray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextLight)
        }
    }
}

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(colors),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            content()
        }
    }
}