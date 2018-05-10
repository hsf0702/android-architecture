##基础组件层
  包含开源库、业务无关的自研库两部分
  
  合理控制各组件的拆分粒度，太小的公有模块不足以构成单独组件的，

 Libs：CommonUtils、XXUtils、UIWidget（自定义UI组件）、httputils（通用网络客户端组件：请求、上传、下载）、
       sqlutils（封装访问数据库）、pay（通用支付组件）及其他基础框架的封装。
 OpenSource Libs：Retrofit2、OkHttp3、Dagger2、Fresco、LeakCanary、BlockCanary、RxJava、RxAndroid、Fragment4M等