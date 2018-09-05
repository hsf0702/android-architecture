## 4.基础组件层
与业务无关，主要是通用工具库、数据库工具库、蓝牙通讯组件、http通讯组件、socket通讯组件、eventbus组件等。
合理控制各组件的拆分粒度，太小的公有模块不足以构成单独组件的，先放到通用工具库klutil中。

自研库Libs：klutils（通用工具）、preferenceutils、kleventbusutils、klimageloadutils、klhttputils（通用网络客户端组件：请求、上传、下载）、kldbutils（封装访问数据库）、klsocketutils、klbleutils。

开源库OpenSource Libs：Retrofit2、OkHttp3、Dagger2、Fresco、LeakCanary、BlockCanary、RxJava、RxAndroid、Fragment4M等