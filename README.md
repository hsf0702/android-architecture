# android-architecture
# https://github.com/kinglong198404/android-architecture
a project of android architecture with samples

一、android 整体架构分层: 

一个新的产品以一个app工程和和多个Module下的工程组合而来，以下是以一个产品为例的架构分层（层级依赖关系:BizModule->BizComponent->BasicComponent）：

    
1.基础组件（技术通用） BasicComponent Layer       
 Libs：CommonUtils、XXUtils、UIWidget（自定义UI组件）、httputils（通用网络客户端组件：请求、上传、下载）、
       sqlutils（封装访问数据库）、pay（通用支付组件）及其他基础框架的封装。
  
 OpenSource Libs：Retrofit2、OkHttp3、Dagger2、Fresco、LeakCanary、BlockCanary、RxJava、RxAndroid、Fragment4M等

2.业务组件（领域通用） BizComponent Layer
  放置与业务有关的组件工程，为应用及所有模块服务，不属于某一个应用或模块。
  
  baseui（string.xml、适配的dimens.xml、style.xml、toast、alertdialog等）
  
  base（通用基类：appacativity、baseacitivity、baseframment）
  
  core（平台核心组件：基础业务类、基础业务api接口实现、进程间通信接口定义及服务）

  appcore（应用核心模块：如注册注销、登录（可共享给其他模块）、子系统应用启动、选人、用户信息页、自动更新等提供给app和各module共用的功能）
  
3.模块层（特定应用） Module Layer
app（主应用：主应用的基本功能）
+ Module（子系统或应用模块：CMS、聊天（含视频会话）、音视频会议、NetDisk等）
+ Theme（？主题工程：为减少耦合，尽量少定义属性，多覆盖系统属性，打包的时候可以根据需要更换）

各模块通过appcore间接引用baseui base core等


二、android Module内部架构分层：

以下是以某一个界面为例的分层设计：

1.数据层（dataprovider+sqlite/cache/remoteapi，注意统一数据源到sqlite、remoteapi用来更新sqlite）

2.业务层 

3.展现层
框架：MVP/MVVM/Flux

