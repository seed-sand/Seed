# 关于Seed目标管理系统的设想

## 系统模型的建立

根据我们的应用涉及到的对象，可以分解为`用户`和`目标`两个基本单位，在此基础上可以有`目标分组`，首先从概念上描述我们所要做的这个系统，之后分别建模。

用户与目标是一个多对多（many-to-many）关系，即用户可自由创建多个目标，同一目标也可以由多个用户共同完成，同时用户可以关注其他用户的目标完成情况。在此基础上继续细化，目标可以大体分为公开项目由其他用户自由关注或申请参与，以及私有目标，创建者可发送私密邀请给其他用户邀请参与。目标可以被标记为完成状态。

此外，目标内可递归地建立子目标。

值得一提的是，当且仅当一个目标的所有子目标被标记完成的时候才会被标记为完成。

参考：

- [GitHub issues](https://guides.github.com/features/issues/)
- [项目里程碑](http://www.baike.com/wiki/%E9%A1%B9%E7%9B%AE%E9%87%8C%E7%A8%8B%E7%A2%91)

### 用户

#### 属性

目前针对用户的建模已经有非常成熟的解决方案，所以这不是Seed所关注的重点，此处仅罗列用户应有的基本属性：

- id
- username
- password
- email
- Objectives（创建的目标）

#### 交互

此处考虑针对用户的交互以便设计出合理的API。

针对用户这个对象的操作如下所列：

+ 创建

+ 登陆

  值得一提的是，用户也许可以直接由微信授权注册登录。


+ 登出
+ 检索
+ 获取个人信息

### 目标

这是我们应用的核心对象，应当重点考虑。

首先明确一点目标的定义，目标可以是日常计划坚持也可以是某个项目的达成，可以定义为还未达成却想要达成的一件事情。

#### 属性

其文档结构可以表示为：

- 标题
- 描述
- 达成时间
- 优先级
- 可见范围

此外还需要一些附属地标记属性：

- id
- 所有者
- 被分配者集
- 子问题集
- 关注者集（enhancement）
- 评论集

#### 交互

+ 创建
+ 修改
+ 删除
+ 查询
+ 添加用户
+ 移除用户
+ 完成
+ 关注
+ 取消关注
+ 评论

### 目标分组

目标分组的作用是方便用户更好的管理自己的目标，不可递归创建。

#### 属性

+ id


+ 组名
+ 描述
+ 目标集
+ 所有者

#### 交互

+ 创建
+ 删除
+ 修改
+ 查找
+ 添加目标
+ 移除目标

### 评论

评论系统作为一个增强功能，在有余力的情况下完成，其形式很像GitHub issues。

#### 属性

+ id
+ 标题
+ 描述
+ 所有者

#### 交互

+ 创建
+ 修改
+ 删除
+ 查找

### 作为第三方服务（如有余力）

微信、微博、GitHub

## 文档结构

### User

```python
user = {
  "id": str,
  "email": str,
  "wechatOpenId": str,
  "username": str,
  "password": str,
  "created": list(str),
  "joined": list(str)
}
```



### Objective

```python
objective = {
  "id": str,
  "userId": str,
  "groupId": str,
  "title": str,
  "descript": str,
  "deadline": str,
  "priority": int,
  "scope": str,
  "status": str,
  "assignment": list(str),
  "watchedBy": list(str),
  "comments": list(Comment),
}
```



### Objective Group

```python
objective = {
  "id": str,
  "title": str,
  "description": str,
  "objectives": list(str),
  "scope": str
}
```



### comment

```python
comment = {
  "userId": str,
  "content": str
}
```



