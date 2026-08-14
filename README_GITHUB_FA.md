# ساخت APK بدون Android Studio

این پروژه برای GitHub Actions آماده شده است.

## مراحل

1. در GitHub یک Repository جدید بساز.
2. محتویات این پوشه را داخل Repository آپلود کن.
3. مطمئن شو فایل زیر وجود دارد:
   `.github/workflows/build-apk.yml`
4. در GitHub وارد تب `Actions` شو.
5. Workflow با نام `Build Android APK` را انتخاب کن.
6. روی `Run workflow` بزن.
7. بعد از تمام شدن Build، وارد همان اجرای موفق شو.
8. پایین صفحه در بخش `Artifacts` فایل `ServiceEndCalculator-APK` را دانلود کن.
9. ZIP دانلودشده را باز کن و `app-debug.apk` را روی گوشی نصب کن.

نکته: GitHub روی runner خودش JDK، Android SDK و Gradle را آماده می‌کند؛ بنابراین روی لپ‌تاپ نیازی به Android Studio یا Android SDK نداری.

تست:
شروع خدمت: ۱۴۰۴/۱۱/۰۱
کسری اضافی: ۶ ماه
سایر کسری: ۰ روز
