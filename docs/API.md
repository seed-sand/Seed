# API

API设计文档

## User API

### 创建用户

`POST`

```
/user
```

**参数**

| 字段       | 类型     | 描述   |
| -------- | ------ | ---- |
| username | String | 用户名  |
| password | String | 密码   |
| email    | String | 邮件地址 |

成功（201）

| 字段   | 类型     | 描述   |
| ---- | ------ | ---- |
| code | Number | 0    |
| user | User   | 用户文档 |

请求失败（412）

| 名称      | 类型    | 描述     |
| ------- | ----- | ------ |
| require | Array | 必填字段缺失 |



| 名称       | 类型     | 描述     |
| -------- | ------ | ------ |
| username | Object | 用户名重复  |
| email    | Object | 电子邮件重复 |



###  检索用户

`GET`

