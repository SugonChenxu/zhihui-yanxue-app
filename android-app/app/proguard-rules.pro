# proguard-rules.pro for ZhiHuiYanXue App

# Keep all classes that are referenced in AndroidManifest.xml
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

# Keep ViewBinding classes
-keep class * implements androidx.viewbinding.ViewBinding {
    *;
}

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep data classes
-keepclassmembers class * {
    public <init>(...);
}

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# 高德地图 SDK 混淆规则
-keep class com.amap.api.** { *; }
-keep class com.autonavi.** { *; }
-keep class com.locnna.** { *; }
-keep class com.a.a.** { *; }
-keep class com.amap.api.location.** { *; }
-keep class com.amap.api.fence.** { *; }
-keep class com.amap.api.maps.** { *; }
-keep class com.amap.api.maps2d.** { *; }
-keep class com.amap.api.search.** { *; }
-keep class com.amap.api.services.** { *; }
-dontwarn com.amap.api.**
-dontwarn com.autonavi.**
