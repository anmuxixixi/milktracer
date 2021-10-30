# milktracer
基于HyperledgerFabric实现的牛奶溯源项目


食品安全问题多发已经成为一个社会安全亟待解决的问题，由于区块链具有溯源功能，可以将区块链应用在食品溯源领域，保证监视食品的整个生产流程。本实验从以上观点出发，基于linux开源项目Hyperledger制作一个牛奶生产溯源项目！
本人对于开源的牛奶溯源项目进行了一些改进和功能的补充，原作者的项目地址为：https://gitee.com/jaline/fabricase/tree/master/food-traceability，在此十分感谢作者的智能合约代码！☺

**本人主要实现的内容有：**
- 前端使用Vue-Element-Admin进行开发，方便用户通过前端界面实现牛奶产品的溯源
- 后端使用SpringBoot进行开发，内嵌JAVA-SDK，将前端用户的操作通过SDK传递给后台Linux运行的Fabric网络
- 从0到1搭建了一整套完整的Hyperledger Fabric网络
- 本项目是单机多节点的Fabric网络，对于使用集群Kafka并不满足（学生实在买不起服务器了）

**环境描述：**
- Hyperledger Fabric  1.4.7
- Docker  20.10.9
- Go   1.17.1
- SpringBoot 2.5.5
- Mysql   8.0.25
- Vue-Element-Admin  4.4.0
