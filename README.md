# android-architecture
# https://github.com/kinglong198404/android-architecture
a project of android architecture with samples

一、android 整体架构分层: 

一个新的产品以一个app工程和和多个Module下的工程组合而来，以下是以一个产品为例的架构分层（层级依赖关系:app/module->framework->business->basic）：

1.应用/模块层  App/Module/Core Layer
App：主应用，可按需集成一个或多个子模块。
Module：子系统或应用模块，也可以打包成独立App。如CMS、Chat、音视频会议、云盘等
Core：供App和Module公用的平台核心库。包括：1.公共接口、实体类、核心api接口及实现、aidl接口及服务；2.注册注销、选人、用户信息页、登录界面、子系统应用启动、自动更新等。
App和Module要依赖应用框架层，在此基础上搭建模块内部的多层框架。App和Module直接引用通用业务组件实现相关功能。

2.应用框架层 App Framework Layer
BaseUI：通用UI库，string.xml、适配的dimens.xml、style.xml、toast、alertdialog、appacativity、baseacitivity、baseframment、UIWidget（自定义UI组件）等
CommonUtils：通用工具库，自行封装的一些必要的工具类。
Auth：身份验证、登录及第三方登录方案的封装。
Router：装管理界面跳转，引用第三方或自行封。
权限框架： Permission4M。
NetUtils：Httputils（通用网络客户端组件：请求、上传、下载、Https，可引用Retrofit2、OkHttp3等）、SocketUtils（常规Socket通讯和SSL安全的Socket通讯的封装）、BleUtils（蓝牙通讯库的封装）。
DBUtils：封装数据库工具库SqlUtils或引入第三方库GreenDao、Ormlite等。
PreferenceUtils：必要的封装，便于使用。
ImageLoader方案：引入第三方Fresco、Glide、ImageLoader，可适当封装。
Json解析：使用第三方库Gson、Jackson等。
Eventbus：事件总线组件。可引用EventBus、OTTO等。
性能监控框架：使用等LeakCanary、BlockCanary等。
其他：Dagger2、RxJava、RxAndroid。

3.通用业务组件：Common Business Libs
音频、视频、二维码、LBS、支付、社会化分享、即时通讯等组件封装。  
音视频业务：Audioplayer、Audiorecorder、Ijkplayer
第三方登录和分享的封装：socialsdk
第三方sdk及封装： PaySDK、LBSSDK、Zxing
       

二、android Module内部架构分层：

以下是以某一个界面为例的分层设计：

1.数据层（dataprovider+sqlite/cache/remoteapi，注意统一数据源到sqlite、remoteapi用来更新sqlite）
   封装多种数据源的请求，对应用层提供统一的接口。数据源包括：网络请求、本地数据库访问、文件访问、缓存数据等

2.业务层 

3.表现层

框架：MVP/MVVM/Flux

附  ：android项目架构图
![Android项目架构图](https://github.com/kinglong198404/android-architecture/blob/master/android-architecture.png)
