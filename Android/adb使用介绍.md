# Android 工具：ADB 使用总结

ADB全称Android Debug Bridge，Android 开发中， 我们在开发过程中经常使用这个工具来操作Android系统，是 Android 开发者必须掌握的。

### 功能介绍

ADB主要功能有:

1. 在Android设备上运行Shell(命令行)

2. 管理模拟器或设备的端口映射

3. 在计算机和设备之间上传/下载文件

4. 将电脑上的本地APK软件安装至Android模拟器或设备上

### 使用配置

一个这么常用工具， Google 早就在 Android SDK 中帮我们集成了，就放在\android-sdk-windows\platform-tools这个目录下面，我们只需要配置一下环境变量就可以使用了。作为开发者，配置环境变量这种小儿科我就不赘述了......

### ADB 用法

#### 查询模拟器或手机状态

查看 adb 服务端连接的模拟器或手机可以帮助更好的使用 adb 命令，这可以通过 devices 命令来列举当前连接的设备：

> adb devices

执行结果是 adb 为每一个设备输出以下状态信息：

> 序列号(serialNumber)：由 adb 创建用于唯一标识设备的字符串，格式是 <设备类型>-<端口号>，例如： emulator-5554

> 连接状态(state)，其值是可能是下面的任意一种：

> offline — 未连接或未响应

> device — 表示设备已经连接到服务端。但需要注意的是，这个状态并不表示 Android 系统已经完全启动起来并且可操作，因为系统在启动的过程中就已经连接 adb ，但这个状态是正常的可操作状态。

> no device – 表示没有任何设备连接(楼主测试过程中没有碰到过 no device 的情况，没连接设备就没任何输出)

#### 操作指定模拟器或手机

如果有多个模拟器或手机正在运行，当使用 adb 命令的时候就需要指定目标设备，这可以通过使用 -s 选项参数实现，用法如下：

> adb -s < serialNumber > < command >

> // 例如在 emulator-5556 设备上安装应用：

> adb -s emulator-5556 install xxxx.apk

这里补充一点， Google 官方给出在多设备的情况下，不用 -s 参数指定目标设备的快捷方式。

> adb -e install xxxx.apk

> //同理，如果有多个设备，但只有一个真机，可以使用如下命令快速发送命令

> adb -d install xxxx.apk

#### 安装应用

使用 adb install 命令可以从开发用电脑中复制应用程序并且安装到模拟器或手机上， adb install 命令必须指定待安装的.apk文件的路径。

> adb install [-lrtsdg] < path_to_apk >

> (-l : 锁定该程序)
> 
> (-r : 重新安装该程序，保留应用数据)
> 
> (-t : allow test packages)
> 
> (-s : 将应用安装到 SD卡，不过现在手机好像都没有 SD卡 了吧)
> 
> (-d : 允许降版本号安装，当然只有 debug 包才能使用)
> 
> (-g : 安装完默认授予所有运行时权限)

#### 卸载应用

上面介绍了安装应用命令，既然有安装应用的命令，那当然有卸载应用的命令。卸载应用命令的格式如下：

> // < package > 表示要卸载应用的包名

> adb uninstall [-k] < package > (-k:不删除程序运行所产生的数据和缓存目录)

#### 与模拟器或手机传输文件

使用 adb 命令 pull 和 push 能从 Android 设备拷贝或复制文件到 Android 设备。跟 install 命令不同，pull 和 push 命令允许拷贝和复制文件到任何位置。

* pull

> adb pull [-a] < remote_path > < local_path > (-a:保留文件时间戳及属性)

举个栗子，我想把应用中的数据库文件复制到本地目录下：
> adb pull sdcard/contacts_app.db


* push

> adb push < local_path > < remote_path >

举个栗子，我想把桌面的 log.txt 复制到手机的 dev 目录下：
> adb push .../log.txt /dev

#### 无线调试

平时我们都是使用 USB调试，但是有时候设备老化或者数据线连接不稳定， USB 调试就不好使了。这时我们就想能不能抛开这根数据线呢？当然可以，adb 也是支持通过 WIFI 进行调试了，使用方式如下：

* 首先，你要将 Android 设备和 装有 adb 的电脑连接到同一 Wi-Fi 网络。其次，你需要配置好防火墙，否则很有可能导致 Wi-Fi 调试不能使用。
* 使用 USB数据线 将手机连接到电脑。
* 设置目标设备监听 5555端口 的 TCP/IP 连接。

	>  adb tcpip 5555
* 断开手机与电脑的 USB 连接。
* 查看手机的 IP地址 。
* 通过 IP 连接手机

	> adb connect < device_ip_address >

这时就可以使用 adb devices 确认手机是否连接到电脑上了。

通过以上步骤，就可以开心的享用 WiFi 调试了。如果没有正常连接，可以按照下面的步骤检查：

1. 检查电脑和手机是否还在同一个 WiFi 网络下
2. 重新执行一次 adb connect <device_ip_address> 命令
3. 重启 adb 服务，然后重头再来
4. 检查是否是防火墙的设置问题

#### 查看设备的 log

在日常开发中，我们经常要查看日志进行调试我们的app，adb 提供了强大的日志查看命令。 adb logcat 命令格式是这样的:

> adb logcat [选项] [过滤项]
> 
> (-s : 设置输出日志的标签, 只显示该标签的日志)
> 
> (-f : 将日志输出到文件, 默认输出到标准输出流中, -f 参数执行不成功)
> 
> (-r : 按照每千字节输出日志, 需要 -f 参数, 不过这个命令没有执行成功)
> 
> (-n : 设置日志输出的最大数目, 需要 -r 参数, 这个执行 感觉 跟 adb logcat 效果一样)
> 
> (-v : 设置日志的输出格式, 注意只能设置一项)
> 
> (-c : 清空所有的日志缓存信息)
> 
> (-d : 将缓存的日志输出到屏幕上, 并且不会阻塞)
> 
> (-t : 输出最近的几行日志, 输出完退出, 不阻塞)
> 
> (-g : 查看日志缓冲区信息)
> 
> (-b : 加载一个日志缓冲区, 默认是 main, 下面详解)
> 
> (-B : 以二进制形式输出日志)

举几个常用的栗子，场景如下：

* 应用调试过程中，我想看全部日志,并想看日志输出时间。
> adb logcat -v time

* 日志输出后发现在终端控制台里查看不太方面，想输出到文件中查看。
> adb logcat -v time >xxx.txt

* 应用崩溃了，我现在只关心崩溃日志。
> adb logcat -v time *:e

* 崩溃解决了，心安了，现在想看某一个逻辑完整的执行过程（日志TAG：######）。
> adb logcat -s ######
 
这里就介绍这些基本且常用的命令的使用，如果你想深入了解一下，可以到  ![Android调试桥](https://developer.android.com/studio/command-line/adb.html?hl=zh-cn) 具体学习。

#### 重启手机

有时候，手动关机太麻烦，那就来个命令行吧~

> adb reboot

#### 以 root 权限开启 adb 守护进程

很多时候我们需要 root 手机以获得高权限操作手机，此时就需要下面的命令了：

> // 此命令会重启守护进程
> 
> adb root
>
> // root 成功后需要重新挂在磁盘
> adb remount

#### 开启或关闭 adb 服务

在某些情况下需要重启 adb 服务来解决问题，比如 adb 无响应。这时你可以通过 adb kill-server 来实现这一操作。

> adb kill-server
 
 kill 之后，通过 adb start-server 或者任意 adb 命令来重启 adb 服务。

> adb start-server

### 总结

以上就是 adb 命令的常见用法，都是本人日常开发中使用频率高的。有些不常用的 adb 命令没有介绍，更多 adb 用法请见：Adb Command Summary。文中如有纰漏，欢迎大家留言指出。