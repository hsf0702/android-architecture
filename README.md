# android-architecture
# https://github.com/kinglong198404/android-architecture
a project of android architecture with samples

一、android 整体架构分层: 

一个新的产品以一个app工程和和多个Module下的工程组合而来，以下是以一个产品为例的架构分层（层级依赖关系:BizModule->BizComponent->BasicComponent）：

    
1.基础组件（技术通用） BasicComponent Layer       
 Libs：CommonUtils、EventBusUtils、BleUtils、Httputils（通用网络客户端组件：请求、上传、下载）、SocketUtils
       SqlUtils、PreferenceUtils等基础框架的封装。
  
 OpenSource Libs：EventBus、OTTO、Retrofit2、OkHttp3、Gson、Jackson、Fresco、Glide、GreenDao、Ormlite
       Permission4M、Dagger2、RxJava、RxAndroid、LeakCanary、BlockCanary等

2.业务组件（领域通用） BizComponent Layer
  放置与业务有关的组件工程，为应用及所有模块服务，不属于某一个应用或模块。
  BaseUI：string.xml、适配的dimens.xml、style.xml、toast、alertdialog、appacativity、baseacitivity、baseframment、UIWidget（自定义UI组件）等

  
  Core： 平台核心组件：公共接口、公共实体类、核心业务api接口及实现、aidl接口及服务

  AppCore：应用核心模块：注册注销、选人、用户信息页、登录（可共享给其他模块）、第三方登录、分享、子系统应用启动、自动更新等
  
  音视频：Audioplayer、Audiorecorder、Ijkplayer
  
  数据提供：DataProvider 封装多种数据源的请求，对应用层提供统一的接口。数据源包括：网络请求、本地数据库访问、文件访问、缓存数据等
  
  第三方sdk： ShareSDK（登录和分享）、PaySDK、LBSSDK、Zxing
  
3.模块层（特定应用） App/Module Layer
app（主应用：主应用的基本功能）+ Module（子系统或应用模块：CMS、Chat（聊天含视频会话）、音视频会议、NetDisk等）

二、android Module内部架构分层：

以下是以某一个界面为例的分层设计：

1.数据层（dataprovider+sqlite/cache/remoteapi，注意统一数据源到sqlite、remoteapi用来更新sqlite）

2.业务层 

3.表现层

框架：MVP/MVVM/Flux
  

附  ：android项目架构图
![Android项目架构图](https://github.com/kinglong198404/android-architecture/blob/master/android-architecture.png)
