## 基于混合推荐算法的文学作品推荐系统
1. 前端 https://github.com/Luyabs/literary-works-recommendation-frontend
2. `你在这` 业务后端 https://github.com/Luyabs/literary-works-recommendation-backend
3. 算法端 https://github.com/Luyabs/literary-works-recommendation-algorithm

## 项目环境
> MySQL: 8.0  (或许5.0也行？)  
> Redis: 7.0  
> JDK: 17 (>= Jdk 8)  
> Spring Boot：2.7  
> 项目所需的依赖都记录在pom.xml中，运行前记得通过pom.xml更新依赖，如果更新失败则考虑maven换源  

## 项目运行
1. 启动前配置：在本项目中你需要**创建一个新文件**，文件位置：src/main/resources/application-dev.yaml
```yaml
# 这是文件的内容
server:
  port: 8080  # 端口号

spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource  # DRUID
    url: jdbc:mysql://localhost:3306/literary_works_recommendation_platform # 你的MySQL数据库地址 [需更改]
    username: root  # 你的MySQL用户名 [需更改 但大概率是root]
    password: PASSSSSSSSWORD  # 你的MySQL密码 [需更改]
  redis:
    host: 127.0.0.1  # 你的Redis所在服务器的IP地址 [需更改 本地运行就不用改了]
    port: 6379   # Redis端口 [需更改 但大概率是6379]
    database: 0   # Redis数据库选择 
    password: PASSSSSSSSWORD  # Redis密码 [需更改]

algorithm-backend:
  url: http://localhost:8000/  # 算法后端所在地址
```
2. 数据库导入：运行本项目的SQL文件导入数据表 literary_works_recommendation_platform.sql。数据库的数据可通过算法端的SQL脚本从数据集导入到数据库，并利用算法端的爬虫扩充数据集的属性，需要同时带数据和结构的SQL文件可以联系我。
4. **启动**： 用最喜欢的IDE或者在命令行敲代码，运行src/main/java/edu/shu/abs/LiteraryWorksRecommendationBackendApplication.java文件
5. 热更新：启动后按Ctrl + F9

## 项目结构
|   目录    |            |              |                       |                 |            注释                |
|-----------|------------|--------------|-----------------------|-----------------|--------------------------------|
| src/main/ | java/      | edu/shu/abs/ | common/               | aop/            | 切面编程 (仅实现系统日志)                 |
|           | ...        | ...          | ...                   | authentication/ | 鉴权 (Token生成 + ThreadLocal线程副本) |
|           | ...        | ...          | ...                   | base/           | Controller与Service基类           |
|           | ...        | ...          | ...                   | exception/      | 自定义异常与全局异常处理                   |
|           | ...        | ...          | ...                   | interceptor/    | 拦截器 (K4J文档 + Token鉴权拦截器)       |
|           | ...        | ...          | ...                   | 其他文件            | 统一响应数据类、元数据填充、封装的分页对象         |
|           |            |              |                       |                 |                                |
|           | ...        | ...          | config/               |                 | 一些配置类 带有@Configuration注解       |
|           |            |              |                       |                 |                                |
|           | ...        | ...          | constant/             |                 | 常量的枚举类                         |
|           |            |              |                       |                 |                                |
|           | ...        | ...          | controller/           | 普通controller    | 与业务功能有关的controller             |
|           | ...        | ...          | ...                   | algorithm/      | 与算法端有关的controller              |
|           |            |              |                       |                 |                                |
|           | ...        | ...          | entity/               |                 | 实体类                            |
|           |            |              |                       |                 |                                |
|           | ...        | ...          | mapper/               |                 | mapper层 与数据库交互                 |
|           |            |              |                       |                 |                                |
|           | ...        | ...          | service/              | 普通Service       | 与业务功能有关的Service接口              |
|           | ...        | ...          | ...                   | impl/           | 实现上述接口的Service实现类              |
|           | ...        | ...          | ...                   | algorithm/      | 与推荐算法主动调度有关的Service            |
|           | ...        | ...          | ...                   | scheduled_job/  | 与定时更新推荐模型有关的Service            |
|           |            |              |                       |                 |                                |
|           | ...        | ...          | vo/                   |                 | 从entity中增删属性的视图对象              |
|           |            |              |                       |                 |                                |
|           | ...        | ...          | LiteraryW…Application |                 | 项目启动类                          |
|           |            |              |                       |                 |                                |
|           | resources/ | META-INF/    |                       |                 | 可忽略                            |
|           | ...        | static/      |                       |                 | 静态资源(仅1张图片)                    |
|           | ...        | 其余yaml文件     |                       |                 | 项目配置类                          |
|           |            |              |                       |                 |                                |
| src/test/ | java/      | edu/shu/abs  | CodeGenerator         |                 | 由数据表直接创建简要文件的代码生成器             |

## 架构设计
![image](https://github.com/Luyabs/literary-works-recommendation-backend/assets/74538732/00b6108f-8cff-46e1-a052-175d9955ac9d)
```text
3.5.1 普通业务功能设计
由于大部分业务功能重复性高、且逻辑较为简单（无非是围绕数据库的增删改查），可参照3.3.3节中的流程直接编码，无需做过多详细设计。此处仅举一例来简述这类业务功能的流程。
	以“新增文学作品为例”：首先需在Work Controller创建一个方法，方法需指定一个URI，此处为“/work”，并指定用于响应HTTP.POST请求，接收来自前端的参数并调用Work Service的保存文学作品方法。接着在Work Service实现该方法：首先检查调用该方法的用户是否为管理员，如果不是，则抛出无权访问的异常，交由异常处理层处理，并结束；如果是管理员则继续。接着判断参数中作品名是否为空，如为空则抛掷异常，交由异常处理层处理，并结束；非空则继续。接着将参数中的作品标签（字符串）按空格符切分，加载成标签列表，如果此处标签个数大于8则抛掷异常，交由异常处理层处理，并结束;否则调用Work Mapper类的方法将标签保存到数据库的tag表，最后将新增的文学作品则调用Work Mapper类的方法保存到work表中。Work Mapper类在ORM框架的协助下可以简单的通过数据库连接工具来执行SQL代码。

3.5.2 鉴权机制
本系统的基于token鉴权。当用户登录成功时，将在服务端生成token（基于UUID生成随机字符串，字符串重复概率趋于0）。随后将token以响应的方式返回给前端（浏览器），并存储到浏览器本地。之后浏览器发出的每次请求都会在请求头中携带此token；同时在服务端也会把该token保存至Redis，在Redis中以哈希（Hash）的形式存储，其中键为拼接的字符串："token4auth:" + 该token，值也是哈希表，值中键"user_id"用来存储该token对应的用户id；键"role"用来存储用户身份（普通用户或管理员），随后为Redis上的该token设置过期时间（14天）。
当用户执行登录、注册以外的功能时，会通过拦截器Auth Interceptor，对请求进行拦截，如果该请求为HTTP.OPTION类型，则表明是跨域的不带信息的请求，允许直接放行；接着会检查该请求头中是否携带token，如果未携带token或Redis中找不到token，则拦截该请求。接着取出token对应的哈希值：{user_id: __, role:__}，如果值不存在，则说明发生异常，拦截请求；如果值中user_id在Redis保存的黑名单（键: "blacklist4auth"，值以集合方式存储）中，则说明用户被封禁，从Redis中删除该token并拦截请求；否则读取值（user_id于role）并保存到该线程的副本（ThreadLocal）中，以便在service中处理请求时能快速通过工具类（UserInfo）访问到用户id与用户身份。
在用户登出时，会直接删除Redis和浏览器上保存的Token信息。
在用户被封禁时，会在Redis中的黑名单集合（Set，键为字符串"blacklist4auth"）中加入用户这名被封禁用户的id，并重置黑名单过期时间。虽然不能直接注销该用户所有在线的token，但可以通过这种黑名单机制来实现在线踢出的效果，只要该用户请求服务端，则会通过黑名单机制移除这名用户请求时的token。

3.5.3 统一异常处理
系统采用统一异常处理，对于拦截器之外的所有异常，均于ControllerAdvice（即ExceptionHandler模块）中对不同的异常做不同处理，拦截器中的异常将单独处理。对于大部分异常（如自定义异常、文件不存在异常、参数传入错误等异常），都会将一部分的异常信息（如数据库）以response形式返回给前端。对于小部分异常，如NullPointerException，发生时必然是后端的代码问题，不会告知前端异常的具体内容，而是以日志的形式记录下来。
统一异常处理有助于业务逻辑的简化，在业务逻辑中，假设代码无bug，则可以认为Service层向上层（普通的Controller）返回的值必是正确结果。因为当检测到错误条件时，都会以异常的形式抛出交给ExceptionHandler处理，如3.5.1中检测到作品名为空时，直接抛出自定义异常，交给处理自定义异常的ExceptionHandler处理，并不会在Service层继续执行该方法。
Mapper层发生的异常（如SQL执行错误、数据库连接失败异常，数据库字段超长异常、数据库违反Unique约束等异常）会连续向上传递异常，也可认为是间接交给ExceptionHandler处理。有时Controller层在接收参数时也会发生异常，也会由ExceptionHandler进行处理。

3.5.4 系统日志
在本系统采用非侵入型日志记录，借助框架提供的动态代理机制实现，具体设计为Log Aspect类。当执行Service层方法时，会记录当前调用的方法名、携带的参数、方法所在类与访问当前方法的用户信息，并生成日志；对于发生的异常会通过3.5.3节的ExceptionHandler记录在日志中。
具体通过Spring Boot框架提供的切面编程注解实现。原理为动态代理，即在运行时将生成日志的方法拼接到原理要执行的方法上。

3.5.5 算法服务调度
于前文3.3.1节与3.3.3节提到了业务后端通过HTTP请求从算法后端获取推荐服务。本系统从算法后端获得的算法服务共四种：检查算法后端是否处于训练状态、执行增量训练任务（定时执行）、获取个性化推荐结果、获取与物品相似的其他物品。
需注意最后一项“通过LFM算法获取与物品相似的其他物品”由于相似物品较为固定，且每次请求也会使算法后端增加负担。因此设计为会将结果缓存到Redis中，缓存过期时间为35分钟，这样同一个用户访问同一个物品的相似物品时，不必重复从算法后端中取得推荐结果，只需要从缓存中取出结果。
项目中Recommend Service与Scheduled Service模块分别负责主动请求和定时请求算法后端的任务，通过RestTemplate工具类发出HTTP请求与处理响应结果。

3.5.6 其他杂项
在图3.7中还有一部分模块未提到：Knife4J文档、元数据填充（AutoFill）。其中Knife4J文档会自动生成可交互的后端接口测试文档，元数据填充通过ORM框架实现在更新时和新增时修改数据表中指定的字段，均由第三方库加简单编码实现。
此外，考虑到系统的安全性，对密码采用非对称加密存储，并设计3.5.2节中的鉴权等方式提升系统安全性。
```
