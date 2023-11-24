# AutumnFramework
对spring的拙劣的模仿

## 注意事项:
- cglib不支持java17以及之后的版本了,降低jdk版本
- 编译结束后方法形参名称不再保留,虽然可以加入编译参数解决,但是为了泛用性选择了形参注解注入形参,mapper接口层同理

## 打个广告:
大四没事干,找了个java实习一个月2000,想跳槽不干了,大连的hr有没有想联系我的 邮箱:3139196862@qq.com


## 已经实现:
- socket实现的网络服务,简单的支持网页输出,GET,POST方法传参,controller声明形参直接注入
- url-method映射,实现url与controller方法的路由表
- 由三级缓存组成的ioc容器,貌似可以解决循环依赖的问题,但是谁写循环依赖的代码一定是想挨揍了
- 非常弱的依赖注入实现,可以实现@autowired标注的字段的自动注入,以及@value注入配置文件,还可以注入接口的实现类,基本上和springboot写法一样了
- cglib实现的aop,稍微解决了代理类注解擦除的问题,可以正常依赖注入
- mybaits的简易实现,使用jdk动态代理接口,实现参数映射以及实体类映射
- 加入了责任链模式的过滤器,使用@order声明顺序
- 观察者模式实现的崩溃重启服务

## 未来打算实现:

- 解决代码耦合严重的问题,再下去就要臭不可闻了
- 手写的mybaits增加 增删改的功能
- 实现文件上传的功能 (半实现)
- 修正icon获取不到的问题 (已经解决)
- 实现json输出的功能,这个可能要引入别的依赖了
- 尝试实现@bean功能,添加配置类,配置跨域 (配置类已实现)

## 更长远的想法:
- 加入beandefinition
- 加入真正可用的aop

## 人生目标:
- 找到月薪3000的实习
- 找个美女对象

## 更新记录:

### 2023/11/23
- 现在Aop定义权开放给用户
- 拦截后处理切面逻辑也交给用户,同时隐藏了cglib的实现,只需要关心逻辑


### 2023/11/22
- 完成了配置类的注入,当用户实现autumnmvcconfig接口便可以更改框架的部分逻辑,例如默认登陆页面
- 条件注解的雏形加入
- 完善了POST方法,可以获取body了
- 修改了request对象,暴露了一些新的接口

### 2023/11
- 实现了注解扫描器,扫描controller实现url-method映射表
- 改进了Ioc容器,增加了注解标识的字段的依赖注入功能
- 改进了di逻辑,可以注入配置文件和字段了,仿的mybaits mapper层也能注入了
- 改进了di逻辑,可以为接口自动注入实现类
- 改进了Ioc容器,三级缓存被加入,解决循环依赖的问题
- 改进了Ioc的第三级缓存,可以注入Cglib创建的代理
- 解决代理子类注解丢失的情况,动态代理也可以依赖注入了
- 实现了一些框架默认操作,默认首页,默认Icon等
- 增加了视图层,可以返回HTML了
- 不再要求方法一定要加入request对象作为形参
- 实现了Controller形参注入器,直接在参数上写需要的字段,框架解析Url自动注入
- 实现了类似Mybatis的#{}拼接Sql,接口声明参数直接映射
- 增加了几个异常类,更直观定位问题
- 反射方法报错重启服务,程序不再直接死掉

### 2023/10
- 实现了套接字实现的简单web服务器,可以在web上输出一些字
- 实现了过滤器

### 2023/6
- 实现了对Jdbc的简易封装
- 实现对接口的代理
- 实现了代理工厂,生产创建好的代理类

### 2023/5
- 实现了简易的Ioc容器,使用容器实现单例模式
- 实现了对JavaBean的字段注入(@Value注解标记)

### 2022/?
- 了解到了Java反射







### 感谢:
没有Gpt4写代码寸步难行