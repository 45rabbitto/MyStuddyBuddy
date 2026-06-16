# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# PDFBox Android - Keep classes
-keep class com.tom_roush.pdfbox.** { *; }
-keep class com.tom_roush.pdfbox.rendering.** { *; }
-keep class com.tom_roush.pdfbox.pdmodel.** { *; }
-keep class com.tom_roush.pdfbox.filter.** { *; }

# Gemalto JP2 - Keep for PDFBox
-keep class com.gemalto.jp2.** { *; }
-dontwarn com.gemalto.jp2.**

# Keep all native methods
-keepclasseswithmembernames class * {
    native <methods>;
}
# Keep semua model/response classes untuk Gson
-keep class com.studdy.mystudybuddy.network.** { *; }
-keep class com.studdy.mystudybuddy.utils.** { *; }

# Keep Gson annotations
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep all classes with SerializedName (untuk response models)
-keep class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep generic signatures for Retrofit
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Keep Kotlin data classes constructors
-keepclassmembers class ** {
    public <init>(...);
}

# Keep Gson TypeAdapters
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**