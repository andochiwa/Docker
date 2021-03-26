# 索引

* [名词概念](#1)
* [安装docker](#2)
* [低层原理](#3)
* [常用命令](#4)
* [进阶命令](#5)
* [可视化界面](#6)

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
--name "NAME"		# 容器名字
-d					# 后台方式运行
-it					# 使用交互方式运行，进入容器查看内容
-p					# 指定端口(小写)
-P					# 随机指定端口
-rm					# 用完后删除

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



# Docker镜像

镜像是一种轻量级的，可执行的软件包，用来打包软件运行环境和基于运行环境开发的软件，它包含运行某个软件所需的所有内容，包括代码、运行时库、环境变量、配置文件。

所有应用直接打包成镜像，就可以运行起来

如何得到镜像：

* 从远程仓库下载
* 拷贝
* 自己制作镜像DockerFile

# Docker镜像加载原理

> UnionFS（联合文件系统）

联合文件系统是一种分层，轻量级且高性能的文件系统，它支持对文件系统的修改作为一次提交来一层层的叠加，同时可以将不同目录挂在到同一个虚拟文件系统下。Union文件系统是Docker镜像的基础，镜像可以通过分层来进行集成，基于基础镜像（没有父镜像），可以制作各种具体的应用镜像

特性：一次同时加载多个文件系统，但从外部来看只能看到一个文件系统，联合加载会把各层文件系统叠加起来，这样最终的文件系统会包含所有底层的文件和目录

> 加载原理

docker镜像实际上是由一层一层的文件系统组成（UnionFS）

bootfs（boot file system）主要包含bootloader和kernel，bootloader主要是引导加载kernel，Linux刚启动时会加载bootfs文件系统，在Docker镜像的最低层是bootfs。这一层与我们典型的Linux/Unix系统是一样的，包含boot加载器和内核，当boot加载完成后整个内核就都在内存中，此时内存的使用权已由bootfs转交给内核，系统也会卸载bootfs

rootfs（root file system）在bootfs之上。包含的就是典型Linux系统中的/dev, /proc, /bin, /etc等标准目录和文件。rootfs就是各种不同的操作系统发行版，比如Ubuntu，Centos等。