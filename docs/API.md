# API

API设计文档

## TOLD List

+ Patch更新资源单一属性

## User API

### User - 创建用户

`POST`

```
/users
```

**权限** ：未登入状态

**参数**

| 字段       | 类型     | 描述   | 是否必要 |
| -------- | ------ | ---- | ---- |
| username | String | 用户名  | 是    |
| password | String | 密码   | 是    |
| email    | String | 邮件地址 | 是    |

成功（201）

| 字段   | 类型     | 描述         |
| ---- | ------ | ---------- |
| code | Number | 0          |
| user | User   | 用户文档（不含密码） |

请求失败（400）

| 名称      | 类型       | 描述     |
| ------- | -------- | ------ |
| require | String[] | 必填字段缺失 |



| 名称       | 类型     | 描述     |
| -------- | ------ | ------ |
| username | Object | 用户名重复  |
| email    | Object | 电子邮件重复 |



### User - 修改密码

`PATCH`

```
/users/password
```

**权限**：用户已登入

参数

| 字段           | 类型     | 描述   | 是否必要 |
| ------------ | ------ | ---- | ---- |
| userId       | String | 用户ID | 是    |
| old_password | String | 旧密码  | 是    |
| new_password | String | 新密码  | 是    |

成功（201）

| 字段   | 类型     | 描述         |
| ---- | ------ | ---------- |
| code | Number | 0          |
| user | User   | 用户文档（不含密码） |

请求失败（400）

| 名称      | 类型    | 描述     |
| ------- | ----- | ------ |
| require | Array | 必填字段缺失 |

| 名称           | 类型     | 描述   |
| ------------ | ------ | ---- |
| old_password | String | 密码错误 |



###  User - 用户个人信息

`GET`

```
/users/profile
```

**权限**：全体

参数

成功（200）

| 字段   | 类型     | 描述         |
| ---- | ------ | ---------- |
| code | Number | 0          |
| user | User   | 用户文档(个人信息) |



### User - 用户检索（废弃）

> 应用Spring `Pageable`接口

> 用户检索的用处是什么？--ASKED BY FROGGY

`GET`

```
/users
```

**权限**：

参数

| 字段    | 类型     | 描述    | 是否必要 |
| ----- | ------ | ----- | ---- |
| page  | Number | 分页码   | 否    |
| limit | Number | 每页容量  | 是    |
| sort  | String | 排序关键字 | 否    |
| key   | String | 关键字   | 否    |

成功（200）

| 字段    | 类型     | 描述     |
| ----- | ------ | ------ |
| code  | Number | 0      |
| users | User[] | 用户文档列表 |



### User - 用户登出

`GET`

```
/users/log-out
```

**权限**：用户已登入

成功（200）

| 字段   | 类型     | 描述   |
| ---- | ------ | ---- |
| code | Number | 0    |



### User - 用户登录

`POST`

```
/users/log-in
```

**权限**：用户未登入

参数

| 字段       | 类型     | 描述   | 是否必要 |
| -------- | ------ | ---- | ---- |
| username | String | 用户名  | 是    |
| password | String | 密码   | 是    |

成功（201）

| 字段   | 类型     | 描述   |
| ---- | ------ | ---- |
| code | Number | 0    |
| user | User   | 用户文档 |

请求失败（400）

| 名称       | 类型     | 描述     |
| -------- | ------ | ------ |
| username | String | 用户名不存在 |
| password | String | 密码错误   |



### User - 用户详细信息

`GET`

```
/users/:userId
```

> 用户详细信息与用户个人信息的差别？？？---FROGGY

**权限**：用户已登入

参数

| 字段     | 类型     | 描述   | 是否必要 |
| ------ | ------ | ---- | ---- |
| userId | String | 用户ID | 是    |

成功（200）

| 字段   | 类型     | 描述   |
| ---- | ------ | ---- |
| code | Number | 0    |
| user | User   | 用户文档 |



## Objective API

### Objective - 创建目标

`POST`

```
/objectives
```

**权限**：用户已登入

参数

| 字段          | 类型       | 描述   | 是否必要 |
| ----------- | -------- | ---- | ---- |
| title       | String   | 标题   | 是    |
| description | String   | 描述   | 否    |
| deadline    | DateTime | 截止时间 | 否    |
| priority    | Number   | 优先级  | 否    |
| scope       | Boolean  | 范围   | 是    |

成功（201）

| 字段        | 类型        | 描述   |
| --------- | --------- | ---- |
| code      | Number    | 0    |
| objective | Objective | 目标文档 |

请求失败（400）

| 名称      | 类型    | 描述     |
| ------- | ----- | ------ |
| require | Array | 必填字段缺失 |



### Objective - 删除目标

`DELETE`

```
/objectives/:objectiveId
```

**权限**：用户已登入

参数

| 字段          | 类型     | 描述   | 是否必要 |
| ----------- | ------ | ---- | ---- |
| objectiveId | String | 目标ID | 是    |

成功（204）

请求失败（401）

| 字段          | 类型     | 描述     |
| ----------- | ------ | ------ |
| objectiveId | String | 删除权限不够 |



### Objective - 修改目标

> 该如何只修改单一属性呢

`PUT`

```
/objectives/:objectiveId
```

**权限**：用户已登入

参数

| 字段          | 类型       | 描述   | 是否必要 |
| ----------- | -------- | ---- | ---- |
| objectiveId | String   | 目标ID | 是    |
| title       | String   | 标题   | 否    |
| description | String   | 描述   | 否    |
| deadline    | DateTime | 截止时间 | 否    |
| priority    | Number   | 优先级  | 否    |
| scope       | Boolean  | 范围   | 否    |
| status      | String   | 完成状态 | 否    |

成功（201）

| 字段         | 类型        | 描述      |
| ---------- | --------- | ------- |
| code       | Number    | 0       |
| objectives | Objective | 被修改目标文档 |

请求失败（400&401）

| 名称          | 类型     | 描述     |
| ----------- | ------ | ------ |
| Require     | Array  | 必填字段缺失 |
| objectiveId | String | 修改权限不够 |



### Objective - 检索目标（废弃）

`GET`

```
/objectives
```

**权限**：用户已登入

参数

| 字段    | 类型     | 描述    | 是否必要 |
| ----- | ------ | ----- | ---- |
| page  | Number | 分页码   | 否    |
| limit | Number | 每页容量  | 是    |
| sort  | String | 排序关键字 | 否    |
| key   | String | 检索关键字 | 否    |
| scope | String | 检索范围  | 是    |

成功（200）

| 字段         | 类型          | 描述     |
| ---------- | ----------- | ------ |
| code       | Number      | 0      |
| objectives | Objective[] | 目标文档列表 |

请求失败（400）

| 字段   | 类型     | 描述    |
| ---- | ------ | ----- |
| key  | String | 无相关内容 |



### Objective - 目标详细信息

`GET`

```
/objectives/:objectiveId
```

**权限**：全体

参数

| 字段          | 类型     | 描述   | 是否必要 |
| ----------- | ------ | ---- | ---- |
| objectiveId | String | 目标ID | 是    |

成功（200）

| 字段        | 类型        | 描述   |
| --------- | --------- | ---- |
| code      | Number    | 0    |
| objective | Objective | 目标文档 |



### Objective - 分享目标

`GET`

```
/objectives/:objectiveId/assignments
```

参数

> TODO: 如何区分所分享目标的可见范围？

| 字段          | 类型     | 描述   | 是否必要 |
| ----------- | ------ | ---- | ---- |
| objectiveId | String | 目标ID | 是    |

成功（200）

| 字段        | 类型     | 描述   |
| --------- | ------ | ---- |
| code      | Number | 0    |
| shareLink | String | 分享链接 |



### Objective - 加入目标

`PUT`

```
/objectives/:objectiveId/assignments
```

**权限**：用户已登入

参数

| 字段          | 类型     | 描述   | 是否必要 |
| ----------- | ------ | ---- | ---- |
| objectiveId | String | 目标ID | 是    |
| userId      | String | 用户ID | 是    |

成功（201）

| 字段        | 类型        | 描述   |
| --------- | --------- | ---- |
| code      | Number    | 0    |
| objective | Objective | 目标文档 |

错误（401）

| 字段          | 类型     | 描述     |
| ----------- | ------ | ------ |
| objectiveId | String | 加入权限不够 |



### Objective - 离开目标

`DELETE`

```
/objectives/:objectiveId/assignments
```

权限：用户已登入

参数

| 字段          | 类型     | 描述   | 是否必要 |
| ----------- | ------ | ---- | ---- |
| objectiveId | String | 目标ID | 是    |

成功（200）

| 字段        | 类型        | 描述   |
| --------- | --------- | ---- |
| code      | Number    | 0    |
| objective | Objective | 目标文档 |



### Objective - 评论目标

`POST`

```
/objectives/:objectiveId/comments
```

权限：用户已登入

参数

| 字段          | 类型     | 描述   | 是否必要 |
| ----------- | ------ | ---- | ---- |
| objectiveId | String | 目标ID | 是    |
| content     | String | 评论正文 | 是    |

成功（201）

| 字段      | 类型      | 描述   |
| ------- | ------- | ---- |
| code    | Number  | 0    |
| comment | Comment | 评论文档 |



## ObjectiveList API

### ObjectiveList - 创建分组

`POST`

```
/objectiveLists
```

权限：用户已登入

参数

| 字段          | 类型     | 描述   | 是否必要 |
| ----------- | ------ | ---- | ---- |
| title       | String | 目标组名 | 是    |
| description | String | 描述   | 否    |
| scope       | String | 可见范围 | 是    |

成功（201）

| 字段            | 类型            | 描述     |
| ------------- | ------------- | ------ |
| code          | Number        | 0      |
| objectiveList | ObjectiveList | 目标分组文档 |

请求失败（400）

| 名称      | 类型    | 描述     |
| ------- | ----- | ------ |
| Require | Array | 必填字段缺失 |

### ObjectiveList - 删除分组

`DELETE`

```
/objectiveLists/:objectiveListId
```

参数

| 字段              | 类型     | 描述   | 是否必要 |
| --------------- | ------ | ---- | ---- |
| objectiveListId | String | 分组ID | 是    |

成功（204）

请求失败（401）

| 字段              | 类型     | 描述     |
| --------------- | ------ | ------ |
| objectiveListId | String | 删除权限不够 |



### ObjectiveList - 修改分组

`PUT`

```
/objectiveLists/:objectiveListId
```

权限：用户已登入

参数

| 字段              | 类型     | 描述   | 是否必要 |
| --------------- | ------ | ---- | ---- |
| objectiveListId | String | 分组ID | 是    |
| title           | String | 分组名  | 否    |
| description     | String | 分组描述 | 否    |
| scope           | String | 可见范围 | 否    |

成功（200）

| 字段            | 类型            | 描述         |
| ------------- | ------------- | ---------- |
| code          | Number        | 0          |
| objectiveList | ObjectiveList | 被修改的目标分组文档 |

请求失败（400）

| 名称      | 类型    | 描述     |
| ------- | ----- | ------ |
| Require | Array | 必填字段缺失 |

请求失败（401）

| 字段              | 类型     | 描述     |
| --------------- | ------ | ------ |
| objectiveListId | String | 修改权限不够 |

### ObjectiveList - 检索分组（废弃）

`GET`

```
/objectiveLists
```

参数

| 字段    | 类型     | 描述    | 是否必要 |
| ----- | ------ | ----- | ---- |
| page  | Number | 分页码   | 否    |
| limit | Number | 每页容量  | 是    |
| sort  | String | 排序关键字 | 否    |
| key   | String | 检索关键字 | 否    |
| scope | String | 检索范围  | 是    |

成功（200）

| 字段            | 类型              | 描述     |
| ------------- | --------------- | ------ |
| code          | Number          | 0      |
| objectiveList | ObjectiveList[] | 分组文档列表 |



###  ObjectiveList - 分组详细信息

`GET`

```
/objectiveLists/:objectiveListId
```

参数

| 字段              | 类型     | 描述   | 是否必要 |
| --------------- | ------ | ---- | ---- |
| objectiveListId | String | 分组ID | 是    |

成功（200）

| 字段            | 类型            | 描述   |
| ------------- | ------------- | ---- |
| code          | Number        | 0    |
| objectiveList | ObjectiveList | 分组文档 |



### ObjectiveList - 添加目标

`PUT`

```
/objectiveLists/:objectiveListId/objective
```

参数

| 字段              | 类型     | 描述   | 是否必要 |
| --------------- | ------ | ---- | ---- |
| objectiveListId | String | 分组ID | 是    |
| objectiveId     | String | 目标ID | 是    |

成功（201）

| 字段            | 类型            | 描述   |
| ------------- | ------------- | ---- |
| code          | Number        | 0    |
| objectiveList | ObjectiveList | 分组文档 |

请求失败（401）

| 字段              | 类型     | 描述     |
| --------------- | ------ | ------ |
| objectiveListId | String | 添加权限不够 |

### ObjectiveList - 移除目标

`DELETE`

```
/objectiveLists/:objectiveListId/objective
```

参数

| 字段              | 类型     | 描述   | 是否必要 |
| --------------- | ------ | ---- | ---- |
| objectiveListId | String | 分组ID | 是    |
| objectiveId     | String | 目标ID | 是    |

成功（200）

| 字段            | 类型            | 描述   |
| ------------- | ------------- | ---- |
| code          | Number        | 0    |
| objectiveList | ObjectiveList | 分组文档 |

请求失败（401）

| 字段              | 类型     | 描述     |
| --------------- | ------ | ------ |
| objectiveListId | String | 移除权限不够 |