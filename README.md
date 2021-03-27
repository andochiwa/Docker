# Docker简述

在开发环境中，有可能会遇到：我的电脑上可以运行，怎么部署到服务器上就不能运行了？这类问题，也就是环境的配置导致了各种问题，每一个机器都需要配置环境，每个机器也要部署集群，部署错了是非常的麻烦。有没有办法能在发布项目时自带环境呢？

docker就是解决这个问题，docker可以在打包时带上环境（镜像），其他环境直接下载发布的镜像就可以了，思想就来自于集装箱，实际就是和虚拟机一样的虚拟化技术



# <span id="1">名词概念</span>

## 镜像（Image） 

docker镜像就像一个类，可以通过这个类创建容器服务（实例），通过这个镜像可以创建多个容器（多实例）

## 容器（Container）

Docker利用容器技术，独立运行一个或一组应用，通过镜像创建，可以把容器理解为一个微型的linux系统

## 仓库（Repository）

存放镜像的地方，仓库也分为公有仓库和私有仓库。



# <span id="2">安装docker</span>

[看官方文档](https://docs.docker.com/engine/)，如果在国内建议使用阿里云镜像，安装完后还可以去阿里云配置阿里云镜像加速



# <span id="3">底层原理</span>

## Docker工作原理

Docker是一个Client - Server结构的系统，守护进程运行在主机上，通过Socket客户端访问。

DockerServer接收到DockerClient的指令，就会执行这个命令

## Docker比VM快的原因

1. Docker有着比VM更少的抽象层
2. Docker利用的是宿主机的内核，VM需要是Guest OS

所以Docker新建容器时，不需要像VM一样重新加载一个OS内核，而VM是加载Guest OS，速度是分级的，Docker利用宿主机的内核，速度是秒级的



# <span id="4">Docker常用命令</span>

```bash
# 帮助命令
docker version 				# 显示docker的版本信息
docker info 				# 显示docker的系统信息
docker <command> --help  	# 万能命令

# 镜像命令
docker images							# 查看镜像
docker search <REPOSITORY[:tag]>		# 搜索镜像
docker pull <REPOSITORY[:tag]>			# 下载镜像
docker rmi <REPOSITORY | ID ...>		# 删除镜像

# 容器命令
docker run <PARAMETER> image	# 启动容器
## 参数说明
--name "NAME"						# 容器名字
-d									# 后台方式运行
-it									# 使用交互方式运行，进入容器查看内容
-p YOUR_PORT:DOCKER_PORT			# 指定端口(小写)
-P									# 随机指定端口
-rm									# 用完后删除
-v YOUR_PATH:CONTAINER_PATH			# 挂载

exit				# 停止并退出容器
ctrl + P + Q		# 退出容器
docker ps						# 列出所有容器
docker rm CONTAINER				# 删除容器，不能删除正在运行的容器，如果要强制删除加上-f
docker ps -a -q|xargs docker rm	# 删除所有容器

docker start CONTAINER			# 启动容器
docker restart CONTAINER		# 重启容器
docker stop CONTAINER			# 停止正在运行的容器
docker kill CONTAINER			# 强制停止正在运行的容器
```

# <span id="5">Docker进阶命令</span>

```shell
# 坑，容器后台运行时必须有一个前台进程，否则容器会自动停止，可以使用-dit

docker logs <OPTIONS> CONTAINER			# 显示日志
## 参数
-tf				# 显示日志时间戳、动态显示日志
--tall number	# 显示日志的条数

docker top <OPTIONS> CONTAINER 			# 显示容器信息
docker inspect CONTAINER				# 显示容器的元数据
docker exec -it CONTAINER /bin/bash		# 进入正在运行的容器
docker attach CONTAINER					# 进入正在运行的容器

docker cp CONTAINER:PATH CPPATH			# 拷贝容器内的文件到主机上 
```



# <span id="6">Docker可视化界面</span>

## portainer（不推荐）

docker的图形化管理工具，提供一个后台面板供我们操作

```shell
docker run -d -p 8088:9000 --restart=always -v /var/run/docker.sock:/var/run/docker.sock --privileged=true portainer/portainer
```



# <span id="7">Docker镜像</span>

镜像是一种轻量级的，可执行的软件包，用来打包软件运行环境和基于运行环境开发的软件，它包含运行某个软件所需的所有内容，包括代码、运行时库、环境变量、配置文件。

所有应用直接打包成镜像，就可以运行起来

如何得到镜像：

* 从远程仓库下载
* 拷贝
* 自己制作镜像DockerFile

# <span id="8">Docker镜像加载原理</span>

> UnionFS（联合文件系统）

联合文件系统是一种分层，轻量级且高性能的文件系统，它支持对文件系统的修改作为一次提交来一层层的叠加，同时可以将不同目录挂在到同一个虚拟文件系统下。Union文件系统是Docker镜像的基础，镜像可以通过分层来进行集成，基于基础镜像（没有父镜像），可以制作各种具体的应用镜像

特性：一次同时加载多个文件系统，但从外部来看只能看到一个文件系统，联合加载会把各层文件系统叠加起来，这样最终的文件系统会包含所有底层的文件和目录

> 加载原理

docker镜像实际上是由一层一层的文件系统组成（UnionFS）

bootfs（boot file system）主要包含bootloader和kernel，bootloader主要是引导加载kernel，Linux刚启动时会加载bootfs文件系统，在Docker镜像的最低层是bootfs。这一层与我们典型的Linux/Unix系统是一样的，包含boot加载器和内核，当boot加载完成后整个内核就都在内存中，此时内存的使用权已由bootfs转交给内核，系统也会卸载bootfs

rootfs（root file system）在bootfs之上。包含的就是典型Linux系统中的/dev, /proc, /bin, /etc等标准目录和文件。rootfs就是各种不同的操作系统发行版，比如Ubuntu，Centos等。



# <span id="9">分层理解</span>

下载镜像的时候，可以看到日志输出，是在一层一层的下载

最大的好处是资源共享。多个镜像都从相同的Base镜像构建而来，那么宿主机只需要在磁盘上保留一份Base镜像，同时内存中也只需要加载一份Base内存镜像，这样就可以为所有容器服务，而且镜像的每一层都可以被共享。

所有的Docker镜像都起始于一个基础镜像层，当进行修改或增加新内容时，就会在当前镜像层之上创建新的镜像层。

Docker通过存储引擎（快照机制）来实现镜像层堆栈，并保证多镜像层对外展示为统一的文件系统。

Linux上可用的存储引擎有AUTF、Overlay2、Device Mapper、Btrfs和ZFS。每种存储引擎都基于Linux中对应的文件系统或块设备技术，并且每种存储引擎都有独有的性能特典

Docker在windows上仅支持windowsfilter一种存储引擎，该引擎基于NTFS文件系统纸上实现分层和COW



# <span id="10">Commit镜像</span>

```shell
docker commit <CONTAINER> NAME		# 提交容器成为一个新的镜像，以后就可以使用修改过的镜像
## 参数
-m "MESSAGE"	# 描述信息
-a "AUTHOR"		# 作者
```



# <span id="11">容器数据卷</span>

docker的理念是把应用和环境打包成一个镜像，如果数据都在容器中，那么如果容器删除，数据就会丢失，我们需要做一些数据持久化

容器之间有一个数据共享技术，Docker容器中产生的数据，同步到本地

这就是卷技术，挂载的目录将在我们容器内的目录挂载到Linux上

## 使用数据卷

在run命令后面加上-v参数

```shell
docker run -v YOUR_PATH:CONTAINER_PATH
```

这样在删除容器后，数据也不会丢失

## 具名挂载和匿名挂载

匿名挂载 只写了容器内路径，**这样挂载的路径会是一串哈希码**

```shell
docker run -v CONTAINER_PATH
```

具名挂载 写了容器内和容器外路径，**这样挂载的路径是具体路径**

拓展：在CONTAINER_PATH路径加上:ro / :rw等可以变为只读 / 读写



# DockerFile

DockerFile是用来构建docker镜像文件的命令参数脚本。

DockerFile是面向开发的，以后发布项目做镜像，就需要编写dockerfile文件

**DockerImages：**通过DockerFile构建生成的镜像，最终发布和运行的产品

步骤：

1. 编写一个dockerfile文件
2. docker build构建成为一个镜像
3. docker run运行镜像
4. docker push发布镜像(Docker Hub, aliyun)

## 构建过程

**基础知识：**

1. 每个保留关键字（指令）都是大写
2. 从上到下顺序执行
3. 每一个指令都会创建提交一个新的镜像层并提交

## 指令

1. `FORM` 指定基础镜像，表示从这里开始构建
2. `MAINTAINER` 指定镜像维护者信息，姓名+ 邮箱
3. `RUN` 镜像构建时需要运行的命令
4. `ADD` COPY文件，会自动解压
5. `WORKDIR` 镜像的工作目录
6. `VOLUMN` 设置卷，挂载的主机目录
7. `EXPOSE` 指定暴露端口
8. `CMD` 指定容器启动时运行的命令，只有最后一个生效，可被替代
9. `ENTRYPOINT` 指定容器启动时运行的命令，可以追加命令
10. `ONBUILD` 当构建一个被继承DOCKERFILE，这个时候会运行ONBUILD指令，触发指令
11. `COPY` 类似`ADD`，将文件拷贝到镜像中
12. `ENV` 构建时设置环境变量

## 发布镜像

> DockerHub

1. 在DockerHub注册账号

   ```shell
   docker login -u USERNAME -p PASSWORD
   ```

2. 在服务器上提交镜像

   ```shell
   docker push
   ```

> aliyun

1. 创建命名空间
2. 创建容器对象
3. 然后看阿里云上的流程就行了



# Docker网络