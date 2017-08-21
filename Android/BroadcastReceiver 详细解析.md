# BroadcastReceiver 详细解析
---

广播是一种广泛运用的在应用程序之间传输信息的机制，主要用来监听系统或者应用发出的广播信息，然后根据广播信息作为相应的逻辑处理，也可以用来传输**少量、频率低**的数据。

在实现开机启动服务和网络状态改变、电量变化、短信和来电时通过接收系统的广播让应用程序作出相应的处理。

BroadcastReceiver 自身并不实现图形用户界面，但是当它收到某个通知后， BroadcastReceiver 可以通过启动 Service 、启动 Activity 或是 NotificationMananger 提醒用户。

## 使用广播的注意事项

当系统或应用发出广播时，将会扫描系统中的所有广播接收者，通过 action 匹配将广播发送给相应的接收者，接收者收到广播后将会产生一个广播接收者的实例，执行其中的 onReceiver() 这个方法；特别需要注意的是这个实例的生命周期只有**10**秒，如果**10**秒内没执行结束 onReceiver() ，系统将会报错。在 onReceiver() 执行完毕之后，该实例将会被销毁，所以**不要在 onReceiver() 中执行耗时操作，也不要在里面创建子线程处理业务**（因为可能子线程没处理完，接收者就被回收了，那么子线程也会跟着被回收掉）；正确的处理方法就是通过 intent 调用 Activity 或者 Service 处理业务。

## BroadcastReceiver的注册

BroadcastReceiver 的注册方式有且只有两种，一种是**静态注册**（推荐使用），另外一种是**动态注册**，广播接收者在注册后就开始监听系统或者应用之间发送的广播消息。

下面通过例子看一下两种注册方式，先定义一个接收短信的 BroadcastReceiver 类：

```
public class MyBroadcastReceiver extends BroadcastReceiver {
	// action 名称
	String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED" ;
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals( SMS_RECEIVED )) {
			// 一个receiver可以接收多个action的，即可以有
			// 多个intent-filter，需要在 onReceive
			// 里面对 intent.getAction(action name) 进行判断。
			...
		}
	}
}
```

* **静态注册**

	在 AndroidManifest.xml 的 application 里面定义 receiver 并设置要接收的action。

	```
	< receiver android:name = ".MyBroadcastReceiver" > 
		< intent-filter android:priority = "777" >       
			<action android:name = "android.provider.Telephony.SMS_RECEIVED" />
		</ intent-filter > 
	</ receiver >
	```

	这里的 priority 取值是　-1000 到 1000 ，值越大优先级越高，同时注意加上系统接收短信的限权。

	静态注册的广播接收者是一个常驻在系统中的全局监听器，当你在应用中配置了一个静态的 BroadcastReceiver ，安装了应用后而无论应用是否处于运行状态，广播接收者都是已经常驻在系统中了。同时应用里的所有 receiver 都在清单文件里面，方便查看。要销毁掉静态注册的广播接收者，可以通过调用 PackageManager 将 Receiver 禁用。

* **动态注册**

	在 Activity 中声明 BroadcastReceiver 的扩展对象，在 onResume 中注册，onPause 中卸载. 

	```
	public class MainActivity extends Activity {
		MyBroadcastReceiver receiver;

		@Override
	 	protected void onResume() {
			// 动态注册广播 (代码执行到这才会开始监听广播消息，并对广播消息作为相应的处理)
			receiver = new MyBroadcastReceiver();
			IntentFilter intentFilter = new IntentFilter( "android.provider.Telephony.SMS_RECEIVED" );
			registerReceiver( receiver , intentFilter);	
			super.onResume();
		}

		@Override
		protected void onPause() { 
			// 撤销注册 (撤销注册后广播接收者将不会再监听系统的广播消息)
			unregisterReceiver(receiver);
			super.onPause();
		}
	}
	```

* **静态注册和动态注册的区别**

	1. 静态注册的广播接收者一经安装就常驻在系统之中，不需要重新启动唤醒接收者；动态注册的广播接收者随着应用的生命周期，由 registerReceiver 开始监听，由 unregisterReceiver 撤销监听，如果应用退出后，没有撤销已经注册的接收者应用应用将会报错。

	2. 当广播接收者通过 intent 启动一个 activity 或者 service 时，如果 intent 中无法匹配到相应的组件。动态注册的广播接收者将会导致应用报错,而静态注册的广播接收者将不会有任何报错，因为自从应用安装完成后，广播接收者跟应用已经脱离了关系。

## 发送广播主要类型

* **普通广播**

	普通广播是完全异步的，可以在同一时刻（逻辑上）被所有接收者接收到，所有满足条件的 BroadcastReceiver 都会随机地执行其 onReceive() 方法。
	
	同级别接收是先后是随机的；级别低的收到广播；消息传递的效率比较高，并且无法中断广播的传播。

	```
	Intent intent = new Intent("android.provider.Telephony.SMS_RECEIVED"); 
	//通过intent传递少量数据
	intent.putExtra("data", "finch"); 
	// 发送普通广播
	sendBroadcast(Intent);
	```

* **有序广播**

	有序广播通过 Context.sendOrderedBroadcast() 来发送，所有的广播接收器优先级依次执行，广播接收器的优先级通过 receiver 的 intent-filter 中的 android:priority 属性来设置，数值越大优先级越高。

	当广播接收器接收到广播后，可以使用 setResult() 函数来结果传给下一个广播接收器接收，然后通过 getResult() 函数来取得上个广播接收器接收返回的结果。

	当广播接收器接收到广播后，也可以用 abortBroadcast() 函数来让系统拦截下来该广播，并将该广播丢弃，使该广播不再传送到别的广播接收器接收。

* **本地广播**

	在 API21 的 Support v4 包中新增本地广播，也就是 LocalBroadcastManager 。由于之前的广播都是全局的，所有应用程序都可以接收到，这样就会带来安全隐患，所以我们使用 LocalBroadcastManager 只发送给自己应用内的信息广播，限制在进程内使用。

	它的用法很简单，只需要把调用 context 的 sendBroadcast、registerReceiver、unregisterReceiver 的地方换为 LocalBroadcastManager.getInstance(Context context)中对应的函数即可。

	这里创建广播的过程和普通广播是一样的过程，这里就不过多介绍了

* **系统广播**

	当然系统中也会有很多自带的广播，当符合一定条件时，系统会发送一些定义好的广播，比如：重启、充电、来电电话等等。我们可以通过action属性来监听我们的系统广播,创建广播的过程和普通广播是一样的过程，这里就不过多介绍了。

	常用的广播action属性有:

	* 屏幕被关闭之后的广播：Intent.ACTION_SCREEN_OFF

	* 屏幕被打开之后的广播：Intent.ACTION_SCREEN_ON

	* 充电状态，或者电池的电量发生变化：Intent.ACTION_BATTERY_CHANGED

	* 关闭或打开飞行模式时的广播：Intent.ACTION_AIRPLANE_MODE_CHANGED

	* 表示电池电量低：Intent.ACTION_BATTERY_LOW

	* 表示电池电量充足，即电池电量饱满时会发出广播：Intent.ACTION_BATTERY_OKAY

	* 按下照相时的拍照按键(硬件按键)时发出的广播：Intent.ACTION_CAMERA_BUTTON

	> 值得注意的是，随着系统版本的提升，很多系统广播已经不再被支持，如果你需要使用哪个系统广播，最好看看最新版本的支持情况。