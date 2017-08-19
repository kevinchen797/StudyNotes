# Service详细解析
---

Service 是 Android 中实现程序后台运行的解决方案，它非常适用于去执行那些不需要和用户交互而且还要求长期运行的任务。Service 默认并不会运行在子线程中，它也不运行在一个独立的进程中，它同样执行在 **UI 线程**中，因此，不要在 Service 中执行耗时的操作，除非你在 Service 中创建了子线程来完成耗时操作

Service 的运行不依赖于任何用户界面，即使程序被切换到后台或者用户打开另一个应用程序，Service 仍然能够保持正常运行，这也正是 Service 的使用场景。当某个应用程序进程被杀掉时，所有依赖于该进程的 Service 也会停止运行。

## Service的生命周期

![Service lifecycle](http://upload-images.jianshu.io/upload_images/4037756-092f726077efe728.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

通过这个图可以看到，两种启动 Service 的方式以及他们的生命周期， bindService 的不同之处在于当绑定的组件销毁后，对应的 Service 也就被 Kill 了。

* **被启动的服务的生命周期**

  一个 Service 被使用 startService 方法启动，不管是否调用了 bindService（绑定服务）或 unbindService （解除绑定服务）到该 Service ，该 Service 都会在后台运行并不受影响。

  一个 Service 被使用 startService 方法启动多少次，onCreate 方法只会调用一次，onStartCommand 方法将会被调用多次（与startService的次数一致），且系统只会创建一个 Service 实例（结束该 Service 也只需要调用一次 stopService ），该 Service 会一直在后台运行，直至调用 stopService 或调用自身的 stopSelf 方法。

  > **注：在系统资源不足的情况下，服务有可能被系统结束（Kill）；**

* **被绑定的服务的生命周期**

  如果一个 Service 在某个 Activity 中被调用 bindService 方法启动，不论 bindService 被调用几次， Service 的 onCreate 方法只会执行一次，同时 onStartCommand 方法始终不会调用。
  　　
  当建立连接后，Service会一直运行，除非调用 unbindService 来接触绑定、断开连接或调用该 Service 的 Context 不存在了（如 Activity 被 Finish ——即通过 bindService 启动的 Service 的生命周期依附于启动它的 Context ），系统在这时会自动停止该

* **被启动又被绑定的服务的生命周期**

  当一个 Service 在被启动 (startService) 的同时又被绑定 (bindService) ，该 Service 将会一直在后台运行，并且不管调用几次， onCreate 方法始终只会调用一次， onStartCommand 的调用次数与 startService 调用的次数一致（使用 bindService 方法不会调用 onStartCommand ）。同时，调用 unBindService 将不会停止 Service ，必须调用 stopService 或 Service 自身的 stopSelf 来停止服务。

* **当服务被停止时**

  当一个服务被终止（stopService、stopSelf、unbindService）时，onDestory 方法将会被调用——所以我们需要在该方法中清除一些工作（依附该 Service 生命周期的，如：停止在 Service 中创建并运行的线程）。

> 特别注意：
> 1. 在使用 startService 方法启动服务后，一定要调用 stopService 方法来停止该服务（同上，可以在 Activity  的**onDestory** 中来停止服务）；
> 2. 在某处调用 bindService 绑定 Service 的时候，要在对应的某处调用 unbindService 来解除绑定（如在 Activity 中绑定了 Service ，可以在 **onDestory** 中来解除绑定——虽然绑定的 Service 会在 Activity 结束时自动解除、停止）；
> 3. 如果同时使用 startService 与 bindService 方法启动 Service ，需要终止该 Service 时，要调用 stopService 和 unbindService 方法（ unbindService 依附于启动它的 Context ，startServicec 并不依附于启动它的 Context 。如果先调用 unbindService ，这时服务并不会被终止，当调用 stopService 后，服务才会被终止；如果先调用 stopService ，服务也不会被终止，当调用 unbindService 或者之前调用 bindService 的 Context 不存在了（如 Activity 被 finish 掉了）服务才会自动停止）；
> 4. 当手机屏幕发生旋转时，如果 Activity 设置的是自动旋转的话，在旋转的过程中， Activity 会重新创建，那么之前通过 bindService 建立的连接便会断开(之前绑定该服务的 Context 不存在了)，服务也会被自动停止。

## Service 使用

在新建一个 Service 后，记得在 AndroidManifest.xml 中注册 Service ，在 application 内添加需要注册的 Service 信息：

```
<service
	android:name=".service.PlayerService"
	android:label="PlayerService"
	android:exported="true" />
```

 AndroidManifest.xml中Service元素常见属性:

* **andorid:name**

  服务类名。可以是完整的包名 + 类名。也可使用.代替包名。

* **adroid:exported**

  其他应用能否访问该服务，如果不能，则只有本应用或有相同用户 ID 的应用能访问。默认为 false 。

* **android:enabled**

  标识服务是否可以被系统实例化。 true --系统默认启动， false --不启动。(默认值为 true )

* **android:label**

  显示给用户的服务名称。如果没有进行服务名称的设置，默认显示服务的类名。

* **android:process**

  服务所运行的进程名。默认是在当前进程下运行，与包名一致。如果进行了设置，将会在包名后加上设置的集成名。
  如果名称设置为冒号 :开头，一个对应用程序私有的新进程会在需要时和运行到这个进程时建立。如果名称为小写字母开头，服务会在一个相同名字的全局进程运行，如果有权限这样的话。这允许不同应用程序的组件可以分享一个进程，减少了资源的使用。

* **android:icon**

  服务的图标。

* **android:permission**

  申请使用该服务的权限，如果没有配置下相关权限，服务将不执行，使用 startService() 、 bindService() 方法将都得不到执行。

## Service 种类

服务是一个应用程序组件，可以在后台执行长时间运行的操作，不提供用户界面。一个应用程序组件可以启动一个服务，它将继续在后台运行，即使用户切换到另一个应用程序。此外，一个组件可以绑定到一个服务与它交互，甚至执行进程间通信 (IPC) 。例如，一个服务可能处理网络通信、播放音乐、计时操作或与一个内容提供者交互，都在后台执行。总共可分为**后台服务**、**前台服务**、**IntentService**、**跨进程服务**（远程服务）、**无障碍服务**、**系统服务 **。

1. **后台服务**

  后台服务可交互性主要是体现在不同的启动服务方式， startService() 和 bindService() 。 bindService() 可以返回一个代理对象，可调用 Service 中的方法和获取返回结果等操作，而 startService() 不行。

  * 不可交互的后台服务

    不可交互的后台服务即是普通的 Service ， Service 的生命周期很简单，分别为 onCreate、onStartCommand、onDestroy 这三个。当我们 startService() 的时候，首次创建 Service 会回调 onCreate() 方法，然后回调 onStartCommand() 方法，再次 startService() 的时候，就只会执行一次 onStartCommand() 。服务一旦开启后，我们就需要通过 stopService() 方法或者 stopSelf() 方法，就能把服务关闭，这时就会回调 onDestroy() 。

  * 可交互的后台服务

    可交互的后台服务是指前台页面可以调用后台服务的方法，可交互的后台服务实现步骤是和不可交互的后台服务实现步骤是一样的，区别在于启动的方式和获得 Service 的代理对象。区别在于多了一个 ServiceConnection 对象，该对象是用户绑定后台服务后，可获取后台服务代理对象的回调，我们可以通过该回调，拿到后台服务的代理对象，并调用后台服务定义的方法，也就实现了后台服务和前台的交互。

2. **前台服务**

  由于后台服务优先级相对比较低，当系统出现内存不足的情况下，它就有可能会被回收掉，所以前台服务就是来弥补这个缺点的，它可以一直保持运行状态而不被系统回收。例如：墨迹天气在状态栏中的天气预报。

  前台服务创建很简单，其实就在 Service 的基础上创建一个 Notification ，然后使用 Service 的 startForeground() 方法即可启动为前台服务
  > startService(new Intent(this, ForegroundService.class));

3. **IntentService**

  IntentService 是专门用来解决 Service 中不能执行耗时操作这一问题的，创建一个 IntentService 也很简单，只要继承 IntentService 并覆写 onHandlerIntent 函数，在该函数中就可以执行耗时操作了。

  ```
  public class MyIntentService extends IntentService {
      public MyIntentService(String name) {
        super(name);
      }
      
      @Override
      protected void onHandleIntent(Intent intent) {
        // 在这里执行耗时操作
      }      
  }
  ```
  在 IntentService 内有一个工作线程来处理耗时操作，当任务执行完后，IntentService 会自动停止，不需要我们去手动结束。如果启动 IntentService 多次，那么每一个耗时操作会以工作队列的方式在 IntentService 的 onHandleIntent 回调方法中执行，依次去执行，执行完自动结束。

4. **跨进程服务**（远程服务）

  远程服务为独立的进程，对应进程名格式为所在包名加上你指定的 android:process 字符串。由于是独立的进程，因此在 Activity 所在进程被 Kill 的时候，该服务依然在运行，不受其他进程影响，有利于为多个进程提供服务具有较高的灵活性。由于是独立的进程，会占用一定资源，并且使用 AIDL 进行 IPC 稍微麻烦一点，这种 Service 是**常驻**的。

  > 关于 AIDL 推荐大家看看[Android使用AIDL实现跨进程通讯（IPC）](http://blog.csdn.net/ydxlt/article/details/50812559)

5. **AccessibilityService无障碍服务**

  无障碍服务旨在帮助身心有障碍的用户使用 Android 设备和应用。无障碍服务在后台运行，当无障碍事件被激活时系统会执行 AccessibilityService 的 onAccessibilityEvent(AccessibilityEvent event) 方法。这些事件表示在用户界面中的一些状态的改变，例如：焦点的改变、按钮被点击等。这类服务可以有选择性地请求查询活动窗口的内容。无障碍服务的开发需要继承 AccessibilityService 和实现它的抽象方法。
  跟详细的介绍推荐阅读[ Android 无障碍辅助功能AccessibilityService（1）](http://blog.csdn.net/pjingying/article/details/52670162)

6. **系统服务**

  系统服务提供了很多便捷服务，可以查询 Wifi 、网络状态、查询电量、查询音量、查询包名、查询 Application 信息等等等相关多的服务，具体大家可以自信查询文档，这里举例几个常见的服务:

  * 判断Wifi是否开启

    ```
    WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
    boolean enabled = wm.isWifiEnabled();
    ```

  * 获取系统最大音量

    ```
    AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
    int max = am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
    ```

  * 获取当前音量

    ```
    AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
    int current = am.getStreamMaxVolume(AudioManager.STREAM_RING);
    ```

  * 判断网络是否有连接

    ```
    ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    NetworkInfo info = cm.getActiveNetworkInfo();
    boolean isAvailable = info.isAvailable();
    ```






