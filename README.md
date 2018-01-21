# springboot.mongo.gridfs
springboot+mongo构建你自己的文件服务器。开箱即用，无论是移动端还是web都可以使用。


## 1 你需要mongo环境
## 2 发布
### a mvn install 打包，并把jar放入publish文件夹
![输入图片说明](https://gitee.com/uploads/images/2018/0121/183317_8568f775_1032061.jpeg "1111.jpg")
## 3 运行
### a 修改publish下application.properties配置为自己mongo环境
![输入图片说明](https://gitee.com/uploads/images/2018/0121/184025_61a8bdfd_1032061.jpeg "2222.jpg")
### b windows环境执行start.bat,linux环境执行start.sh
### c http://localhost:8088/访问，如下图便成功
![输入图片说明](https://gitee.com/uploads/images/2018/0121/184849_95abad47_1032061.jpeg "333.jpg")
## 4 前端调用
### a 个人封装的普通jquery调用（也可以写适合自己的其它js调用）
![输入图片说明](https://gitee.com/uploads/images/2018/0121/185132_4dc2d23f_1032061.jpeg "444.jpg")

## 5 controller 层主要方法
![输入图片说明](https://gitee.com/uploads/images/2018/0121/185630_f86fd769_1032061.jpeg "0000000.jpg")