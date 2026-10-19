package com.rafiq.launcher.data.prayer

data class ManualCity(
    val id: String,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneId: String
)

object ManualCities {
    val all = listOf(
        ManualCity("cairo", "القاهرة", "مصر", 30.0444, 31.2357, "Africa/Cairo"),
        ManualCity("alexandria", "الإسكندرية", "مصر", 31.2001, 29.9187, "Africa/Cairo"),
        ManualCity("giza", "الجيزة", "مصر", 30.0131, 31.2089, "Africa/Cairo"),
        ManualCity("makkah", "مكة المكرمة", "السعودية", 21.3891, 39.8579, "Asia/Riyadh"),
        ManualCity("madinah", "المدينة المنورة", "السعودية", 24.4672, 39.6111, "Asia/Riyadh"),
        ManualCity("riyadh", "الرياض", "السعودية", 24.7136, 46.6753, "Asia/Riyadh"),
        ManualCity("jeddah", "جدة", "السعودية", 21.4858, 39.1925, "Asia/Riyadh"),
        ManualCity("jerusalem", "القدس", "فلسطين", 31.7683, 35.2137, "Asia/Jerusalem"),
        ManualCity("amman", "عمّان", "الأردن", 31.9454, 35.9284, "Asia/Amman"),
        ManualCity("beirut", "بيروت", "لبنان", 33.8938, 35.5018, "Asia/Beirut"),
        ManualCity("baghdad", "بغداد", "العراق", 33.3152, 44.3661, "Asia/Baghdad"),
        ManualCity("dubai", "دبي", "الإمارات", 25.2048, 55.2708, "Asia/Dubai"),
        ManualCity("doha", "الدوحة", "قطر", 25.2854, 51.5310, "Asia/Qatar"),
        ManualCity("kuwait", "الكويت", "الكويت", 29.3759, 47.9774, "Asia/Kuwait"),
        ManualCity("casablanca", "الدار البيضاء", "المغرب", 33.5731, -7.5898, "Africa/Casablanca"),
        ManualCity("tunis", "تونس", "تونس", 36.8065, 10.1815, "Africa/Tunis"),
        ManualCity("algiers", "الجزائر", "الجزائر", 36.7538, 3.0588, "Africa/Algiers"),
        ManualCity("istanbul", "إسطنبول", "تركيا", 41.0082, 28.9784, "Europe/Istanbul"),
        ManualCity("karachi", "كراتشي", "باكستان", 24.8607, 67.0011, "Asia/Karachi"),
        ManualCity("kuala_lumpur", "كوالالمبور", "ماليزيا", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
        ManualCity("jakarta", "جاكرتا", "إندونيسيا", -6.2088, 106.8456, "Asia/Jakarta"),
        ManualCity("london", "لندن", "المملكة المتحدة", 51.5074, -0.1278, "Europe/London"),
        ManualCity("paris", "باريس", "فرنسا", 48.8566, 2.3522, "Europe/Paris"),
        ManualCity("new_york", "نيويورك", "الولايات المتحدة", 40.7128, -74.0060, "America/New_York")
    )

    fun byId(id: String): ManualCity = all.firstOrNull { it.id == id } ?: all.first()
}
