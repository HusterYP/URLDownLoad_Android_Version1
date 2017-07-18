1. 界面：
	接收URL和线程数量的输入框
	开始按钮（同时也是暂停按钮）
	进度条
	按钮，以显示文件内容（如果是可显示文件的话），同时启动另一个活动
	
2. class preDownLoad  //所有线程共有属性
	成员变量：
		URL网址
		文件总长度
		String filePath;  //文件保存路径
	方法：
		getURL()
		getTotalLength()
	
3. class ThreadRun extends ThreadRun  //每条线程不同的属性
	成员变量：
		每条线程开始位置
		结束位置
		当前下载量
		线程ID
		一个preDownLoad对象  //共同属性
	方法：
		run() //线程下载,通过Handler跟新界面，若暂停则要将进度保存，暂停与开始通过main中的一个boolean标志位判断
		
4. 显示内容的活动，初步定为两种，一个显示图片，另一个显示文本，其他默认不能预览

5. class main
	成员变量：
		boolean StartStopFlag;
		int ThreadCount;
		long curDownLength; //当前已下载量，用于更新进度条setProgress(int);
	
	方法：
		onCreate()      ：  	数据接收
		onDownLoad() 	：		开始暂停按钮的回调函数，开启线程
		onPreContent()  ： 		预览文件，判断
		
		
		
		
		
		
		
		
		
		
		
-------------------------------------
调bug调了一个通宵，尝试各种调法，最后发现竟然是自己手机没有联网，获取不到数据.....瞬间有句mmp
但是在我没有联网的时候，好像设置的链接超时没有起到作用？？

只能在主函数中用getCacheDir()？

handler如何在不同的类中访问？？只能像我的实现中的那样作为一个参数传进去吗


























