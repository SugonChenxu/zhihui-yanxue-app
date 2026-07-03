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
-assumenosideffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}