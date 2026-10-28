package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "platforms")
data class PlatformEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nameArabic: String,
    val nameEnglish: String,
    val descriptionArabic: String,
    val descriptionEnglish: String,
    val url: String,
    val categoryArabic: String, // حكومي، أعمال، مالي، عدلي
    val securityLevelArabic: String, // آمن جداً، تشفير كامل
    val isOfficial: Boolean = true,
    val rating: Float = 5.0f,
    val iconName: String = "default" // To dynamically resolve icons in Compose
)

@Entity(tableName = "requests")
data class RequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val typeArabic: String, // معاملة حكومية، برمجة SAP، إعادة هيكلة، استشارة رقمية
    val description: String,
    val facilityName: String, // اسم المنشأة / الشركة
    val contactPhone: String,
    val statusArabic: String = "قيد المراجعة", // قيد المراجعة، جاري المعالجة، تم الحل، مقترح الحلول جاهز
    val createdAt: Long = System.currentTimeMillis(),
    val aiResponse: String? = null // Auto-generated solution proposal by AI
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // user, assistant
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
