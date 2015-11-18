控制中心模块合入说明文档。

1、拷贝：
	拷贝assets目录下的www目录到锁屏相对应的目录；拷贝res/xml/config.xml文件到锁屏相对应目录；
	
2、类继承：
	原生锁屏中的wrap类需要继承LockWrapApi类，并保留所有的父类方法，因为内置方案中需要这些类方法；
	libgdx锁屏中的wrap类需要继承GdxLockWrapApi类，并保留所有的父类方法，因为内置方案中需要这些类方法。

3、AndroidManifest：
	拷贝AndroidManifest里注册的activity、service、receiver以及需要的权限到锁屏的AndroidManifest文件中。
	
4、混淆保护：
	拷贝proguard.cfg文件到锁屏对应目录。