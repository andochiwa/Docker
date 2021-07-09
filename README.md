# Docker简述

在开发环境中，有可能会遇到：我的电脑上可以运行，怎么部署到服务器上就不能运行了？这类问题，也就是环境的配置导致了各种问题，每一个机器都需要配置环境，每个机器也要部署集群，部署错了是非常的麻烦。有没有办法能在发布项目时自带环境呢？

docker就是解决这个问题，docker可以在打包时带上环境（镜像），其他环境直接下载发布的镜像就可以了，思想就来自于集装箱，实际就是和虚拟机一样的虚拟化技术

[Docker 从入门到实践](https://vuepress.mirror.docker-practice.com/)*

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

## portainer

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



# <span id="10">Commit, push镜像</span>

```shell
docker commit <CONTAINER> NAME		# 提交容器成为一个新的镜像，以后就可以使用修改过的镜像
## 参数
-m "MESSAGE"			# 描述信息
-a "AUTHOR"			# 作者

## 发布镜像
docker login -u USERNAME -p PASSWORD IP:PORT #if private
docker push
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

## FROM 指定基础镜像

所谓定制镜像，那一定是以一个镜像为基础，在其上进行定制。基础镜像是必须指定的。而 `FROM` 就是指定 **基础镜像**，因此一个 `Dockerfile` 中 `FROM` 是必备的指令，并且必须是第一条指令

如果你以 `scratch` 为基础镜像的话，意味着你不以任何镜像为基础，接下来所写的指令将作为镜像第一层开始存在。

##  RUN 执行命令

`RUN` 指令是用来执行命令行命令的。由于命令行的强大能力，`RUN` 指令在定制镜像时是最常用的指令之一。其格式有两种：

- *shell* 格式：`RUN <命令>`，就像直接在命令行中输入的命令一样。刚才写的 Dockerfile 中的 `RUN` 指令就是这种格式。

```bash
RUN echo '<h1>Hello, Docker!</h1>' > /usr/share/nginx/html/index.html
```

- *exec* 格式：`RUN ["可执行文件", "参数1", "参数2"]`，这更像是函数调用中的格式。

既然 `RUN` 就像 Shell 脚本一样可以执行命令，那么我们是否就可以像 Shell 脚本一样把每个命令对应一个 RUN 呢？比如这样：

## COPY 复制文件

格式：

- `COPY [--chown=<user>:<group>] <源路径>... <目标路径>`
- `COPY [--chown=<user>:<group>] ["<源路径1>",... "<目标路径>"]`

和 `RUN` 指令一样，也有两种格式，一种类似于命令行，一种类似于函数调用。

`COPY` 指令将从构建上下文目录中 `<源路径>` 的文件/目录复制到新的一层的镜像内的 `<目标路径>` 位置。比如：

```bash
COPY package.json /usr/src/app/
```

`<源路径>` 可以是多个，甚至可以是通配符，其通配符规则要满足 Go 的 [`filepath.Match` ](https://golang.org/pkg/path/filepath/#Match)规则，如：

```bash
COPY hom* /mydir/
COPY hom?.txt /mydir/
```

`<目标路径>` 可以是容器内的绝对路径，也可以是相对于工作目录的相对路径（工作目录可以用 `WORKDIR` 指令来指定）。目标路径不需要事先创建，如果目录不存在会在复制文件前先行创建缺失目录。

此外，还需要注意一点，使用 `COPY` 指令，源文件的各种元数据都会保留。比如读、写、执行权限、文件变更时间等。这个特性对于镜像定制很有用。特别是构建相关文件都在使用 Git 进行管理的时候。

## ADD 更高级的复制文件

`ADD` 指令和 `COPY` 的格式和性质基本一致。但是在 `COPY` 基础上增加了一些功能。

比如 `<源路径>` 可以是一个 `URL`，这种情况下，Docker 引擎会试图去下载这个链接的文件放到 `<目标路径>` 去。下载后的文件权限自动设置为 `600`，如果这并不是想要的权限，那么还需要增加额外的一层 `RUN` 进行权限调整，另外，如果下载的是个压缩包，需要解压缩，也一样还需要额外的一层 `RUN` 指令进行解压缩。所以不如直接使用 `RUN` 指令，然后使用 `wget` 或者 `curl` 工具下载，处理权限、解压缩、然后清理无用文件更合理。因此，这个功能其实并不实用，而且不推荐使用。

如果 `<源路径>` 为一个 `tar` 压缩文件的话，压缩格式为 `gzip`, `bzip2` 以及 `xz` 的情况下，`ADD` 指令将会自动解压缩这个压缩文件到 `<目标路径>` 去。

## CMD 容器启动命令

`CMD` 指令的格式和 `RUN` 相似，也是两种格式：

- `shell` 格式：`CMD <命令>`
- `exec` 格式：`CMD ["可执行文件", "参数1", "参数2"...]`
- 参数列表格式：`CMD ["参数1", "参数2"...]`。在指定了 `ENTRYPOINT` 指令后，用 `CMD` 指定具体的参数。

之前介绍容器的时候曾经说过，Docker 不是虚拟机，容器就是进程。既然是进程，那么在启动容器的时候，需要指定所运行的程序及参数。`CMD` 指令就是用于指定默认的容器主进程的启动命令的。

在运行时可以指定新的命令来替代镜像设置中的这个默认命令，比如，`ubuntu` 镜像默认的 `CMD` 是 `/bin/bash`，如果我们直接 `docker run -it ubuntu` 的话，会直接进入 `bash`。我们也可以在运行时指定运行别的命令，如 `docker run -it ubuntu cat /etc/os-release`。这就是用 `cat /etc/os-release` 命令替换了默认的 `/bin/bash` 命令了，输出了系统版本信息。

在指令格式上，一般推荐使用 `exec` 格式，这类格式在解析时会被解析为 JSON 数组，因此一定要使用双引号 `"`，而不要使用单引号。

如果使用 `shell` 格式的话，实际的命令会被包装为 `sh -c` 的参数的形式进行执行。比如：

```docker
CMD echo $HOME
```

在实际执行中，会将其变更为：

```docker
CMD [ "sh", "-c", "echo $HOME" ]
```

## ENTRYPOINT 入口点

`ENTRYPOINT` 的格式和 `RUN` 指令格式一样，分为 `exec` 格式和 `shell` 格式。

`ENTRYPOINT` 的目的和 `CMD` 一样，都是在指定容器启动程序及参数。`ENTRYPOINT` 在运行时也可以替代，不过比 `CMD` 要略显繁琐，需要通过 `docker run` 的参数 `--entrypoint` 来指定。

当指定了 `ENTRYPOINT` 后，`CMD` 的含义就发生了改变，不再是直接的运行其命令，而是将 `CMD` 的内容作为参数传给 `ENTRYPOINT` 指令，换句话说实际执行时，将变为：

```bash
<ENTRYPOINT> "<CMD>"
```

## ENV 设置环境变量

格式有两种：

- `ENV <key> <value>`
- `ENV <key1>=<value1> <key2>=<value2>...`

这个指令很简单，就是设置环境变量而已，无论是后面的其它指令，如 `RUN`，还是运行时的应用，都可以直接使用这里定义的环境变量。

```bash
ENV VERSION=1.0 DEBUG=on \
    NAME="Happy Feet"
```

这个例子中演示了如何换行，以及对含有空格的值用双引号括起来的办法，这和 Shell 下的行为是一致的。

## VOLUME 定义匿名卷

格式为：

- `VOLUME ["<路径1>", "<路径2>"...]`
- `VOLUME <路径>`

之前我们说过，容器运行时应该尽量保持容器存储层不发生写操作，对于数据库类需要保存动态数据的应用，其数据库文件应该保存于卷(volume)中。为了防止运行时用户忘记将动态文件所保存目录挂载为卷，在 `Dockerfile` 中，我们可以事先指定某些目录挂载为匿名卷，这样在运行时如果用户不指定挂载，其应用也可以正常运行，不会向容器存储层写入大量数据。

```bash
VOLUME /data
```

这里的 `/data` 目录就会在容器运行时自动挂载为匿名卷，任何向 `/data` 中写入的信息都不会记录进容器存储层，从而保证了容器存储层的无状态化。当然，运行容器时可以覆盖这个挂载设置。比如：

```bash
$ docker run -d -v mydata:/data xxxx
```

在这行命令中，就使用了 `mydata` 这个命名卷挂载到了 `/data` 这个位置，替代了 `Dockerfile` 中定义的匿名卷的挂载配置。

## EXPOSE 声明端口

格式为 `EXPOSE <端口1> [<端口2>...]`。

`EXPOSE` 指令是声明容器运行时提供服务的端口，这只是一个声明，在容器运行时并不会因为这个声明应用就会开启这个端口的服务。在 Dockerfile 中写入这样的声明有两个好处，一个是帮助镜像使用者理解这个镜像服务的守护端口，以方便配置映射；另一个用处则是在运行时使用随机端口映射时，也就是 `docker run -P` 时，会自动随机映射 `EXPOSE` 的端口。

要将 `EXPOSE` 和在运行时使用 `-p <宿主端口>:<容器端口>` 区分开来。`-p`，是映射宿主端口和容器端口，换句话说，就是将容器的对应端口服务公开给外界访问，而 `EXPOSE` 仅仅是声明容器打算使用什么端口而已，并不会自动在宿主进行端口映射。

## WORKDIR 指定工作目录

格式为 `WORKDIR <工作目录路径>`。

使用 `WORKDIR` 指令可以来指定工作目录（或者称为当前目录），以后各层的当前目录就被改为指定的目录，如该目录不存在，`WORKDIR` 会帮你建立目录。

一些初学者常犯的错误是把 `Dockerfile` 等同于 Shell 脚本来书写，这种错误的理解还可能会导致出现下面这样的错误：

```bash
RUN cd /app
RUN echo "hello" > world.txt
```

如果将这个 `Dockerfile` 进行构建镜像运行后，会发现找不到 `/app/world.txt` 文件，或者其内容不是 `hello`。原因其实很简单，在 Shell 中，连续两行是同一个进程执行环境，因此前一个命令修改的内存状态，会直接影响后一个命令；而在 `Dockerfile` 中，这两行 `RUN` 命令的执行环境根本不同，是两个完全不同的容器。这就是对 `Dockerfile` 构建分层存储的概念不了解所导致的错误。

之前说过每一个 `RUN` 都是启动一个容器、执行命令、然后提交存储层文件变更。第一层 `RUN cd /app` 的执行仅仅是当前进程的工作目录变更，一个内存上的变化而已，其结果不会造成任何文件变更。而到第二层的时候，启动的是一个全新的容器，跟第一层的容器更完全没关系，自然不可能继承前一层构建过程中的内存变化。

因此如果需要改变以后各层的工作目录的位置，那么应该使用 `WORKDIR` 指令。

```bash
WORKDIR /app

RUN echo "hello" > world.txt
```

如果你的 `WORKDIR` 指令使用的相对路径，那么所切换的路径与之前的 `WORKDIR` 有关：

```bash
WORKDIR /a
WORKDIR b
WORKDIR c

RUN pwd
```

`RUN pwd` 的工作目录为 `/a/b/c`。

## USER 指定当前用户

格式：`USER <用户名>[:<用户组>]`

`USER` 指令和 `WORKDIR` 相似，都是改变环境状态并影响以后的层。`WORKDIR` 是改变工作目录，`USER` 则是改变之后层的执行 `RUN`, `CMD` 以及 `ENTRYPOINT` 这类命令的身份。

注意，`USER` 只是帮助你切换到指定用户而已，这个用户必须是事先建立好的，否则无法切换。

```bash
RUN groupadd -r redis && useradd -r -g redis redis
USER redis
RUN [ "redis-server" ]
```

如果以 `root` 执行的脚本，在执行期间希望改变身份，比如希望以某个已经建立好的用户来运行某个服务进程，不要使用 `su` 或者 `sudo`，这些都需要比较麻烦的配置，而且在 TTY 缺失的环境下经常出错。建议使用 [`gosu` ](https://github.com/tianon/gosu)。

```bash
# 建立 redis 用户，并使用 gosu 换另一个用户执行命令
RUN groupadd -r redis && useradd -r -g redis redis
# 下载 gosu
RUN wget -O /usr/local/bin/gosu "https://github.com/tianon/gosu/releases/download/1.12/gosu-amd64" \
    && chmod +x /usr/local/bin/gosu \
    && gosu nobody true
# 设置 CMD，并以另外的用户执行
CMD [ "exec", "gosu", "redis", "redis-server" ]
```



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

```yaml
container_name: docker-web-container
```

> 注意：指定容器名称后，该服务将无法扩展，因为 Docker 不允许多个容器具有相同的名称

## build

指定 `Dockerfile` 所在文件夹的路径（可以是绝对路径，或者相对 docker-compose.yml 文件的路径）。 `Compose` 将会利用它自动构建这个镜像，然后使用这个镜像。

```yaml
version: '3'
services:

  webapp:
    build: ./dir
```

也可以使用 `context` 指令指定 `Dockerfile` 所在文件夹的路径。

使用 `dockerfile` 指令指定 `Dockerfile` 文件名。

使用 `arg` 指令指定构建镜像时的变量。

```yaml
version: '3'
services:

  webapp:
    build:
      context: ./dir                     # 指定Dockerfile文件目录
      dockerfile: Dockerfile-alternate   # 指定Dockerfile文件的文件名
      args:
        buildno: 1
```

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

## env_file

从文件中获取环境变量，可以为单独的文件路径或列表，**一般用于敏感信息的隐藏**。

如果通过 `docker-compose -f FILE` 方式来指定 Compose 模板文件，则 `env_file` 中变量的路径会基于模板文件路径。

如果有变量名称与 `environment` 指令冲突，则按照惯例，以后者为准。

```yaml
env_file: .env

env_file:
  - ./common.env
  - ./apps/web.env
  - /opt/secrets.env
```

环境变量文件中每一行必须符合格式，支持 `#` 开头的注释行。

```yaml
# common.env: Set development environment
PROG_ENV=development
```

## depends_on

解决容器的依赖、启动先后的问题。以下例子中会先启动 `redis` `db` 再启动 `web`

```yaml
version: '3'

services:
  web:
    build: .
    depends_on:
      - db
      - redis

  redis:
    image: redis

  db:
    image: postgres
```

> 注意：`web` 服务不会等待 `redis` `db` 「完全启动」之后才启动。

## healthcheck

通过命令检查容器是否健康运行。

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost"]
  interval: 1m30s
  timeout: 10s
  retries: 3
```

# Docker Compose 命令

## 命令对象与格式

对于 Compose 来说，大部分命令的对象既可以是项目本身，也可以指定为项目中的服务或者容器。如果没有特别的说明，命令对象将是项目，这意味着项目中所有的服务都会受到命令影响。

执行 `docker-compose [COMMAND] --help` 或者 `docker-compose help [COMMAND]` 可以查看具体某个命令的使用格式。

`docker-compose` 命令的基本的使用格式是

```bash
docker-compose [-f=<arg>...] [options] [COMMAND] [ARGS...]
```

## 命令选项

- `-f, --file FILE` 指定使用的 Compose 模板文件，默认为 `docker-compose.yml`，可以多次指定。
- `-p, --project-name NAME` 指定项目名称，默认将使用所在目录名称作为项目名。
- `--verbose` 输出更多调试信息。
- `-v, --version` 打印版本并退出。

## 命令使用说明

### build

格式为 `docker-compose build [options] [SERVICE...]`。

构建（重新构建）项目中的服务容器。

服务容器一旦构建后，将会带上一个标记名，例如对于 web 项目中的一个 db 容器，可能是 web_db。

可以随时在项目目录下运行 `docker-compose build` 来重新构建服务。

选项包括：

- `--force-rm` 删除构建过程中的临时容器。
- `--no-cache` 构建镜像过程中不使用 cache（这将加长构建过程）。
- `--pull` 始终尝试通过 pull 来获取更新版本的镜像。

### config

验证 Compose 文件格式是否正确，若正确则显示配置，若格式错误显示错误原因。

### down

此命令将会停止 `up` 命令所启动的容器，并移除网络

### exec

进入指定的容器。

### help

获得一个命令的帮助。

### images

列出 Compose 文件中包含的镜像。

### kill

格式为 `docker-compose kill [options] [SERVICE...]`。

通过发送 `SIGKILL` 信号来强制停止服务容器。

支持通过 `-s` 参数来指定发送的信号，例如通过如下指令发送 `SIGINT` 信号。

```bash
$ docker-compose kill -s SIGINT
```

### logs

格式为 `docker-compose logs [options] [SERVICE...]`。

查看服务容器的输出。默认情况下，docker-compose 将对不同的服务输出使用不同的颜色来区分。可以通过 `--no-color` 来关闭颜色。

该命令在调试问题的时候十分有用。

### pause

格式为 `docker-compose pause [SERVICE...]`。

暂停一个服务容器。

### port

格式为 `docker-compose port [options] SERVICE PRIVATE_PORT`。

打印某个容器端口所映射的公共端口。

选项：

- `--protocol=proto` 指定端口协议，tcp（默认值）或者 udp。
- `--index=index` 如果同一服务存在多个容器，指定命令对象容器的序号（默认为 1）。

### ps

格式为 `docker-compose ps [options] [SERVICE...]`。

列出项目中目前的所有容器。

选项：

- `-q` 只打印容器的 ID 信息。

### pull

格式为 `docker-compose pull [options] [SERVICE...]`。

拉取服务依赖的镜像。

选项：

- `--ignore-pull-failures` 忽略拉取镜像过程中的错误。

### push

推送服务依赖的镜像到 Docker 镜像仓库。

### restart

格式为 `docker-compose restart [options] [SERVICE...]`。

重启项目中的服务。

选项：

- `-t, --timeout TIMEOUT` 指定重启前停止容器的超时（默认为 10 秒）。

### rm

格式为 `docker-compose rm [options] [SERVICE...]`。

删除所有（停止状态的）服务容器。推荐先执行 `docker-compose stop` 命令来停止容器。

选项：

- `-f, --force` 强制直接删除，包括非停止状态的容器。一般尽量不要使用该选项。
- `-v` 删除容器所挂载的数据卷。

### run

格式为 `docker-compose run [options] [-p PORT...] [-e KEY=VAL...] SERVICE [COMMAND] [ARGS...]`。

在指定服务上执行一个命令。

例如：

```bash
$ docker-compose run ubuntu ping docker.com
```

将会启动一个 ubuntu 服务容器，并执行 `ping docker.com` 命令。

默认情况下，如果存在关联，则所有关联的服务将会自动被启动，除非这些服务已经在运行中。

该命令类似启动容器后运行指定的命令，相关卷、链接等等都将会按照配置自动创建。

两个不同点：

- 给定命令将会覆盖原有的自动运行命令；
- 不会自动创建端口，以避免冲突。

如果不希望自动启动关联的容器，可以使用 `--no-deps` 选项，例如

```bash
$ docker-compose run --no-deps web python manage.py shell
```

将不会启动 web 容器所关联的其它容器。

选项：

- `-d` 后台运行容器。
- `--name NAME` 为容器指定一个名字。
- `--entrypoint CMD` 覆盖默认的容器启动指令。
- `-e KEY=VAL` 设置环境变量值，可多次使用选项来设置多个环境变量。
- `-u, --user=""` 指定运行容器的用户名或者 uid。
- `--no-deps` 不自动启动关联的服务容器。
- `--rm` 运行命令后自动删除容器，`d` 模式下将忽略。
- `-p, --publish=[]` 映射容器端口到本地主机。
- `--service-ports` 配置服务端口并映射到本地主机。
- `-T` 不分配伪 tty，意味着依赖 tty 的指令将无法运行。

### scale

格式为 `docker-compose scale [options] [SERVICE=NUM...]`。

设置指定服务运行的容器个数。

通过 `service=num` 的参数来设置数量。例如：

```bash
$ docker-compose scale web=3 db=2
```

将启动 3 个容器运行 web 服务，2 个容器运行 db 服务。

一般的，当指定数目多于该服务当前实际运行容器，将新创建并启动容器；反之，将停止容器。

选项：

- `-t, --timeout TIMEOUT` 停止容器时候的超时（默认为 10 秒）。

### start

格式为 `docker-compose start [SERVICE...]`。

启动已经存在的服务容器。

### stop

格式为 `docker-compose stop [options] [SERVICE...]`。

停止已经处于运行状态的容器，但不删除它。通过 `docker-compose start` 可以再次启动这些容器。

选项：

- `-t, --timeout TIMEOUT` 停止容器时候的超时（默认为 10 秒）。

### top

查看各个服务容器内运行的进程。

### unpause

格式为 `docker-compose unpause [SERVICE...]`。

恢复处于暂停状态中的服务。

### up

格式为 `docker-compose up [options] [SERVICE...]`。

该命令十分强大，它将尝试自动完成包括构建镜像，（重新）创建服务，启动服务，并关联服务相关容器的一系列操作。

链接的服务都将会被自动启动，除非已经处于运行状态。

可以说，大部分时候都可以直接通过该命令来启动一个项目。

默认情况，`docker-compose up` 启动的容器都在前台，控制台将会同时打印所有容器的输出信息，可以很方便进行调试。

当通过 `Ctrl-C` 停止命令时，所有容器将会停止。

如果使用 `docker-compose up -d`，将会在后台启动并运行所有的容器。一般推荐生产环境下使用该选项。

默认情况，如果服务容器已经存在，`docker-compose up` 将会尝试停止容器，然后重新创建（保持使用 `volumes-from` 挂载的卷），以保证新启动的服务匹配 `docker-compose.yml` 文件的最新内容。如果用户不希望容器被停止并重新创建，可以使用 `docker-compose up --no-recreate`。这样将只会启动处于停止状态的容器，而忽略已经运行的服务。如果用户只想重新部署某个服务，可以使用 `docker-compose up --no-deps -d <SERVICE_NAME>` 来重新创建服务并后台停止旧服务，启动新服务，并不会影响到其所依赖的服务。

选项：

- `-d` 在后台运行服务容器。
- `--no-color` 不使用颜色来区分不同的服务的控制台输出。
- `--no-deps` 不启动服务所链接的容器。
- `--force-recreate` 强制重新创建容器，不能与 `--no-recreate` 同时使用。
- `--no-recreate` 如果容器已经存在了，则不重新创建，不能与 `--force-recreate` 同时使用。
- `--no-build` 不自动构建缺失的服务镜像。
- `-t, --timeout TIMEOUT` 停止容器时候的超时（默认为 10 秒）。

### version

格式为 `docker-compose version`。

打印版本信息