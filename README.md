# EndMinecraftPlusV2
___
### 许久未见，star竟已上百，感谢关注！
### 我看到了一些建议，但由于事务繁忙，无暇增添删改。
### 且该项目过于久远，大量代码逻辑混乱，晦涩难懂，会考虑近期进行完全重构。
___
### EndMinecraftPlusV2 - EMP重制版！
### 支持Minecraft 1.7.2-1.19.1
### (注意：较新版本的Minecraft支持可能仍存在一些问题，遇到错误可以提交Issues~)
___
### 温馨提示：
### 1. 此工具仅允许用作测试，请勿滥用，如因使用此工具而造成任何后果，本人概不负责！
### 2. 请尊重作者及他人，严禁出售、倒卖或进行任何牟利行为。
___
### Forked by SerendipityR~
### 源于<a href="https://github.com/ReActRailGun/EndMinecraftPlus">EndMinecraftPlus</a>优化修改的Minecraft压力测试工具
___
## 更新了什么？
### 自定义假人名称、自定义刷屏内容/指令、高级假人绕过(点击验证/重进验证)
### 可配置的Forge支持、猫反作弊绕过、高度自定义的配置项、发包崩服
### 优化内部逻辑、支持代理API更改、更加好看的输出界面~
### [量的提升促进质的飞跃，性能开销相较EMP降低75%+]
___
## 常见问题：
### 1. java.lang.ClassCastException: class jdk.internal.loader.ClassLoaders$AppClassLoader cannot be cast to class java.net.URLClassLoader
工具仅支持Java 8，请使用正确的Java版本后再试。
### 2. java.lang.OutOfMemoryError: GC overhead limit exceeded
内存溢出，尝试修改「Run.bat」文件中-Xmx参数以使用更大的内存运行。
### 3. 工具运行期间CPU占用率飙升，以致卡顿甚至假死
内存严重不足，此时JVM频繁进行GC操作以释放内存，占用了大量运算资源。
尝试修改「Run.bat」文件中-Xmx参数以使用更大的内存运行。
### 4. 部分高版本模组服务器出现java.lang.IllegalArgumentException: Invalid packet id: XX
服务端使用了LightFall或其他模组代理端，工具目前仅支持Forge官服。
### 5. IO异常: Too many open files
Linux系统对线程有一定限制，尝试修改配置并重启系统。

[/etc/systemd/system.conf] 及 [/etc/systemd/user.conf]

```
DefaultLimitNOFILE=100000
DefaultLimitNPROC=100000
```
___
## 有其他Bug怎么办？
### 发Issues，心情好的时候可能会修~
