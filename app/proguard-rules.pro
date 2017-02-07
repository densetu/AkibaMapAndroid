# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android-SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepattributes Signature
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn com.squareup.okhttp.*
-dontwarn com.google.appengine.api.urlfetch.*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}
-keepclassmembers class * extends net.dentare.akibamapandroid.activity.BaseActivity {
   public void *(android.view.View);
}
-keepclassmembers class * extends net.dentare.akibamapandroid.activity.BaseSubActivity {
   public void *(android.view.View);
}
-keepclassmembers class * extends android.support.v7.app.AppCompatActivity {
   public void *(android.view.View);
}
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
# Add this global rule
-keepattributes Signature

-keepclassmembers class net.dentare.akibamapandroid.resources.** {
  *;
}