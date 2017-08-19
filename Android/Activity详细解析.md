# Activity 详细解析
---

## Activity的生命周期

![Activity 生命周期图.png](http://upload-images.jianshu.io/upload_images/4037756-5574a4d135a8a7fb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

Android 中的 Activity 全都归属于 Task 管理 。Task 是多个 Activity 的集合，这些 activity 按照启动顺序排队存入一个栈（即“back stack”）。 Android 默认会为每个 app 维持一个 Task 来存放该 app 的所有 Activity，Task 的默认 name 为该 app 的 packagename 。

首先打开一个新的 Activity 实例的时候，系统会依次调用 **onCreate() -> onStart() -> onResume()** 然后开始 running, 在 running 的时候被覆盖了（从它打开了新的 Activity 或是被锁屏，但是它**依然在前台**运行， lost focus but is still visible），系统调用onPause()，该方法执行 Activity 暂停，通常用于提交未保存的更改到持久化数据，停止动画和其他的东西。但这个 Activity 还是完全活着（它保持所有的状态和成员信息，并保持连接到窗口管理器）

**接下来这个 Activity 有三种不一样的人生：**

1. 用户返回到该 Activity 就调用 onResume() 方法重新 running


2. 用户回到桌面或是打开其他 Activity，就会调用 onStop() 进入停止状态（保留所有的状态和成员信息，**对用户不可见**）


3. 系统内存不足，拥有更高限权的应用需要内存，那么该 Activity 的进程就可能会被系统回收（回收onRause()和onStop()状态的 Activity 进程）要想重新打开就必须重新创建一遍。

如果用户返回到 onStop() 状态的 Activity（又显示在前台了），系统会调用 onRestart() -> onStart() -> onResume() 然后重新 running 在 Activity 结束（调用finish () ）或是被系统杀死之前会调用 onDestroy() 方法释放所有占用的资源。

**Activity 生命周期中三个嵌套的循环:**

* Activity 的完整生存期会在 onCreate() 调用和 onDestroy() 调用之间发生。


* Activity 的可见生存期会在 onStart() 调用和 onStop() 调用之间发生。系统会在 Activity 的整个生存期内多次调用 onStart() 和 onStop()， 因为 Activity 可能会在显示和隐藏之间不断地来回切换。　


* Activity 的前后台切换会在 onResume() 调用和 onPause() 之间发生。 因为这个状态可能会经常发生转换，为了避免切换迟缓引起的用户等待，**这两个方法中的代码应该相当地轻量化**。

## Activity 的启动模式

### 启动模式什么？

简单的说就是定义 Activity 实例与 Task 的关联方式。

### 为什么要定义启动模式？

为了实现一些默认启动（standard）模式之外的需求：

* 让某个 Activity 启动一个新的 Task （而不是被放入当前 Task ）

* 让 Activity 启动时只是调出已有的某个实例（而不是在 back stack 顶创建一个新的实例）

* 或者，你想在用户离开 Task 时只保留根 Activity，而 back stack 中的其它 Activity 都要清空

### 怎样定义启动模式？

定义启动模式的方法有两种：

#### 使用 manifest 文件

在 manifest 文件中 Activity 声明时，利用 Activity 元素的 **launchMode** 属性来设定 Activity 与 Task 的关系。

> 注意： 你用 launchMode 属性为 Activity 设置的模式可以被启动 Activity 的 intent 标志所覆盖。

##### 有哪些启动模式？

* **"standard"（默认模式）**

  当通过这种模式来启动 Activity 时,　Android 总会为目标 Activity 创建一个新的实例,并将该 Activity 添加到当前Task栈中。这种方式不会启动新的 Task,只是将新的 Activity 添加到原有的 Task 中。

* **"singleTop"**

  该模式和standard模式基本一致,但有一点不同:当将要被启动的 Activity 已经位于 Task 栈顶时,系统不会重新创建目标 Activity 实例,而是直接复用 Task 栈顶的  Activity。

* **"singleTask"**

  Activity 在同一个 Task 内只有一个实例。如果将要启动的 Activity 不存在,那么系统将会创建该实例,并将其加入 Task 栈顶。

  如果将要启动的 Activity 已存在,且存在栈顶,直接复用 Task 栈顶的 Activity。

  如果 Activity 存在但是没有位于栈顶,那么此时系统会把位于该 Activity 上面的所有其他Activity全部移出 Task,从而使得该目标 Activity 位于栈顶。

* **"singleInstance"**

  无论从哪个 Task 中启动目标 Activity,只会创建一个目标 Activity 实例且会用一个全新的 Task 栈来装载该 Activity 实例（全局单例）

  如果将要启动的 Activity 不存在,那么系统将会先创建一个全新的 Task ,再创建目标 Activity 实例并将该 Activity 实例放入此全新的 Task 中。

  如果将要启动的Activity已存在,那么无论它位于哪个应用程序,哪个 Task 中;系统都会把该 Activity 所在的 Task 转到前台,从而使该 Activity 显示出来。

#### 使用 Intent 标志

在要启动 activity 时，你可以在传给 startActivity() 的 intent 中包含相应标志，以修改 activity 与 task 的默认关系。

like this
> Intent i = new Intent(this,ＮewActivity.class);
> **i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);**
> startActivity(i);

##### 可以通过标志修改的默认模式有哪些？

* FLAG_ACTIVITY_NEW_TASK

  与 "singleTask" 模式相同，在新的 task 中启动 Activity。如果要启动的 Activity 已经运行于某 Task 中，则那个 task 将调入前台。

* FLAG_ACTIVITY_SINGLE_TOP

  与 "singleTop" 模式相同，如果要启动的 Activity位于back stack 顶，系统不会重新创建目标 Activity 实例,而是直接复用 Task 栈顶的 Activity。

* FLAG_ACTIVITY_CLEAR_TOP

  此种模式在 launchMode 中没有对应的属性值。

  如果要启动的 Activity 已经在当前 Task 中运行，则不再启动一个新的实例，且所有在其上面的 Activity 将被销毁。

  > 一般不要改变 Activity 和 Task 默认的工作方式。 如果你确定有必要修改默认方式，请保持谨慎，并确保 Activity 在启动和从其它 Activity 返回时的可用性，多做测试和安全方面的工作。

## Activity被回收之后状态的保存和恢复

```
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(savedInstanceState!=null){ //判断是否有以前的保存状态信息
			savedInstanceState.get("Key"); 
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
   		//可能被回收内存前保存状态和信息，
    	Bundle data = new Bundle(); 
    	data.putString("key", "last words before be kill");
    	outState.putAll(data);
    	super.onSaveInstanceState(outState);
	}

  	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	  if(savedInstanceState!=null){ //判断是否有以前的保存状态信息
		savedInstanceState.get("Key"); 
	   }
	 super.onRestoreInstanceState(savedInstanceState);
	}
}
```

### onSaveInstanceState 方法

在 Activity　可能被回收之前调用,用来保存自己的状态和信息，以便回收后重建时恢复数据（在 onCreate() 或 onRestoreInstanceState() 中恢复）。旋转屏幕重建 Activity 会调用该方法，但其他情况在 onRause() 和 onStop() 状态的 Activity 不一定会调用，下面是该方法的文档说明。

> One example of when onPause and onStop is called and not this method is when a user navigates back from activity B to activity A: there is no need to call onSaveInstanceState on B because that particular instance will never be restored, so the system avoids calling it. An example when onPause is called and not onSaveInstanceState is when activity B is launched in front of activity A: the system may avoid calling onSaveInstanceState on activity A if it isn't killed during the lifetime of B since the state of the user interface of A will stay intact.

也就是说，系统灵活的来决定调不调用该方法，**但是如果要调用就一定发生在 onStop 方法之前，但并不保证发生在 onPause 的前面还是后面。**

### onRestoreInstanceState 方法

这个方法在 onStart 和 onPostCreate 之间调用，在 onCreate 中也可以状态恢复，但有时候需要所有布局初始化完成后再恢复状态。

onPostCreate：一般不实现这个方法，当程序的代码开始运行时，它调用系统做最后的初始化工作。

## Intent Filter

Android 的 3 个核心组件—— Activity、Services、广播接收器——是通过 intent 传递消息的。 intent 消息用于在运行时绑定不同的组件。 　　在 Android 的 AndroidManifest.xml 配置文件中可以通过 intent-filter 节点为一个 Activity 指定其 Intent Filter，以便告诉系统该 Activity 可以响应什么类型的 Intent。

### intent-filter 的三大属性

* **Action**

  一个 Intent Filter 可以包含多个 Action，Action 列表用于标示 Activity 所能接受的“动作”，它是一个用户自定义的字符串。

```
	<intent-filter > 
 		<action android:name="android.intent.action.MAIN" /> 	
 		<action android:name="com.scu.amazing7Action" /> 
		……
 	</intent-filter>

```

	在代码中使用以下语句便可以启动该Intent 对象：

	> Intent i=new Intent(); 
	> i.setAction("com.scu.amazing7Action");
	
	Action 列表中包含了“com.scu.amazing7Action”的 Activity 都将会匹配成功

* **URL**

  在 intent-filter 节点中，通过 data 节点匹配外部数据，也就是通过 URI 携带外部数据给目标组件。

```
		<data android:mimeType="mimeType" 
			  android:scheme="scheme" 
			  android:host="host"
			  android:port="port" 
		  android:path="path"/>
```

	> 注意：只有data的所有的属性都匹配成功时 URI 数据匹配才会成功

* **Category**

  指定 Action 范围,这个选项指定了将要执行的这个 Action 的其他一些额外的约束.有时通过 Action，配合 Data 或 Type ，很多时候可以准确的表达出一个完整的意图了，但也会需要加一些约束在里面才能够更精准。

```
	<intent-filter . . . >
		<action android:name="code android.intent.action.MAIN" />
		<category android:name="code　android.intent.category.LAUNCHER" />
	</intent-filter>
```

### Activity 中 Intent Filter 的匹配过程

1. 加载所有的 Intent Filter列表

2. 去掉 Action 匹配失败的 Intent Filter

3. 去掉 url 匹配失败的 Intent Filter

4. 去掉 Category 匹配失败的 Intent Filter

5. 判断剩下的 Intent Filter 数目是否为 0 。如果为 0 查找失败返回异常；如果大于 0 ，就按优先级排序，返回最高优先级的 Intent Filter

一般设置Activity为非公开的
```
<activity 
．．．．．． 
android:exported="false" />
```
> 注意：非公开的 Activity 不能设置 Intent-filter，以免被其他 Activity 唤醒（如果拥有相同的 Intent-filter ）。

* 不要指定 Activity 的 taskAffinity 属性

* 不要设置 Activity 的 LaunchMode（保持默认）

  > 注意 Activity 的 intent 最好也不要设定为 FLAG_ACTIVITY_NEW_TASK

* 在匿名内部类中使用 this 时加上 Activity 类名（类名.this,不一定是当前 Activity ）
