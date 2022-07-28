# 项目名称
java mysql高校返校新冠疫情排查系统+论文+代码讲解视频+开题报告+所需软件（包远程安装配置）

# 系统介绍
不同角色登录系统后将拥有不同的功能模块，界面。学生返校后将录入自己返校的交通信息：学生所在宿舍区、返程车次、出发地区、到达地区、返程时间。系统根据你录入的返校数据，后台对接用友疫情api，和用友提供的同程大数据进行比对查询，如果有和患者同程，系统将改学生的返校风险等级评定为高，否则评定为无。学生返校后，每天都应该提交自己的体温数据，主要包含学生的体温、学生呼吸情况是否正常。对学生的体温风险划分为无、中、高三个等级。如果学生体温有发烧症状，则风险等级为高，如果学生无发烧症状，但是有呼吸疾病，风险等级将评定为中，如果学生既无发烧又无呼吸疾病，风险等级将评定为无。最后系统将综合学生返校交通数据和学生体温数据，综合判断出学生感染疫情风险的高低情况。感染疫情风险等级取体温数据和同程数据的风险评定的最高等级。

辅导员登录后可以查看学院学生的综合风险评定，如果学院有确诊同学，辅导员可以更改学生状态为确诊。治愈后可改为治愈，系统保存治愈记录。同时辅导员可以查看全校的体温和同程数据，及时了解学生情况。

疫情管理员可以管理全校学生的确诊数据。

大家都可以看到校园确诊地图，确诊数据将在地图上进行标识。校内新冠疫情大数据分析完美适配大屏幕，可以在学校大屏上进行展示，主要分析学生体温数据增长情况，学校各个风险等级人数、各个宿舍区风险情况、学生返校地区分析，和车次分析等….

# 环境需要

1.运行环境：最好是java jdk 1.8，我们在这个平台上运行的。其他版本理论上也可以。\
2.IDE环境：IDEA，Eclipse,Myeclipse都可以。推荐IDEA;\
3.tomcat环境：Tomcat 7.x,8.x,9.x版本均可\
4.硬件环境：windows 7/8/10 1G内存以上；或者 Mac OS； \
5.数据库：MySql 5.7版本；\
6.是否Maven项目：否；

# 技术栈

1. 后端：Spring+SpringMVC+Mybatis\
2. 前端：JSP+CSS+JavaScript+jQuery

# 使用说明

1. 使用Navicat或者其它工具，在mysql中创建对应名称的数据库，并导入项目的sql文件；\
2. 使用IDEA/Eclipse/MyEclipse导入项目，Eclipse/MyEclipse导入时，若为maven项目请选择maven;\
若为maven项目，导入成功后请执行maven clean;maven install命令，然后运行；\
3. 将项目中springmvc-servlet.xml配置文件中的数据库配置改为自己的配置;\
4. 运行项目，在浏览器中输入http://localhost:8080/ 登录

# 高清视频演示

https://ym.maptoface.com/archives/43203


​