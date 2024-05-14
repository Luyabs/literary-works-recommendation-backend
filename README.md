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

## 项目整体架构
![image](https://github.com/Luyabs/literary-works-recommendation-backend/assets/74538732/a3344555-7c3e-498e-a794-06a25da84354)

## 算法后端架构设计
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
## E-R图与数据库设计
![image](https://github.com/Luyabs/literary-works-recommendation-frontend/assets/74538732/722428ff-8047-4494-ae55-af065e8ccfe2)
| 表名                     | 字段                     | 数据类型         | 约束                                    | 解释                       |
|------------------------|------------------------|--------------|---------------------------------------|--------------------------|
| user                   | user_id                | bigint       | primary key                           | 用户id                     |
| <用户>                   | username               | varchar(80)  | unique, not null                      | 用户名                      |
|                        | password               | varchar(80)  | not null                              | 密码                       |
|                        | role                   | int          | default 0                             | 角色{0=普通用户，1=管理}          |
|                        | is_banned              | boolean      | default 0                             | 账户是否被封禁                  |
|                        | is_info_public         | boolean      | default 1                             | 个人信息是否公开                 |
|                        | is_comment_public      | boolean      | default 1                             | 个人评价是否公开                 |
|                        | introduction           | varchar(300) |                                       | 个人简介                     |
|                        |                        |              |                                       |                          |
| collection             | collection_id          | bigint       | primary key                           | 收藏夹id                    |
| <收藏夹>                  | owner_id               | bigint       | 逻辑外键(user:user_id)                    | 所属用户id                   |
|                        | collection_name        | varchar(40)  | unique, not null                      | 收藏夹名                     |
|                        | introduction           | varchar(300) |                                       | 收藏夹简介                    |
|                        | is_public              | boolean      | default 1                             | 是否公开                     |
|                        | is_default_collection  | boolean      | not null                              | 是否为默认收藏夹(仅一个收藏夹可作为默认收藏夹) |
|                        |                        |              |                                       |                          |
| work                   | work_id                | bigint       | primary key                           | 作品id                     |
| <作品>                   | tags                   | varchar(300) | 数组(非单值), index, 冗余存储                  | 标签(字符串格式)                |
|                        | work_name              | varchar(80)  | not null                              | 作品名                      |
|                        | author                 | varchar(80)  |                                       | 作者                       |
|                        | introduction           | varchar(300) |                                       | 作品简介                     |
|                        | publisher              | varchar(80)  |                                       | 出版社                      |
|                        | cover_link             | varchar(300) |                                       | 封面地址(URL)                |
|                        | sum_rating             | int          | default 0, 冗余存储                       | 总评分                      |
|                        | sum_rating_user_number | int          | default 0, 冗余存储                       | 总评价人数                    |
|                        | is_deleted             | boolean      | default 0                             | 是否被逻辑删除                  |
|                        |                        |              |                                       |                          |
| tag                    | tag_id                 | bigint       | primary key                           | 标签id                     |
| <标签>                   | tag_name               | varchar(300) | unique, not null                      | 标签名                      |
|                        |                        |              |                                       |                          |
| record_tag_work        | record_id              | bigint       | primary key                           | 标签关系记录id                 |
| (标签-作品)记录              | tag_id                 | bigint       | index, 逻辑外键(tag:tag_id)               | 标签id                     |
|                        | work_id                | bigint       | index, 逻辑外键(work:work_id)             | 作品id                     |
|                        |                        |              |                                       |                          |
| record_collection_work | record_id              | bigint       | primary key                           | 收藏记录id                   |
| (收藏夹-作品)记录             | collection_id          | bigint       | index, 逻辑外键(collection:collection_id) | 收藏夹id                    |
|                        | work_id                | bigint       | 逻辑外键(work:work_id)                    | 作品id                     |
|                        |                        |              |                                       |                          |
| history_user_work      | history_id             | bigint       | primary key                           | 访问记录id                   |
| (用户-作品)访问记录            | user_id                | bigint       | 逻辑外键(user:user_id)                    | 用户id                     |
|                        | work_id                | bigint       | 逻辑外键(work:work_id)                    | 作品id                     |
|                        | visit_count            | int          | default 1                             | 访问次数                     |
|                        |                        |              |                                       |                          |
| review_user_work       | review_id              | bigint       | primary key                           | 评论id                     |
| (用户-作品)评论记录            | user_id                | bigint       | index, 逻辑外键(user:user_id)             | 用户id                     |
|                        | work_id                | bigint       | index, 逻辑外键(work:work_id)             | 作品id                     |
|                        | rating                 | int          | not null                              | 评分                       |
|                        | content                | varchar(500) |                                       | 评论内容                     |
|                        |                        |              |                                       |                          |
| 补充                     | 所有表均有以下两个字段：           |              |                                       |                          |
|                        | create_time            | datetime     | 自动填充                                  | 创建时间                     |
|                        | update_time            | datetime     | 自动填充                                  | 更新时间                     |


## 用例图与完整需求
![image](https://github.com/Luyabs/literary-works-recommendation-frontend/assets/74538732/7144c98a-ca43-4dfe-be21-c004510b688f)
```text
本项目共有两类参与者：普通用户与管理员。其中普通用户（下简称用户）是平台的主体参与者，可使用整个系统中除了少数系统管理以外的全部功能。管理员是整个平台的管理者，参与整个系统的全部功能。
项目的算法端无需系统的使用者（用户与管理员）管理，在系统部署并运行后会定期自动更新算法模型。
项目包含以下模块：分别为身份认证模块、个人信息管理模块、用户间交互模块、文学作品访问模块、历史记录模块、文学作品评价模块、收藏夹模块与管理员模块。其中除管理员模块仅对管理员开放外，所有模块都可被用户与管理员访问。
身份认证模块包含：登录、注册、登出功能与鉴权。
（1）	登录：用户通过输入正确的用户名与密码得到个人令牌（Token），并于服务端以持久化方式于数据库保存Token。
（2）	注册：用户通过输入用户名、密码与个人简介（此项可选）来生成新的账号。
（3）	登出：用户从服务端与客户端同时注销Token。
（4）	鉴权：鉴权是所有登录、注册外所有功能执行前进行的操作，用于检验用户Token是否合法，并通过Token读取个人信息。
（5）	限制条件：出于安全性考虑，密码在前后端传输过程中进行非对称加密，且禁止由后端向前端传输密码。
个人信息管理模块包含：基本信息修改与设置个人信息公开状态。
（1）	基本信息修改：用户通过输入用户名、密码与个人简介（均可选）覆盖之前的信息。
（2）	设置个人信息公开状态：用户可以设置自身“是否公开自身除用户Id与用户名外的其它个人信息（密码除外）”、“是否公开评论信息”与“是否公开收藏夹信息”状态。
用户间交互模块包含查看他人公开信息功能。
（1）	查看他人信息：可通过用户名或用户Id搜索用户，或通过用户留下的文学作品的评论访问该用户公开的详细信息。以查看他的公开的个人信息、公开的收藏夹信息（可访问具体收藏条项并收藏到自己的收藏夹）与公开的全部评论信息。
（2）	限制条件：这个操作受限于目标用户的个人信息公开状态。
文学作品访问模块包含：通过搜索访问、基于简单推荐算法访问、基于复杂推荐算法访问与访问作品详细信息。
（1）	通过搜索访问：用户可以输入文学作品名、作者信息、标签、出版社与简介（均可选）搜索文学作品摘要信息（包含作品Id、作品名、作者、平均评分与标签（可通过点击作品标签快速查询有相似标签的作品））。
（2）	基于简单推荐算法（非个性化推荐算法）访问：用户可以访问热门（评分最高、评价最多、浏览最多与收藏最多；以排行榜形式展现）与新品作品。
（3）	基于复杂推荐算法访问：用户可以通过基于LFM + 热门物品召回+ FM（或Deep FM）精排序的混合推荐与基于LFM的相似向量推荐算法获取个性化推荐结果。
（4）	访问作品详细信息：用户可以了解某作品是否被系统删除，如果未被删除则可进一步访问作品的完整信息页面。该页面包含：作品的全部摘要信息、与作品相似（具有相似作品向量）的其他作品和所有用户对于该作品的评论与评分（可以筛选评论的评分区间与筛选空白评论；用户无法通过设置“不公开评论信息”状态来隐藏自己在具体文学作品页面下的评论，只能隐藏通过主页访问用户发表过的全部评论）。此外用户可以在此页面对作品进行评价与收藏，如有管理员权限则可修改作品摘要信息与删除该作品。
历史记录模块包含：历史记录查询与新增历史记录。
（1）	历史记录查询：用户可以直接查询自己的历史记录，需要统计用户自身对同一作品的访问次数。
（2）	新增历史记录：用户在访问文学作品详细页面时增加一次对该作品的访问记录。
文学作品评价模块包含：发布评论与查询评论。
（1）	发布评论：用户可以发表对某作品的评论，评论允许不携带文字评价，但必须进行打分。打分的范围在1~5区内，视评分≥4分为用户喜欢该作品，否则视为不喜欢。支持用户对作品做出快速评价（如快速1分表明不喜欢该作品）。用户对作品的评分将直接影响推荐给用户的结果。用户不能重复发表对某作品的评论，也不支持删除先前的评论，但可以对之前自己已发布的评论进行修改。
（2）	查询评论：用户可以直接对自己已发表的所有评论或对某文学作品的全部评论进行查询。也可以查询他人发表过的公开评论。
收藏夹模块包含：收藏夹管理与收藏作品管理
（1）	收藏夹管理：用户可以自由创建、删除、修改自己的收藏夹信息。可以设置收藏夹为默认收藏夹，但默认收藏夹至多只有一个。
（2）	收藏作品管理：用户可以自由收藏文学作品到任意收藏夹或从收藏夹移除收藏记录。支持快速收藏到默认收藏夹功能。当作品被删除后，自动从所有收藏夹删除关于该作品的收藏记录。
管理员模块包含：文学作品管理和用户封禁。
（1）	文学作品管理：管理员可以新建、修改和删除文学作品。删除作品时将标记该作品为逻辑删除，只额外删除收藏记录，并不删除历史记录与评价信息（但不能通过URL、收藏记录和评价记录等方式访问该作品）。
（2）	用户封禁：管理员可以封禁用户，从服务端移除用户Token，被封禁的用户将无法登录。如果该用户在线，则通过移除Token使其离线。
（3）	限制条件：此模块需要求管理员权限。

```
