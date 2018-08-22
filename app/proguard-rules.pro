

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-dontoptimize
-dontpreverify
#--------------关于Bean类----------

-keep class com.com.loopview.drawableindicator.widget.**{*;}
-keep class com.broadlink.**{*;}
-keep class com.broadlink.networkapi.**{*;}
-keep class com.zxing.**{*;}
-keep class com.supor.suporairclear.config.**{*;}
-keep class com.supor.suporairclear.model.**{*;}

-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keepclassmembers class com.supor.suporairclear.activity.TimerSetActivity$TimerJavaScriptInterface{
  public *;
}
-keepclassmembers class com.supor.suporairclear.activity.FilterCheckActivity{
  public *;
}



#------------------------------一般不会改变的---------------------
-keep public class * extends android.app.Activity  #所有activity的子类不要去混淆
-keep public class * extends android.support.v4.app.FragmentActivity #所有activity的子类不要去混淆
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService #指定具体类不要去混淆

-keepclasseswithmembernames class * {
    native <methods>;  #保持 native 的方法不去混淆
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);  #保持自定义控件类不被混淆，指定格式的构造方法不去混淆
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View); #保持指定规则的方法不被混淆（Android layout 布局文件中为控件配置的onClick方法不能混淆）
}

-keep public class * extends android.view.View {  #保持自定义控件指定规则的方法不被混淆
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclassmembers enum * {  #保持枚举 enum 不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {  #保持 Parcelable 不被混淆（aidl文件不能去混淆）
    public static final android.os.Parcelable$Creator *;
}

-keepnames class * implements java.io.Serializable #需要序列化和反序列化的类不能被混淆（注：Java反射用到的类也不能被混淆）

-keepclassmembers class * implements java.io.Serializable { #保护实现接口Serializable的类中，指定规则的类成员不被混淆
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepattributes Signature  #过滤泛型（不写可能会出现类型转换错误，一般情况把这个加上就是了）

-keepattributes *Annotation*  #假如项目中有用到注解，应加入这行配置

-keep class **.R$* { *; }  #保持R文件不被混淆，否则，你的反射是获取不到资源id的

-keep class **.Webview2JsInterface { *; }  #保护WebView对HTML页面的API不被混淆
-keepclassmembers class * extends android.webkit.WebViewClient {  #如果你的项目中用到了webview的复杂操作 ，最好加入
     public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
     public boolean *(android.webkit.WebView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {  #如果你的项目中用到了webview的复杂操作 ，最好加入
     public void *(android.webkit.WebView,java.lang.String);
}

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

#_______________________________________________________________
#------------------------jar包---------------------------------
#---------ablecloud----------
-keep class yhq.jad.easysdk.**{*;}
-keep class org.java_websocket.**{*;}
-keep class mediatek.android.IoTManager.**{*;}
-keep class com.xlwtech.util.**{*;}
-keep class com.wukoon.api.**{*;}
-keep class com.winnermicro.smartconfig.**{*;}
-keep class com.MURATA.**{*;}
-keep class com.MARVELL.**{*;}
-keep class com.integrity_project.smartconfiglib.**{*;}
-keep class com.hiflying.**{*;}
-keep class com.fasterxml.jackson.**{*;}
-keep class com.example.smartlinklibcom.example.smartlinklib.**{*;}
-keep class com.espressif.iot.esptouch.**{*;}
-keep class com.AI6060H.**{*;}
-keep class com.accloud.cloudservice.**{*;}
-keep class cn.ablecloud.service.**{*;}
-keep class com.fasterxml.jackson.**{*;}
-keep class com.accloud.**{*;}
-keep class com.mxchip.easylink.**{*;}
-keep class cn.com.broadlink.bleasyconfig.**{*;}
#--------友盟-------
-keep class anet.channel.**{*;}
-keep class anetwork.channel.**{*;}
-keep class com.taobao.**{*;}
-keep class com.umeng.message.**{*;}
-keep class org.android.agoo.**{*;}
-keep class org.android.spdy.**{*;}

-keep class com.loopj.android.http.**{*;}
-keep class cn.com.broadlink.bleasyconfig.**{*;}
-keep class com.google.zxing.**{*;}
-keep class cz.msebera.**{*;}
-keep class cn.com.broadlink.networkapi.**{*;}
-keep class com.squareup.picasso.**{*;}
-keep class com.qiniu.android.**{*;}



-keepattributes SourceFile,LineNumberTable
-keep class com.parse.*{ *; }
-dontwarn com.parse.**
-dontwarn com.squareup.picasso.**
-keepclasseswithmembernames class * {
    native <methods>;
}



-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}

#------------忽略警告-------------
-dontwarn com.taobao.**
-dontwarn com.accloud.**
-dontwarn com.alibaba.**
-dontwarn com.mxchip.**
-dontwarn com.realtek.**
-dontwarn com.broadcom.**
-dontwarn com.umeng.**
-dontwarn com.fasterxml.**
-dontwarn org.w3c.**
-dontwarn com.qiniu.**
-dontwarn com.qiniu.**
-dontwarn com.qiniu.**
-dontwarn com.ta.**
-dontwarn com.ut.**



-dontwarn com.gensee.**
-dontwarn org.apache.http.client.**
-dontwarn org.greenrobot.eventbus.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn io.reactivex.**
-dontwarn com.qihoo.**
-dontwarn com.alipay.**
#__________________________________________________________________________

#关于友盟
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-dontwarn com.meizu.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class com.meizu.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class **.R$*{
   public static final int *;
}

#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }


