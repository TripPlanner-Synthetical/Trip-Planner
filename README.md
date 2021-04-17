# Trip-Planner
An Android-based application on travelling

2021/4/17
关于注册登录登出接口的说明：

运行方式和环境：
1、拉项目
2、配置Mongodb  在idea->database->点击加号引入mongodb数据源，在本地mongo新建一个名为User的collection
3、本地开启redis，端口默认
4、找到.\Server\Trip Planner\user\src\main\java\com\tp\UserApplication 运行


请求方式post
登录：http://localhost:8090/user/login
注册：http://localhost:8090/user/register
登出：http://localhost:8090/user/logout

参数请求方式如下：
![5f4c8b3f63df4b51693bb85b8c52371](https://user-images.githubusercontent.com/45162720/115113633-d8ced100-9fbd-11eb-8f15-b70fa2e7ceb7.png)
![a3b629c3aa0dbf528eb9eb501582a06](https://user-images.githubusercontent.com/45162720/115113634-d9fffe00-9fbd-11eb-98fe-796858f1e8d9.png)
![67b2554146f1f154e33bcdd15e3a4cd](https://user-images.githubusercontent.com/45162720/115113639-dc625800-9fbd-11eb-953a-9c0b9692a748.png)


