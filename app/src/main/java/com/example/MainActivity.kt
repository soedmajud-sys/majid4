package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.ChatMessageEntity
import com.example.data.PlatformEntity
import com.example.data.RequestEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

enum class ScreenTab {
    DASHBOARD,
    PLATFORMS,
    CHAT_AI,
    REQUESTS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel()
    
    // States from ViewModel
    val platforms by viewModel.filteredPlatforms.collectAsStateWithLifecycle(initialValue = emptyList())
    val requests by viewModel.requests.collectAsStateWithLifecycle(initialValue = emptyList())
    val chatHistory by viewModel.chatHistory.collectAsStateWithLifecycle(initialValue = emptyList())
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(ScreenTab.DASHBOARD) }
    var activePlatformLaunch by remember { mutableStateOf<PlatformEntity?>(null) }
    var showAddRequestDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(RoyalBlueSoftTint)
                                .border(1.dp, SilverLuxury, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Logo",
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مكتب الوطن للحلول الرقمية",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                ),
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "المنصة تعمل بنظام تشفير آمن 256-بت بموجب معايير نفاذ", Toast.LENGTH_LONG).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Security Status",
                            tint = SilverDark
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = currentTab == ScreenTab.DASHBOARD,
                    onClick = { currentTab = ScreenTab.DASHBOARD },
                    label = { Text("الرئيسية", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("tab_dashboard")
                )
                NavigationBarItem(
                    selected = currentTab == ScreenTab.PLATFORMS,
                    onClick = { currentTab = ScreenTab.PLATFORMS },
                    label = { Text("المنصات", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.GridView, contentDescription = "المنصات") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("tab_platforms")
                )
                NavigationBarItem(
                    selected = currentTab == ScreenTab.CHAT_AI,
                    onClick = { currentTab = ScreenTab.CHAT_AI },
                    label = { Text("المستشار ساب", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.SupportAgent, contentDescription = "المستشار الذكي") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("tab_chat")
                )
                NavigationBarItem(
                    selected = currentTab == ScreenTab.REQUESTS,
                    onClick = { currentTab = ScreenTab.REQUESTS },
                    label = { Text("الطلبات", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "الطلبات والحلول") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("tab_requests")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "ScreenTransition"
            ) { targetTab ->
                when (targetTab) {
                    ScreenTab.DASHBOARD -> DashboardScreen(
                        platformsCount = platforms.size,
                        requestsCount = requests.size,
                        solvedCount = requests.count { it.statusArabic == "تم توليد الحل الرقمي" },
                        onNavigateToPlatforms = { currentTab = ScreenTab.PLATFORMS },
                        onNavigateToChat = { currentTab = ScreenTab.CHAT_AI },
                        onNavigateToRequests = { currentTab = ScreenTab.REQUESTS }
                    )
                    ScreenTab.PLATFORMS -> PlatformsScreen(
                        platforms = platforms,
                        selectedCategory = selectedCategory,
                        onCategorySelect = { viewModel.selectCategory(it) },
                        onPlatformLaunch = { activePlatformLaunch = it }
                    )
                    ScreenTab.CHAT_AI -> ChatAiScreen(
                        chatHistory = chatHistory,
                        isChatLoading = isChatLoading,
                        onSendMessage = { viewModel.sendChatMessage(it) },
                        onClearChat = { viewModel.clearChat() }
                    )
                    ScreenTab.REQUESTS -> RequestsScreen(
                        requests = requests,
                        onAddRequestClick = { showAddRequestDialog = true },
                        onDeleteRequest = { viewModel.deleteRequest(it) }
                    )
                }
            }

            // Secure simulated Browser modal when launching platform
            activePlatformLaunch?.let { platform ->
                SecurePlatformBrowser(
                    platform = platform,
                    onClose = { activePlatformLaunch = null }
                )
            }

            // Add Request Dialog
            if (showAddRequestDialog) {
                AddRequestDialog(
                    onDismiss = { showAddRequestDialog = false },
                    onConfirm = { title, type, desc, company, phone ->
                        viewModel.addRequest(title, type, desc, company, phone)
                        showAddRequestDialog = false
                    }
                )
            }
        }
    }
}

// ==========================================
// SCREEN 1: DASHBOARD
// ==========================================
@Composable
fun DashboardScreen(
    platformsCount: Int,
    requestsCount: Int,
    solvedCount: Int,
    onNavigateToPlatforms: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToRequests: () -> Unit
) {
    val context = LocalContext.current
    val contacts = listOf(
        Pair("خدمة العملاء", "0506953566"),
        Pair("خدمات تعقيب المعاملات", "0551683899"),
        Pair("مدير منصة الحلول الرقمية", "0555614852"),
        Pair("الدعم الفني والحلول", "0556907115")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // WELCOME HERO & DIRECTOR BANNER (Integrated Trust Design)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = RoyalBlueDeep
                ),
                elevation = CardDefaults.cardElevation(8.dp),
                border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(SilverShine, SilverLuxury, SilverDark, SilverShine)))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    RoyalBluePrimary,
                                    RoyalBlueDeep
                                )
                            )
                        )
                ) {
                    // Background watermark app logo pattern
                    AsyncImage(
                        model = "/app/src/main/res/drawable/app_logo.jpg",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.CenterEnd),
                        alpha = 0.12f,
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Trust Slogan Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SilverLuxury.copy(alpha = 0.2f))
                                    .border(1.dp, SilverLuxury.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "مكتب الوطن: شريككم الموثوق",
                                    color = SilverShine,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            // Verified Trust Star Icon
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(PureWhite.copy(alpha = 0.15f), CircleShape)
                                    .border(1.dp, SilverLuxury.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "موثوق ومؤكد",
                                    tint = SilverShine,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // App Title & Core message
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "مرحباً بكم في المنصة الموحدة للخدمات الرقمية",
                                color = SilverShine.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = "مكتب الوطن للحلول والخدمات الإلكترونية",
                                color = PureWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 26.sp
                            )
                        }

                        HorizontalDivider(
                            color = SilverLuxury.copy(alpha = 0.25f),
                            thickness = 1.dp
                        )

                        // Director Profile Card Integrated
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PureWhite.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                .border(1.dp, SilverLuxury.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(RoyalBlueSecondary)
                                    .border(1.5.dp, SilverShine, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "مدير المكتب الأستاذ ماجد سعود العميري",
                                    tint = PureWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "الأستاذ ماجد سعود العميري",
                                    color = PureWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "مدير عام المنصة والشريك الموثوق",
                                    color = SilverShine,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Trust Slogan / Signature Quote
                        Text(
                            text = "« شعارنا الأمان، الدقة والموثوقية المطلقة في تبسيط كافة إجراءات المعاملات وإعادة هيكلة وحلول SAP للقطاع الخاص والعام. »",
                            color = PureWhite.copy(alpha = 0.95f),
                            fontSize = 11.5.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        // FEATURED: CONTACT CHANNELS (DIRECT DIAL & WHATSAPP)
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "أرقام التواصل والدعم المباشر",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Layout our numbers beautifully
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    contacts.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pair.forEach { contact ->
                                val (role, phone) = contact
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                            context.startActivity(dialIntent)
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(1.dp, SilverLuxury.copy(alpha = 0.4f)),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = role,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RoyalBluePrimary
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Phone,
                                                    contentDescription = "اتصال",
                                                    tint = RoyalBlueSecondary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = phone,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                                )
                                            }

                                            // Small quick WhatsApp button
                                            IconButton(
                                                onClick = {
                                                    val formattedPhone = if (phone.startsWith("0")) {
                                                        "966" + phone.substring(1)
                                                    } else {
                                                        "966$phone"
                                                    }
                                                    val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone"))
                                                    context.startActivity(waIntent)
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Send,
                                                    contentDescription = "واتساب",
                                                    tint = RoyalBlueAccent,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Feature Announcement / Quick Navigation
        item {
            Column {
                Text(
                    text = "الخدمات والمؤشرات الرئيسية",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stat 1: Integrated Platforms
                    StatCard(
                        title = "المنصات المتكاملة",
                        value = "9 منصات",
                        subtitle = "حكومية وتجارية موحدة",
                        icon = Icons.Default.Apps,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToPlatforms() }
                    )

                    // Stat 2: Active Consultancies
                    StatCard(
                        title = "الطلبات والحلول",
                        value = "$requestsCount طلبات",
                        subtitle = "تم اقتراح $solvedCount خطة حل AI",
                        icon = Icons.Default.AssignmentTurnedIn,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToRequests() }
                    )
                }
            }
        }

        // Direct Core Services Highlight Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, SilverLuxury.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(RoyalBlueSoftTint)
                                .border(1.dp, SilverLuxury.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = RoyalBluePrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "برمجة واستشارات نظم SAP",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "هيكلة القطاعين العام والخاص برمجياً",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Text(
                        text = "نقدم حلول تكامل الأنظمة وبرمجة حلول SAP المخصصة لتطوير وهيكلة العمليات الإدارية والمالية مع ربط فوري بكافة الخدمات والمنصات.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    Button(
                        onClick = { onNavigateToChat() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = PureWhite
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("استشر المستشار الذكي SAP فوراً")
                    }
                }
            }
        }

        // Information Security Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RoyalBlueSoftTint)
                    .border(1.dp, SilverLuxury.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security Shield",
                        tint = RoyalBlueSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "وصول آمن وموثق بالكامل",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "المنصة لا تخزن كلمات المرور الرسمية. جميع التعاملات تتم عبر تشفير ثنائي آمن وبوابة نفاذ الموحدة.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, SilverLuxury.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}


// ==========================================
// SCREEN 2: PLATFORMS
// ==========================================
@Composable
fun PlatformsScreen(
    platforms: List<PlatformEntity>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    onPlatformLaunch: (PlatformEntity) -> Unit
) {
    val categories = listOf("الكل", "خدمات حكومية", "قطاع الأعمال", "خدمات بلدية", "عدلية وقانونية", "خدمات مالية", "تجارة وصناعة")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "دليل المنصات الرقمية الموحد",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "تجميع المنصات الرسمية لتسهيل وصولك ببيئة مشفرة آمنة",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Horizontal scrolling category chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else PureWhite
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color.Transparent else SilverLuxury.copy(alpha = 0.5f),
                            RoundedCornerShape(30.dp)
                        )
                        .clickable { onCategorySelect(category) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) PureWhite else LightTextPrimary
                    )
                }
            }
        }

        // Platforms Grid/List
        if (platforms.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("لا توجد منصات ضمن هذا التصنيف حالياً", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(platforms) { platform ->
                    PlatformCard(
                        platform = platform,
                        onLaunch = { onPlatformLaunch(platform) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlatformCard(
    platform: PlatformEntity,
    onLaunch: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, SilverLuxury.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Styled Icon Box
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(RoyalBlueSoftTint)
                            .border(1.dp, SilverLuxury.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (platform.iconName) {
                                "absher" -> Icons.Default.Person
                                "qiwa" -> Icons.Default.Work
                                "balady" -> Icons.Default.HomeWork
                                "najiz" -> Icons.Default.Gavel
                                "saber" -> Icons.Default.QrCodeScanner
                                "mudad" -> Icons.Default.Payments
                                "etimad" -> Icons.Default.AccountBalance
                                "muqeem" -> Icons.Default.Group
                                "fasah" -> Icons.Default.LocalShipping
                                else -> Icons.Default.Link
                            },
                            contentDescription = null,
                            tint = RoyalBluePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = platform.nameArabic,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(RoyalBlueSoftTint)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = platform.categoryArabic,
                                fontSize = 10.sp,
                                color = RoyalBluePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Security badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Verified",
                        tint = RoyalBlueSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "آمن",
                        fontSize = 11.sp,
                        color = RoyalBlueSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = platform.descriptionArabic,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مستوى التشفير: ${platform.securityLevelArabic}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Button(
                    onClick = onLaunch,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("دخول آمن", fontSize = 11.sp)
                }
            }
        }
    }
}


// ==========================================
// SCREEN 3: AI CONSULTANT (CHAT)
// ==========================================
@Composable
fun ChatAiScreen(
    chatHistory: List<ChatMessageEntity>,
    isChatLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit
) {
    var textState by remember { mutableStateOf("") }
    val suggestionChips = listOf(
        "كيف أهيئ نظام SAP للشركات؟",
        "حل مشكلة توثيق عقد عمل في قوى",
        "خطوات إصدار رخصة تجارية بلدي",
        "كيف تفيد إعادة هيكلة المؤسسة رقمياً؟"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(RoyalBlueSoftTint)
                        .border(1.dp, SilverLuxury.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null, tint = RoyalBluePrimary)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "مستشار ساب والأنظمة الرقمية الذكي",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "أجب عن الاستفسارات، حلول SAP، وإعادة الهيكلة",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            IconButton(onClick = onClearChat) {
                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat", tint = MaterialTheme.colorScheme.error)
            }
        }

        // Chat Conversation View
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, SilverLuxury.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            if (chatHistory.isEmpty()) {
                // Welcoming / Suggestion State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "مرحباً بك في مستشارك الرقمي!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اطرح أي سؤال حول الأنظمة السعودية، ساب SAP، تطوير وهيكلة منشأتك، أو أي استفسار آخر.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "أسئلة شائعة للاستشارة:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    suggestionChips.forEach { chip ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onSendMessage(chip) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(1.dp, SilverLuxury.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = chip,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    reverseLayout = false
                ) {
                    items(chatHistory) { message ->
                        ChatBubble(message = message)
                    }

                    if (isChatLoading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = RoyalBluePrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "جاري صياغة الاستشارة الذكية...",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                placeholder = { Text("اكتب استشارتك هنا حول SAP أو المنصات...", fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input"),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (textState.isNotBlank()) {
                            onSendMessage(textState)
                            textState = ""
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            )

            FloatingActionButton(
                onClick = {
                    if (textState.isNotBlank()) {
                        onSendMessage(textState)
                        textState = ""
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessageEntity) {
    val isUser = message.sender == "user"
    val bubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isUser) 16.dp else 0.dp,
        bottomEnd = if (isUser) 0.dp else 16.dp
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(bubbleShape)
                .then(
                    if (isUser) {
                        Modifier.background(
                            Brush.linearGradient(listOf(RoyalBluePrimary, RoyalBlueSecondary))
                        )
                    } else {
                        Modifier
                            .background(PureWhite)
                            .border(1.dp, SilverLuxury.copy(alpha = 0.5f), bubbleShape)
                    }
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.message,
                color = if (isUser) PureWhite else LightTextPrimary,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = if (isUser) TextAlign.Right else TextAlign.Left
            )
        }
    }
}


// ==========================================
// SCREEN 4: REQUESTS (SUPPORT & RESTRUCTURING)
// ==========================================
@Composable
fun RequestsScreen(
    requests: List<RequestEntity>,
    onAddRequestClick: () -> Unit,
    onDeleteRequest: (Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "المعاملات وإعادة الهيكلة الرقمية",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "قدم طلباً لإنجاز معاملة، استشارة SAP، أو خطة لإعادة هيكلة شركتك",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (requests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "سجل طلباتك ومعاملاتك الرقمية",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اضغط على الزر أدناه لتسجيل معاملة وسنقدم لك حلاً فورياً باستخدام الذكاء الاصطناعي.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(requests) { request ->
                        RequestItemCard(request = request, onDelete = { onDeleteRequest(request.id) })
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddRequestClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_request_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "أضف طلب")
                Spacer(modifier = Modifier.width(8.dp))
                Text("طلب جديد", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RequestItemCard(
    request: RequestEntity,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SilverLuxury.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "المنشأة: ${request.facilityName}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Delete Button
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(RoyalBlueSoftTint)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = request.typeArabic,
                        fontSize = 10.sp,
                        color = RoyalBluePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            if (request.statusArabic == "قيد المراجعة") SilverShine
                            else RoyalBlueSoftTint
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = request.statusArabic,
                        fontSize = 10.sp,
                        color = if (request.statusArabic == "قيد المراجعة") SilverDark else RoyalBluePrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Expanded view displaying Gemini-generated digital solution proposal
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "تفاصيل المشكلة / الطلب:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = request.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "رقم التواصل للتنسيق:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = request.contactPhone,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Premium AI Response Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SilverLuxury.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = RoyalBlueAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مقترح الهيكلة والحل الرقمي الفوري (AI):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = request.aiResponse ?: "جاري تحليل طلبك لتوليد أفضل الحلول التقنية المخصصة...",
                                fontSize = 11.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// SECURE EMBEDDED SIMULATED BROWSER (PORTAL ACCESS)
// ==========================================
@Composable
fun SecurePlatformBrowser(
    platform: PlatformEntity,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onClose,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        confirmButton = {},
        dismissButton = {},
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Browser Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(RoyalBluePrimary)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Https, contentDescription = "Secure Https", tint = SilverShine, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = platform.url,
                            color = PureWhite,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PureWhite)
                    }
                }

                // Security Verification Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SilverShine)
                        .border(1.dp, SilverLuxury.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = RoyalBluePrimary, modifier = Modifier.size(18.dp))
                        Text(
                            text = "نظام الدخول الآمن الموحد مشفر بنسبة 100%. المستفيد جاهز للتصفح والربط.",
                            fontSize = 10.sp,
                            color = RoyalBluePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Web View Simulator Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(PureWhite)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = null,
                        tint = RoyalBluePrimary.copy(alpha = 0.2f),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "جاري فتح الاتصال الآمن مع ${platform.nameArabic}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = RoyalBluePrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "لضمان أمان معاملاتك، يمكنك فتح الرابط الأصلي مباشرة في متصفحك الخارجي الآمن أو الاستعانة بمستشارنا الذكي لحل المشكلات.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(platform.url))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBluePrimary, contentColor = PureWhite),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("فتح الرابط الأصلي", color = PureWhite)
                        }

                        OutlinedButton(
                            onClick = onClose,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, SilverLuxury),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RoyalBluePrimary)
                        ) {
                            Text("الرجوع للمنصة")
                        }
                    }
                }
            }
        }
    )
}


// ==========================================
// DIALOG: ADD REQUEST (NEW TRANSACTION / CONSULT)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRequestDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("برمجة وتكامل SAP") }
    val serviceTypes = listOf("برمجة وتكامل SAP", "إعادة هيكلة المنشأة رقمياً", "معاملات المنصات الحكومية", "حل مشكلات تقنية واستفسار")
    var desc by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && desc.isNotBlank() && companyName.isNotBlank() && phone.isNotBlank()) {
                        onConfirm(title, serviceType, desc, companyName, phone)
                    }
                },
                enabled = title.isNotBlank() && desc.isNotBlank() && companyName.isNotBlank() && phone.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = PureWhite)
            ) {
                Text("إرسال وتوليد الحل الرقمي")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = MaterialTheme.colorScheme.error)
            }
        },
        title = {
            Text(
                text = "طلب جديد واستشارة فورية",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان الطلب (مثال: ربط قوى بالرواتب)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("input_title"),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = serviceType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("نوع الاستشارة / المعاملة", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().clickable { typeExpanded = true }.testTag("input_type"),
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    modifier = Modifier.clickable { typeExpanded = true }
                                )
                            }
                        )
                        DropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            serviceTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type, fontSize = 12.sp) },
                                    onClick = {
                                        serviceType = type
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("اسم الشركة / المنشأة المستفيدة", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("input_company"),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم جوال للتواصل والتنسيق", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("input_phone"),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }

                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("تفاصيل المشكلة، المعاملة أو أهداف إعادة الهيكلة الرقمية...", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("input_desc"),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 5
                    )
                }
            }
        }
    )
}
