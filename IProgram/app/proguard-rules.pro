#引用的其他Module可以直接在app的这个混淆文件里配置
#混淆保护自己项目的部分代码以及引用的第三方jar包library
#如果在当前的application module或者依赖的library module中使用了第三方的库，并不需要显式添加规则-libraryjars xxx
#添加了反而有可能在打包的时候遭遇同一个jar多次被指定的错误，一般只需要添加忽略警告和保持某些class不被混淆的声明。
#以libaray的形式引用了开源项目,如果不想混淆 keep 掉，在引入的module的build.gradle中设置minifyEnabled=false


################### 1.通用部分  ##################

##记录生成的日志数据,gradle build时在本项目根目录输出##
#apk 包内所有 class 的内部结构
-dump proguard/class_files.txt
#未混淆的类和成员
-printseeds proguard/seeds.txt
#列出从 apk 中删除的代码
-printusage proguard/unused.txt
#混淆前后的映射
-printmapping proguard/mapping.txt

# Uncomment this to preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to hide the original source file name.
-renamesourcefileattribute SourceFile

-keep class avp8.**

#忽略警告
-ignorewarnings
#以上是默认的配置

#指定代码的压缩级别
-optimizationpasses 5

#包名不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

 #优化 不优化输入的类文件
-dontoptimize

 #预校验
-dontpreverify

 #混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护注解
-keepattributes *Annotation*

# 保持哪些类不被混淆 必须keep四大组件类名 否则在manifest中找不到注册号的类
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.message.BroadcastReceiver
-keep public class * extends android.message.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#如果引用了v4或者v7包等第三方库
-dontwarn android.support.**
-dontwarn com.tencent.**
-dontwarn org.dom4j.**
-dontwarn org.slf4j.**
-dontwarn org.http.mutipart.**
-dontwarn org.apache.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.commons.codec.binary.**
-dontwarn weibo4android.**

-keep public class org.jivesoftware.** {*;}

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.message.Context, android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keep public class * extends android.view.View {
    public <init>(android.message.Context);
    public <init>(android.message.Context, android.util.AttributeSet);
    public <init>(android.message.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#保持 Parcelable 和它的子类不被混淆，还有Creator成员变量
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#避免混淆泛型与反射
-keepattributes Signature
-keepattributes EnclosingMethod

# 不混淆内部类
-keepattributes InnerClasses




################### 2.第三方模块的混淆选项  ####################

# for gson
# 如果用用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# 用Gson解析的JavaBean类即实体类不被混淆。
-keep class com.matrix.app.entity.json.** { *; }
-keep class com.matrix.appsdk.network.model.** { *; }

# for zxing
-keep class com.google.zxing.** { *; }

 # for facebook
-keep class com.facebook.**{ *; }
-keep class com.facebook.cache.common.**{ *; }
-keep class com.facebook.cache.disk.**{ *; }
-keep class com.facebook.common.**{ *; }

# for fm
-keep class com.badlogic.** { *; }
-keep class * implements com.badlogic.gdx.utils.Json*
-keep class org.openide.awt.**{ *; }
-keep class fm.icelink.**{ *; }

# for nineoldandroids
-keep class com.nineoldandroids.** { *; }
-keep interface com.nineoldandroids.** { *; }
-dontwarn com.nineoldandroids.**

# for okhttp3
-keepattributes Signature
-keepattributes Annotation
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# for retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# for volley
-keep class com.android.volley.** {*;}
-keep class com.android.volley.toolbox.** {*;}
-keep class com.android.volley.Response$* { *; }
-keep class com.android.volley.Request$* { *; }
-keep class com.android.volley.RequestQueue$* { *; }
-keep class com.android.volley.toolbox.HurlStack$* { *; }
-keep class com.android.volley.toolbox.ImageLoader$* { *; }

-keep class org.apache.** { *; }
-keep class com.sun.** { *; }
-dontwarn com.sun.mail.handlers.handler_base

# for mob
-keep class com.mob.**{*;}
-keep class cn.smssdk.**{*;}
-dontwarn com.mob.**

-keep class android.net.http.SslError
-keep class android.webkit.**{*;}
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class m.framework.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn cn.sharesdk.**
-dontwarn **.R$*


# for ijk
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**


# 自定义模块中含有反射的
-keep public class * extends com.klfront.core.BaseObject {*;}


