# eyes-world-android

## 该程序为网页打包程序  
想要测试网页打包后在电视上的效果，可以clone该工程，用AS在平板虚拟机上测试（效果跟在电视上差不多）

## 具体测试用法
-  首先确保网页工程更新到最新（之前config下某个设置不对，导致无法正常生成网页）。
- 在确认工程没有错误之后，在工程目录下运行命令: 
```
npm run build
```
- 命令完成后，可以在dist目录下找到一个index.html和一个static文件夹，其中static文件夹中是工程的所有图片，js和css等。
- 直接打开index.html，可以在电脑上先看到效果，此时可以最后确认一下效果。
- 确认无误后，将index.html和static文件夹复制到改工程下的app/src/main/assets下（原来的文件可以删掉）。
- 之后用AS编译，在平板虚拟机上运行，即可以看到效果。
- 遥控器的上下左右与键盘的上下左右相同，中间按键为键盘上的Enter，菜单和返回键虚拟机上应该会有。
  
## 安装到开发板
- 如果要安装到开发板上，要与开发板进行连接
- 首先确认你有adb工具，也就是一个adb.exe（很多软件都有集成，比如Android SDK中也有一个）
- 为了方便使用，可以修改环境变量，即将包含adb.exe的目录添加到path变量下。
- 或者进入到adb.exe的目录下再进行命令的输入
- 然后我们需要得知开发板的ip地址
	- 由于开发板与电脑的连接是通过串口连接，一般电脑没有驱动，adb无法识别。所以最好的办法就是通过网络连接。
	- 确保开发板和电脑都连接到交换机，即在同一个局域网中,并将开发板电源打开
	- 在cmd下输入：
	```
	arp -a
	```
	- 可以看到局域网中所有主机的IP地址和物理地址
	- 开发板的物理地址为 7E:A6:53:C9:01:8B ,找到这个物理地址对应的IP地址，例如172.20.114.55
	- 之后使用adb命令连接开发板
	```
	adb connect 172.20.114.55
	```
	- 连接成功后AS应该可以找到开发板，就可以直接在板子上运行。
- 若AS还是无法识别，但是可以连接，或者有apk但是没有AS，可以用adb命令将生成的apk安装到开发板上。
	- 首先先将开发板上的旧应用卸载掉
	- 查看生成apk的目录，注意是电脑上的目录，例如D:\eyes-world.apk
	- 使用以下命令：
```
adb install D:\eyes-world.apk
```
- 安装成功后即可在开发板上运行
- P.S. 安装过程需要将apk传输到板子上，项目图片较多，传输过程稍微有点长，请耐心等待...
    
## 目前发现的问题

  1. 网页工程中，命令 **npm run dev** 直接对编写的vue文件编译呈现效果，而命令 **npm run build** 会将整个工程编译为较少文件，其中所有css和js都会分别集中在同一个文件中。这造成了本来不同文件下的css互相影响，实际效果跟直接编译的效果不一样。
  2. css中引用的图片url无法正确转换。若在JS下通过url获取图片，则路径可以正确转换到static/img下，而css中引用的url则会被转换成static/css/static/img，路径不正确。
  3. webview对于css动画支持不佳，更换为XWalkView后效果较好，但还是掉帧严重。
  4. to be continued...
