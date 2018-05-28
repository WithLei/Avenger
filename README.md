# 智障先锋
[![GitHub stars](https://img.shields.io/github/stars/WithLei/Avenger.svg)](https://github.com/WithLei/Avenger/stargazers) [![GitHub forks](https://img.shields.io/github/forks/WithLei/Avenger.svg)](https://github.com/WithLei/Avenger/network) [![GitHub license](https://img.shields.io/github/license/WithLei/Avenger.svg)](https://github.com/WithLei/Avenger/blob/master/LICENSE)

>This is a collection game that moves your fingers so your hero can get more gold coins!
But be careful not to die...

## Project Intro
个人开源2D Android游戏项目，使用强大的AndAngine引擎,基于java语言开发，使用 OpenGL ES进行图形绘制。数据的存储读取使用轻型的数据库SQLite，能够有效处理并发以及保证数据的安全，同时使用了Google开发的框架volley，实现了图片加载的异步请求。

开源地址：https://github.com/WithLei/Avenger

下载链接：https://github.com/WithLei/Avenger/releases/download/2.0.0.0/app.apk
【目前最新2.0版本】

## Entity Intro
死神：搞笑艺人，主角遇到会被笑死，生命值减一。同时死神实在是过于神秘，以至于他的移动是完全随机的，谁也不知道他下一步的行动，在不同难度下死神的移动速度有所不同。

操控角色：玩家可操控角色，初始角色为DJ，皮肤界面可选角色有DVA，逃兵：77，猎空，天使。初始有三条生命，遭遇死神会被笑到吐血HP-1，碰到粉红笑脸可以恢复一条生命值，血量满时不可叠加。

粉色笑脸：神秘的笑脸，据说碰到会恢复玩家操控角色的生命值。它总会在玩家最需要的时候出现【当玩家HP出现减扣，粉红笑脸将会在一段时间后出现】。粉红笑脸的刷新位置随机，移动类似于弹球。



## DEMO
![menu](https://github.com/WithLei/Avenger/blob/master/screen/menu.png)
![skin](https://github.com/WithLei/Avenger/blob/master/screen/skin.gif)
![game](https://github.com/WithLei/Avenger/blob/master/screen/game.gif)
![leaderboard](https://github.com/WithLei/Avenger/blob/master/screen/leaderboard.png)
![leaderboard](https://github.com/WithLei/Avenger/blob/master/screen/leaderboard.gif)

## Installation

1. install the java JDK7.
2. clone this repo.
3. run make.bat if on windows, make.sh if on linux, or compile it with netbeans.
4. add the .jar file created to your project.
