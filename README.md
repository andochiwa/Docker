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
docker version 					# 显示docker的版本信息
docker info 					# 显示docker的系统信息
docker <command> --help  			# 万能命令

# 镜像命令
docker images					# 查看镜像
docker search <REPOSITORY[:tag]>		# 搜索镜像
docker pull <REPOSITORY[:tag]>			# 下载镜像
docker rmi <REPOSITORY | ID ...>		# 删除镜像

# 容器命令
docker run <PARAMETER> image			# 启动容器
## 参数说明
--name "NAME"				# 容器名字
-d					# 后台方式运行
-it					# 使用交互方式运行，进入容器查看内容
-p YOUR_PORT:DOCKER_PORT		# 指定端口(小写)
-P					# 随机指定端口
-rm					# 用完后删除
-v YOUR_PATH:CONTAINER_PATH		# 挂载

exit					# 停止并退出容器
ctrl + P + Q				# 退出容器
docker ps					# 列出所有容器
docker rm CONTAINER				# 删除容器，不能删除正在运行的容器，如果要强制删除加上-f
docker ps -a -q|xargs docker rm	# 删除所有容器

docker start CONTAINER				# 启动容器
docker restart CONTAINER			# 重启容器
docker stop CONTAINER				# 停止正在运行的容器
docker kill CONTAINER				# 强制停止正在运行的容器
```

# <span id="5">Docker进阶命令</span>

```shell
# 坑，容器后台运行时必须有一个前台进程，否则容器会自动停止，可以使用-dit

docker logs <OPTIONS> CONTAINER			# 显示日志
## 参数
-tf						# 显示日志时间戳、动态显示日志
--tall number	# 显示日志的条数

docker top <OPTIONS> CONTAINER 			# 显示容器信息
docker inspect CONTAINER			# 显示容器的元数据
docker exec -it CONTAINER /bin/bash		# 进入正在运行的容器
docker attach CONTAINER				# 进入正在运行的容器

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
-m "MESSAGE"			# 描述信息
-a "AUTHOR"			# 作者
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

Docker内置三种网络模式，运行容器时，可以使用–net标志来指定容器应连接到哪些网络

1. host：容器将不会虚拟出自己的网卡，配置自己的IP等，而是使用宿主机的IP和端口。
2. Container：创建的容器不会创建自己的网卡，配置自己的IP，而是和一个指定的容器共享IP、端口范围。
3. Bridge（默认）：此模式会为每一个容器分配、设置IP等，并将容器连接到一个docker0虚拟网桥，通过docker0网桥以及Iptables nat表配置与宿主机通信。

## 桥接模式

> 桥接模式（Bridge），使用的技术是**veth-pair**技术

1. 只要安装了docker，都会有一个网卡docker0，每启动一个容器，docker0子网就会给容器分配一个ip
2. 每启动一个容器也会分配一个网卡，这些网卡都是成对出现的（veth-pair），一端连着协议，一端彼此相连，由这个特性，veth-pair充当一个桥梁，连接着各种虚拟网络设备
3. docker0相当于交换机，由veth-pair协议与每个容器相连，容器之间的交互通过docker0交换机转发（需要解析），而docker0自身与物理网卡直连
4. 只要容器删除，对应的一对网桥也随之删除

在bridge模式下，连在同一网桥上的容器可以相互通信（若出于安全考虑，也可以禁止它们之间通信，方法是在DOCKER_OPTS变量中设置–icc=false，这样只有使用–link才能使两个容器通信）。

Docker可以开启容器间通信（意味着默认配置–icc=true），也就是说，宿主机上的所有容器可以不受任何限制地相互通信，这可能导致拒绝服务攻击。进一步地，Docker可以通过–ip_forward和–iptables两个选项控制容器间、容器和外部世界的通信


## 自定义网络

使用自定义网络来控制哪些容器可以互相通信，还可以自动解析DNS容器名称到IP地址。Docker提供了创建这些网络的默认网络驱动程序，可以创建一个新的Bridge网络，Overlay或Macvlan网络。还可以创建一个网络插件或远程网络进行完整的自定义和控制。

```shell
docket network create --driver bridge --subnet <xxx.xxx.xxx.xxx/xx> --gateway <xxx.xxx.xxx.xxx> NET_NAME
# --driver 网络模式
# --subnet 子网号
# --gateway 网关号

连接时，只需要在run命令后面加上--net NET_NAME
```

这样的话，当容器处在同一个自定义网络时，不用解析也能相互访问

## 网络连通

当容器处于不同网关时，如何连通两个容器？

```shell
docker network connet GATEWAY CONTAINER
```

连通之后，CONTAINER就会直接放到GATEWAY下，即为一个容器两个IP



# Docker Compose

## 简介

[官方文档](https://docs.docker.com/compose/)

Compose 项目是 Docker 官方的开源项目，负责实现对 Docker 容器集群的快速编排，从功能上看，跟 OpenStack 中的 Heat 十分类似

Docker Compose 的定位是【定义和运行多个Docker容器的应用（Defining and running multi-container Docker Applications）】

我们知道使用一个`Dockerfile`模板文件，可以让用户很方便的定义一个单独的应用容器。然而在实际场景中，经常会碰到需要多个容器相互配合来完成某项任务的情况。例如要实现一个 Web 项目，除了 Web 服务容器本身，往往还需要再加上数据库服务容器，负载均衡容器等。

Docker Compose 恰好满足了这样的需求，它允许用户通过一个单独的`docker-compose.yml`模板文件来定义一组相关联的应用容器为一个项目

Docker Compose 中有两个重要的概念

* 服务（service）：一个应用的容器，实际上可以包含若干运行相同镜像的容器实例
* 项目（project）：由一组关联的应用容器组成的一个完整业务单元，在`docker-compose.yml`中定义

Docker Compose 的默认管理对象是项目，通过子命令对项目中的一组容器进行便捷地生命周期管理

Docker Compose 由 Python 编写，实现上调用了 Docker 服务提供的 API 来对容器进行管理，因此，只要所操作的平台支持 Docker API，就可以利用 Docker Compose 来进行编排管理

## 安装

```shell
sudo curl -L "https://github.com/docker/compose/releases/download/1.28.6/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

# Docker Compose 模板文件

模板文件是使用 Compose 的核心，涉及到的指令关键字比较多，这里大部分指令和`docker run`相关参数的含义都是类似的

默认的模板文件名为`docker-compose.yml`，格式为 yaml

```yaml
version: "3"

services:
  webapp:
    image: IMAGE:TAG
    ports:
      - "80:80"
    volumes:
      - "/data"
```

注意每个服务都必须通过`image`指令指定镜像或`build`指令（需要 Dockerfile）等自动构建生成镜像

如果使用`build`指令，在`Dockerfile`中设置的选项（例如: CMD, EXPOSE, VOLUME, ENV）会被自动获取，无需在`docker-compose.yml`中重复设置

各个指令的用法：

## image

指定为镜像名称或镜像 ID。如果镜像在本地不存在，Compose 将尝试拉取这个镜像

```yaml
image: jenkins/jenkins:latest
image: abcdfafas # image hash
```

## ports

暴露端口信息

使用宿主端口：容器端口`HOST:CONTAINER`的格式，或者仅仅指定容器的端口（宿主会随机选择端口）都可以

```yaml
ports:
  - "3000"
  - "8080:8080"
  - "127.0.0.1:8000:8080"
```

注意，当使用`HOST:CONTAINER`格式来映射端口时，如果使用的容器端口小于 60 且没有放到引号里，可能会得到错误的结果。因为 yaml 会自动解析 `xx:yy`这种数字格式为60禁止。为避免出现这种问题，建议数字串都采用引号包起来的字符串格式

## volumes

数据卷所挂载路径设置，可以设置为宿主机路径`HOST:CONTAINER`或者数据卷名称`VOLUME:CONTAINER`，并且可以设置访问模式`HOST:CONTAINER:ro`，该指令中路径支持相对路径

```yaml
volumes:
  - /var/lib/mysql
  - /opt/maven:/usr/local/maven
  - ~/configs:/etc/configs/:ro
```

如果路径为数据卷名称，必须在文件中设置数据卷

```yaml
services:
  mysql:
    image: mysql:8.0
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:   # 声明指定的卷名，compose自动创建该卷名但是会在之前加入项目名
    external: # 是否使用自定义卷名
      false # 默认false，true确定，这样就不会在之前加入项目名，启动服务之前必须手动创建
```

## networks

配置容器连接的网络

```yaml
services:
  mysql:
    image: mysql:8.0
    networks:
      - net
          
networks:
  net:      # 定义上面服务使用的网桥，默认 bridge 模式
    external:
      true  # 指定网桥名称不带项目名 注意：网桥必须存在
```

## container_name

指定容器名称，默认会使用`项目名称_服务名称_序号`的格式

> 注意：指定容器名称后，该服务将无法扩展，因为 Docker 不允许多个容器具有相同的名称

## enviroment

设置环境变量，可以使用数组或字典两种格式

只给定名称的变量会自动获取运行 Compose 主机上对应变量的值，可以用来防止泄露不必要的数据

```yaml
services:
  mysql:
    image: mysql:8.0
    enviroment:
      - MYSQL_ROOT_PASSWORD=root
```

## command

覆盖容器启动后默认执行的命令

```yaml
services:
  volumes:
    - redisdata:/data
  command: "redis-server --appendonly yes"
volumes:
  redisdata:
```

