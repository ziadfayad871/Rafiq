# رفيق · RAFIQ

مشغّل إسلامي (Islamic Launcher) لأندرويد — بالعربي بالكامل، بمزاج إسلامي هادي: أخضر زمردي عميق، ذهب خافت، نقوش هندسية، وخط Amiri/Tajawal.

نسخة **Kotlin + Jetpack Compose** من المشروع، والنسخة **Flutter** موجودة في: [ziadfayad871/RafiqFlutter](https://github.com/ziadfayad871/RafiqFlutter).

## التحميل والتثبيت

التطبيق جاهز كملف APK على صفحة الإصدارات:

- **[رفيق v1.0.0 (Compose)](https://github.com/ziadfayad871/Rafiq/releases/tag/v1.0.0)** — `rafiq-compose-v1.0.0.apk` (12.5 MB)
- **[رفيق v1.0.0 (Flutter)](https://github.com/ziadfayad871/RafiqFlutter/releases/tag/v1.0.0)** — `rafiq-flutter-v1.0.0.apk` (51 MB)

خطوات التثبيت:
1. نزّل الـ APK من صفحة الإصدار.
2. فعّل **تثبيت التطبيقات من مصادر غير معروفة**.
3. ثبّت الـ APK وافتح **رفيق**.
4. لتثبيته كشاشة بداية: إعدادات النظام ← الواجهة الافتراضية ← **رفيق** (أو من داخل التطبيق: الإعدادات ← «تعيين رفيق كشاشة رئيسية»).

> الإصدار موقّع بمفتاح تصحيح (debug key) لأنه إصدار تجريبي. عند النشر على Play Store يلزم signing key حقيقي.

## المميزات

- **الرئيسية**: ساعة حيّة، التاريخ الميلادي والهجري، مشهد المسجد عند الفجر، بطاقة الصلاة القادمة مع العدّ التنازلي، اختصارات العبادات، والدوك السفلي.
- **الصلاة**: ١١ طريقة حساب و٣٠ مدينة يدوية وتحديد تلقائي للموقع، حلقة عدّ تنازلي، وتنبيهات دقيقة تُعاد جدولتها بعد إعادة تشغيل الجهاز.
- **القرآن**: ١١٤ سورة (٦٢٣٦ آية) بنص عثماني وترجمة Saheeh International، فهرس السور، العلامات المرجعية، تلاوة مشاري العفاسي مع الانتقال التلقائي، واستكمال آخر موضع قراءة.
- **الأذكار**: الصباح والمساء والنوم والمتنوعة، بعدّاد لكل ذكر مع تصفير وعرض المصدر.
- **المهام**: مهام يومية بنسبة إنجاز، إضافة/تعديل/حذف.
- **التركيز**: جلسات بعدّ تنازلي، حفظ الجلسة بعد إغلاق التطبيق، وقائمة التطبيقات المسموحة.
- **المساعد**: إجابات موثوقة من ملف محلي، قراءة صوتية (TTS)، إدخال صوتي (STT)، وإنشاء مهام أو جلسات تركيز بالأوامر.
- **الإعدادات**: ثلاث سمات (زمردي/منتصف الليل/الفجر)، الاسم، التنبيهات، الصوت والاهتزاز، اللغة، إعادة الضبط، وضبط المشغّل.

## البنية

```
app/src/main/java/com/rafiq/launcher/
├── RafiqApplication.kt        نقطة البداية + حاوية الاعتماديات
├── MainActivity.kt            الواجهة والنظام (edge-to-edge)
├── data/                      Quran · Adhkar · Prayer · Tasks · Preferences · Apps
├── service/                   PrayerAlarm · FocusAlarm · Notifications · BootReceiver
├── ui/ screens/               Home · Prayer · Quran · Adhkar · Focus · Tasks · Assistant · Apps · Settings
└── ui/ components/ theme/     نظام التصميم والمكوّنات المشتركة
```

المصحف والأذكار وإجابات المساعد داخل `app/src/main/assets/` كملفات JSON محلية (بدون إنترنت).

## البناء

```bash
# gradle wrapper مرفق
./gradlew assembleDebug
# الناتج: app/build/outputs/apk/debug/app-debug.apk
```

يتطلب: JDK 17+ · Android SDK 36 · build-tools 36.0.0 · minSdk 26 · targetSdk 35.

## الرخص

الكود مفتوح المصدر. الخطوط: [Amiri](https://fonts.google.com/specimen/Amiri) و[Tajawal](https://fonts.google.com/specimen/Tajawal) — رخصة SIL OFL. تسجيل الأذان: «The Adhan — Muslim Call to Prayer» بأداء Aaqib Azeez، مرفوع بواسطة Atcovi على [Wikimedia Commons](https://commons.wikimedia.org/wiki/File:The_Adhan_-_Muslim_Call_to_Prayer_-_Aaqib_Azeez.mp3) تحت رخصة [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/).
