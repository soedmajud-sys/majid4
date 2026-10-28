package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    private val platformRepository = PlatformRepository(database.platformDao())
    private val requestRepository = RequestRepository(database.requestDao())
    private val chatRepository = ChatMessageRepository(database.chatMessageDao())
    private val geminiRepository = GeminiRepository()

    // UI States
    val platforms: StateFlow<List<PlatformEntity>> = platformRepository.allPlatforms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val requests: StateFlow<List<RequestEntity>> = requestRepository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatHistory: StateFlow<List<ChatMessageEntity>> = chatRepository.chatHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Seed the default platforms if database is empty
            platformRepository.seedDatabaseIfEmpty()
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    // Filter platforms based on selection
    val filteredPlatforms: Flow<List<PlatformEntity>> = combine(
        platforms,
        _selectedCategory
    ) { allPlatforms, category ->
        if (category == "الكل") {
            allPlatforms
        } else {
            allPlatforms.filter { it.categoryArabic == category }
        }
    }

    // SAP & Digital Consult AI Prompt
    private val SYSTEM_PROMPT = """
        أنت المستشار الرقمي الذكي لـ "المنصة الرقمية الموحدة". 
        تخصصك هو تقديم الحلول الرقمية، ومساندة جميع المنصات الحكومية والخاصة في المملكة العربية السعودية (مثل أبشر، قوى، بلدي، ناجز، مدد، اعتماد، سبر، مقيم)، وبرمجة نظم معلومات ومشاريع SAP (ساب) وإعادة هيكلة وتطوير القطاعات الحكومية والخاصة رقمياً لتسهيل وصول المستفيدين بكل سهولة وأمان.
        
        مهامك وإرشاداتك:
        1. الإجابة باللغة العربية الفصحى بشكل مهني، واضح، ومبسط مع خطوات عملية مرقمة.
        2. عند السؤال عن نظام SAP، قدم إرشادات برمجية أو هندسية واضحة وممتازة، مثل تهيئة الوحدات (FI/CO, MM/SD, SuccessFactors) أو لغة البرمجة ABAP أو التحول الرقمي إلى SAP S/4HANA.
        3. عند الاستفسار عن مشكلة في المنصات الحكومية، اشرح الخطوات الرسمية الآمنة لحلها وتسهيل الإجراءات على المستفيد.
        4. شجع دائماً على تبسيط الإجراءات، الأمن والسرية للبيانات الحساسة.
        5. قدم مقترحات حول إعادة الهيكلة الرقمية وتحسين كفاءة المنشآت والشركات.
    """.trimIndent()

    // Add a Transaction / Restructuring / SAP Request with immediate AI solution draft
    fun addRequest(
        title: String,
        type: String,
        description: String,
        facilityName: String,
        contactPhone: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // Create initial request entry
            val request = RequestEntity(
                title = title,
                typeArabic = type,
                description = description,
                facilityName = facilityName,
                contactPhone = contactPhone,
                statusArabic = "قيد المراجعة"
            )
            val requestId = requestRepository.insertRequest(request)

            // Trigger Gemini to analyze the request and generate an immediate customized draft of digital solution/restructuring plan!
            val aiPrompt = """
                لقد تلقينا طلباً جديداً لإنجاز معاملة أو تقديم استشارة إعادة هيكلة أو حل مشكلة رقمية. 
                تفاصيل الطلب:
                - العنوان: $title
                - نوع الخدمة المطلوبة: $type
                - تفاصيل المشكلة/الطلب: $description
                - اسم المنشأة أو الشركة: $facilityName
                
                بصفتك خبير المنصات الرقمية وSAP وإعادة الهيكلة، يرجى تقديم مقترح فني وحل رقمي مباشر يتضمن:
                1. تحليل سريع للمشكلة أو طلب إعادة الهيكلة.
                2. الخطوات التفصيلية المقترحة لمعالجة الطلب عبر المنصات ذات العلاقة.
                3. نصيحة برمجية أو هيكلية مخصصة (مثلاً إذا كان الطلب يخص SAP أو التحول التقني).
                4. التوصية الأمنية لحماية المعاملة.
                أجب بشكل مباشر ولطيف ومهني باللغة العربية.
            """.trimIndent()

            val solutionProposal = geminiRepository.generateResponse(aiPrompt, SYSTEM_PROMPT)

            // Update request with the AI solution proposal
            val updatedRequest = request.copy(id = requestId.toInt(), aiResponse = solutionProposal, statusArabic = "تم توليد الحل الرقمي")
            requestRepository.updateRequest(updatedRequest)
        }
    }

    fun deleteRequest(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            requestRepository.deleteRequestById(id)
        }
    }

    // Chatbot functionality
    fun sendChatMessage(messageText: String) {
        if (messageText.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            // 1. Insert user message
            val userMsg = ChatMessageEntity(sender = "user", message = messageText)
            chatRepository.insertMessage(userMsg)

            // 2. Set loading
            _isChatLoading.value = true

            // 3. Request Gemini response
            val replyText = geminiRepository.generateResponse(messageText, SYSTEM_PROMPT)

            // 4. Insert assistant response
            val assistantMsg = ChatMessageEntity(sender = "assistant", message = replyText)
            chatRepository.insertMessage(assistantMsg)

            // 5. Clear loading
            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.clearHistory()
        }
    }
}
