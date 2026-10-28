package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PlatformEntity::class, RequestEntity::class, ChatMessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun platformDao(): PlatformDao
    abstract fun requestDao(): RequestDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "unified_digital_platform_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        // Standard seed data representing premium Arabic/Saudi digital systems
        fun getSeedPlatforms(): List<PlatformEntity> {
            return listOf(
                PlatformEntity(
                    nameArabic = "منصة أبشر",
                    nameEnglish = "Absher Portal",
                    descriptionArabic = "المنصة الرائدة للخدمات الإلكترونية للأفراد والمديرية العامة للجوازات والأحوال المدنية بكل أمان.",
                    descriptionEnglish = "The leading electronic services platform for individuals, civil affairs, and passports.",
                    url = "https://www.absher.sa",
                    categoryArabic = "خدمات حكومية",
                    securityLevelArabic = "أمان ثنائي عالي (نفاذ)",
                    rating = 5.0f,
                    iconName = "absher"
                ),
                PlatformEntity(
                    nameArabic = "منصة قوى",
                    nameEnglish = "Qiwa Platform",
                    descriptionArabic = "المنصة الموحدة لقطاع العمل وإدارة عقود الموظفين وتأشيرات العمل الفورية واللوائح والامتثال.",
                    descriptionEnglish = "The unified platform for the labor sector, employment contracts, and compliance.",
                    url = "https://www.qiwa.sa",
                    categoryArabic = "قطاع الأعمال",
                    securityLevelArabic = "تشفير تجاري معتمد",
                    rating = 4.9f,
                    iconName = "qiwa"
                ),
                PlatformEntity(
                    nameArabic = "منصة بلدي",
                    nameEnglish = "Balady Platform",
                    descriptionArabic = "منصة الخدمات البلدية الشاملة لإصدار الرخص التجارية والإنشائية والشهادات الصحية للمنشآت.",
                    descriptionEnglish = "Comprehensive municipal services platform for commercial and building permits.",
                    url = "https://www.balady.gov.sa",
                    categoryArabic = "خدمات بلدية",
                    securityLevelArabic = "أمان حكومي مشفر",
                    rating = 4.8f,
                    iconName = "balady"
                ),
                PlatformEntity(
                    nameArabic = "بوابة ناجز العدلية",
                    nameEnglish = "Najiz Judicial Portal",
                    descriptionArabic = "المنصة الموحدة لوزارة العدل لتقديم خدمات المحاكم، الوكالات الإلكترونية، والإفراغ العقاري والتوثيق.",
                    descriptionEnglish = "The unified platform of the Ministry of Justice for judicial and notary services.",
                    url = "https://najiz.sa",
                    categoryArabic = "عدلية وقانونية",
                    securityLevelArabic = "تشفير عالي التوثيق",
                    rating = 4.9f,
                    iconName = "najiz"
                ),
                PlatformEntity(
                    nameArabic = "نظام سبر الإلكتروني",
                    nameEnglish = "Saber System",
                    descriptionArabic = "منصة تسجيل شهادات مطابقة المنتجات والسلع المستوردة أو المصنعة محلياً لضمان سلامتها الاستهلاكية.",
                    descriptionEnglish = "Platform for registering conformity certificates for imported and local products.",
                    url = "https://saber.sa",
                    categoryArabic = "تجارة وصناعة",
                    securityLevelArabic = "حماية تجارية معتمدة",
                    rating = 4.7f,
                    iconName = "saber"
                ),
                PlatformEntity(
                    nameArabic = "منصة مدد المالية",
                    nameEnglish = "Mudad Platform",
                    descriptionArabic = "نظام حماية الأجور وإدارة الرواتب للمنشآت الصغيرة والمتوسطة للامتثال لأنظمة الموارد البشرية.",
                    descriptionEnglish = "Wage protection and payroll system for compliance with human resource laws.",
                    url = "https://mudad.com.sa",
                    categoryArabic = "خدمات مالية",
                    securityLevelArabic = "ربط بنكي مشفر وآمن",
                    rating = 4.8f,
                    iconName = "mudad"
                ),
                PlatformEntity(
                    nameArabic = "بوابة اعتماد الحكومية",
                    nameEnglish = "Etimad Portal",
                    descriptionArabic = "المنصة الموحدة للخدمات المالية الحكومية، المشتريات والمنافسات، العقود، والمدفوعات والمستخلصات.",
                    descriptionEnglish = "The unified platform for government financial services and procurement.",
                    url = "https://etimad.sa",
                    categoryArabic = "خدمات مالية",
                    securityLevelArabic = "أمان فيدرالي مشفر",
                    rating = 4.9f,
                    iconName = "etimad"
                ),
                PlatformEntity(
                    nameArabic = "منصة مقيم للشركات",
                    nameEnglish = "Muqeem Portal",
                    descriptionArabic = "إدارة شؤون المقيمين وتأشيرات الخروج والعودة وتجديد الإقامات للشركات والمؤسسات بشكل رقمي متكامل.",
                    descriptionEnglish = "Manage corporate resident affairs, visas, and residencies digitally.",
                    url = "https://muqeem.sa",
                    categoryArabic = "قطاع الأعمال",
                    securityLevelArabic = "اتصال آمن مباشر مع الجوازات",
                    rating = 4.9f,
                    iconName = "muqeem"
                ),
                PlatformEntity(
                    nameArabic = "منصة فسح الجمركية",
                    nameEnglish = "Fasah Customs",
                    descriptionArabic = "النافذة الإلكترونية الموحدة للتجارة الخارجية والاستيراد والتصدير والمخلصين الجمركيين.",
                    descriptionEnglish = "The unified electronic window for foreign trade, import, and export.",
                    url = "https://www.fasah.sa",
                    categoryArabic = "تجارة وصناعة",
                    securityLevelArabic = "أمان جمركي سيادي",
                    rating = 4.7f,
                    iconName = "fasah"
                )
            )
        }
    }
}
