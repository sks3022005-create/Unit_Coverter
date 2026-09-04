# ---------------------------------------------------------------------------
# ProGuard / R8 rules
#
# The release build has minification AND resource shrinking enabled. Everything
# below is reachable only via reflection or code generation, so R8 cannot see
# the usage and would otherwise strip it — producing a release APK that crashes
# on launch while the debug build works fine.
# ---------------------------------------------------------------------------

# Keep line numbers so Play Console crash reports are readable, but hide the
# original source file names.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Generic signatures and annotations are needed by Room, Hilt and kotlinx.serialization.
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# ---------------------------------------------------------------------------
# Kotlin
# ---------------------------------------------------------------------------
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }

# ---------------------------------------------------------------------------
# kotlinx.serialization — type-safe navigation routes are serialized reflectively.
# Without these the app crashes on any navigation with arguments.
# ---------------------------------------------------------------------------
-keepattributes *Annotation*
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
    static **$* *;
}
-keepclassmembers class **$serializer {
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
# Navigation route classes in this app
-keep @kotlinx.serialization.Serializable class com.example.unit_coverter.** { *; }

# ---------------------------------------------------------------------------
# Hilt / Dagger — generated components are looked up by name at runtime.
# ---------------------------------------------------------------------------
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# ---------------------------------------------------------------------------
# Room — entities and DAOs are instantiated reflectively by generated code.
# ---------------------------------------------------------------------------
-keep class androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class com.example.unit_coverter.data.local.entity.** { *; }
-dontwarn androidx.room.paging.**

# ---------------------------------------------------------------------------
# Glance app widget — the receiver is resolved from the manifest by name.
# ---------------------------------------------------------------------------
-keep class com.example.unit_coverter.widget.** { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidget { *; }

# Quick Settings tile service — resolved from the manifest.
-keep class com.example.unit_coverter.tile.** { *; }
-keep class * extends android.service.quicksettings.TileService { *; }

# ---------------------------------------------------------------------------
# Play Billing — the library uses reflection over its own AIDL stubs.
# ---------------------------------------------------------------------------
-keep class com.android.vending.billing.** { *; }
-keep class com.android.billingclient.api.** { *; }
-dontwarn com.android.billingclient.**

# ---------------------------------------------------------------------------
# The unit registry: unit and category definitions carry data used for display.
# Keeping them avoids any chance of R8 rewriting the enum-like singletons.
# ---------------------------------------------------------------------------
-keep class com.example.unit_coverter.core.registry.** { *; }
-keep class com.example.unit_coverter.core.math.** { *; }
-keep class com.example.unit_coverter.core.cooking.** { *; }

# ---------------------------------------------------------------------------
# Compose
# ---------------------------------------------------------------------------
-dontwarn androidx.compose.**

# Strip Android log calls from the release build.
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
