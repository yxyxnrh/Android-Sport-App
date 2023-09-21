# Android-Sport-App
一款使用安卓自带的realm数据库来储存数据的锻炼跑步app，Realm自带数据库加密，需要64位字节数据进行加密。指定数据库的密钥后，用户可以安全地存储自己的锻炼数据，而且可以通过注销的方式来自己清除本地的数据，防止数据的泄露。这个app还同时拥有市面上绝大多数跑步锻炼app的功能：例如用户登录，里程计算，分数评定，跑步记录的浏览，地图定位，轨迹确定与回顾以及社交分享的功能。

## 开发环境
开发环境选择windows10，安卓SDK JDK。开发工具选择Android studio 3.5.2。
         安卓的应用架构选择MVC模式，图2所示是MVC架构的操作过程。这也是市面上常用的软件系统模式，它将软件系统分为三部分。
         模型(Model)：存储系统的中心数据。
         视图(View)：产生一个或多个视图，将其中的信息显示给用户。
         控制图(Controller)：处理用户输入信息，用户交互后，从用户方的视图获得数据并将数据发送到模型，负责管理与用户交互控制。
         
## 系统整体结构
账号数据都存储在本地realm数据库里。长跑信息数据库通过调用高德地图的接口来访问数据。应用层用于实现从高德地图的接口调用数据，编写获取函数实现相应的功能。用户借助应用层来实现与客户端的交互。

![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/aa346810-f36e-4f6e-b162-1d22c4ac714d)

## 系统功能模块
该系统主要分为三大模块：登录注册，长跑功能和历史记录，各大模块下又有相应的子功能。

![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/0609c2a2-dd4c-44da-8298-e64645683e29)


## 登录注册模块
这个模块是用户用来登录注册自己信息的，包括以下几个子功能。
（1）身份注册
经过注册才能在本地数据库里存储自己的信息，并使用该长跑系统。注册将会要求用户输入自己的手机号，获取的验证码和密码。
（2）身份登录
该子模块包含两种登录方式，密码登录和手机验证码登录。
（3）用户数据注销
点击左上角的注销按钮后，用户下次依旧可以登录，但用户在本地存储的历史长跑数据会被全部清除。
        
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/deffc24c-f750-4572-9f4c-95fa3239705f)


## 长跑功能模块
这个功能模块是长跑软件里的核心模块，主要满足长跑用户的功能需求。
（1）界面提示与数据分析
点击开始长跑后，先出现一个提示界面，展示用户在此账号下长跑的时间，路程和次数。并分析本地数据，为用户提供建议，
（2）长跑功能服务
在界面提示里点击开始后，正式进入长跑功能模块，该模块下有两个模式。个人模式：不显示地图，只显示长跑时间，路程和速度。用户可以独自不受干扰锻炼。定位模式：会显示用户在地图上的实时位置!
         
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/8b80b7e0-0e1a-4fc9-8172-aa6eece3df7e)

（3）轨迹绘制
用户完成一次长跑后，会出现轨迹绘制的界面将用户本次长跑的轨迹显示出来，轨迹会被进行去噪和优化。图8是轨迹绘制的流程图。
（4）长跑评分
长跑完成后，对用户全程奔跑的速度和路程等参数进行分析，给出系统对长跑用户的评分，并可以对用户的长跑进行一些指导。图9是长跑评分流程图。
（5）社交分享
长跑完成的用户可以将自己的记录分享到其他社交软件里，直接在轨迹绘制与分数评定的页面加入分享功能。

![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/771f8ca3-d41d-4aae-9f75-707da598d35e)

![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/1dfa911e-ca7d-4561-ad0a-78d4d0b3f1ba)

## 历史记录模块
此模块用于存储和显示用户的历史长跑信息
（1）日历展示
在登录后的第一个界面里创建日历来准确定位任意一天长跑记录。
（2）信息回顾
在日历对应的当天记录上与历史详细记录绑定，实现点击确定的日期，界面跳转到该记录下的详细轨迹与参数。图10是整个历史记录模块的流程图。

![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/1026a4c5-efa6-45b8-a23b-00367da3328f)

## realm数据库设计分析
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/bb3cacb4-adb3-4aea-b5f2-55d37191a5f5)
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/e59c9fe3-bd9a-46c6-b73e-d96bf34da725)
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/9008676a-93ce-4c73-93d5-c58905d6d2fc)


## 最终实现
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/bdc6cc3d-8a77-4edc-bc27-5c252b1ec822)
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/14f36cbe-bf0d-44b8-aa35-9de234aaa2f4)
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/ab3dfa4e-a676-4121-9709-f026baa411a3)
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/c3e482a9-7571-4e7c-9b0a-4b3972467740)
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/0dc6a1a9-4c3a-4460-b55c-77e474b88b35)
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/742e344b-3c6a-4804-8ad9-a3065edb4bae)
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/c057512a-ede5-4045-a3b2-788f14b1db78)
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/9dec7eb6-c8df-45ef-b3ab-0e16c157b43d)
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/560489c0-7a1c-4fe6-815a-9a0d7a3e0e51)
![image](https://github.com/yxyxnrh/Android-Sport-App/assets/82510221/d299c4b9-5db4-4c10-b9e2-b1fd0dabe75f)




































