# Lambda表达式在Android中的使用
---

Lambda ,希腊字母 “λ” 的英文名称。没错，就是你高中数学老师口中的那个“兰布达”。在编程世界中，它是匿名函数的别名， Java 从 Java 8 开始引入 lambda 表达式。而 Android 开发者的世界里，直到 Android Studio 2.4 Preview 4 及其之后的版本里，lambda 表达式才得到完全的支持（在此之前需要使用 Jack 编译器或 retrolambda 等插件，详见：

> [使用 Java 8 语言功能](https://developer.android.google.cn/guide/platform/j8-jack.html?hl=zh-cn#configuration)

新版本 Android Studio 使用向导详见：

> [Use Java 8 language features](https://developer.android.google.cn/studio/preview/features/java8-support.html)

Oracle 官方推出的 lambda 教程开篇第一句就表扬了其对匿名内部类笨拙繁琐的代码的简化，然而，在各大 RxJava 教程下的评论中，最受吐槽的就是作者提供的示例代码用了 lambda 表达式，给阅读造成了很大的障碍。

所以，在这篇文章中，我会先讲解 lambda 表达式的作用和三种形式，之后提供一个在 Android Studio 便捷使用 lambda 的小技巧，然后说一说 lambda 表达式中比较重要的变量捕获概念，最后再讲一些使用 lambda 表达式前后的差异。

## 作用

前面提到，lambda 是匿名函数的别名。简单来说，lambda 表达式是对匿名内部类的进一步简化。使用 lambda 表达式的前提是编译器可以准确的判断出你需要哪一个匿名内部类的哪一个方法。

我们最经常接触使用匿名内部类的行为是为 view 设置 OnClickListener ，这时你的代码是这样的：

	button.setOnClickListener(new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			doSomeWork();
		}
	});

使用匿名内部类，实现了对象名的隐匿；而匿名函数，则是对方法名的隐匿。所以当使用 lambda 表达式实现上述代码时，是这样的：

	button.setOnClickListener(
		(View v) -> {
			doSomeWork();
		}
	);

看不懂？没关系,在这两个示例中，你只要理解，lambda 表达式不仅对对象名进行隐匿，更完成了方法名的隐匿，展示了一个接口抽象方法最有价值的两点：参数列表和具体实现。下面我会对 lambda 的各种形式进行列举。

## 形式

在 Java 中，lambda 表达式共有三种形式：函数式接口、方法引用和构造器引用。其中，函数式接口形式是最基本的 lambda 形式，其余两种形式都是基于此形式进行拓展。

PS：为了更好的展示使用 lambda 表达式前后的代码区别，本文将使用 lambda 表达式给引用赋值的形式作为实例展示，而不是常用的直接将 lambda 表达式传入方法之中。同时，举例也不一定具有实际意义。

### 函数式接口

函数式接口是指有且只有一个抽象方法的接口，比如各种 Listener 接口和 Runnable 接口。lambda 表达式就是对这类接口的匿名内部类进行简化。基本形式如下：

> ( 参数列表... ) -> { 语句块... }

下面以 Java 提供的 Comparator 接口来展示一个实例，该接口常用于排序比较：

	interface Comparator<T> {int compare(T var1, T var2);}
	Comparator<String> comparator = new Comparator<String>() {
		@Override
		public int compare(String s1, String s2) {
			doSomeWork();
			return result;
		}
	};

当编译器可以推导出具体的参数类型时，我们可以从参数列表中忽略参数类型，那么上面的代码就变成了：

	Comparator<String> comparator = (s1, s2) -> {
		doSomeWork();
		return result;
	};

当参数只有一个时，参数列表两侧的圆括号也可省略，比如 OnClickListener 接口可写成 ：

	interface OnClickListener {
		void onClick(View v);
	}
	
	OnClickLisenter listener = v -> {语句块...};

然而，当方法没有传入参数的时候，则记得提供一对空括号假装自己是参数列表（雾），比如 Runnable 接口：

	interface Runnable {
		void void run();
	}
	
	Runnable runnable = v -> {语句块...};

当语句块内的处理逻辑只有一句表达式时，其两侧的花括号也可省略，特别注意这句处理逻辑表达式后面也不带分号。比如这个关闭 activity 的点击方法：

	button.setOnClickListener( v -> activity.finish() );

同时，当只有一句去除花括号的表达式且接口方法需要返回值时，这个表达式不用（也不能）在表达式前加 return ，就可以当作返回语句。下面用 Java 的 Function 接口作为示例，这是一个用于转换类型的接口，在这里我们获取一个 User 对象的姓名字符串并返回：

	interface Function<T, R> { R apply(T t); }
	Function<User, String> function = new Function<User, String>() {
		@Override
		public String apply(User user) {
			return user.getName();
		}
	};
	Function<User, String> function = user -> user.getName();

### 方法引用

在介绍第一种形式的之前，我曾写道：函数式接口形式是最基本的 lambda 表达式形式，其余形式都是由其拓展而来。那么，现在来介绍第二种形式：方法引用形式。

当我们使用第一种 lambda 表达式的时候，进行逻辑实现的时候我们既可以自己实现一系列处理，也可以直接调用已经存在的方法，下面以 Java 的 Predicate 接口作为示例，此接口用来实现判断功能，我们来对字符串进行全面的判空操作：

	interface Predicate<T> { boolean test(T t); }
	Predicate<String> predicate = 
		s -> {
			// 用基本代码组合进行判断
			return s== null || s.length() == 0;
		};

我们知道，TextUtils 的 isEmpty() 方法实现了上述功能，所以我们可以写作：

	Predicate<String> predicate = s -> TextUtils.isEmpty(s);

这时我们调用了已存在的方法来进行逻辑判断，我们就可以使用方法引用的形式继续简化这一段 lambda 表达式：

	Predicate<String> predicate = TextUtils::isEmpty(s);

惊不惊喜？意不意外？

方法引用形式就是当逻辑实现只有一句且调用了已存在的方法进行处理( this 和 super 的方法也可包括在内)时，对函数式接口形式的 lambda 表达式进行进一步的简化。传入引用方法的参数就是原接口方法的参数。

接下来总结一下方法引用形式的三种格式：

* object :: instanceMethod

	直接调用任意对象的实例方法，如 obj::equals 代表调用 obj 的 equals 方法与接口方法参数比较是否相等，效果等同 obj.equals(t);当前类的方法可用this::method进行调用，父类方法同理。

* ClassName :: staticMethod

	直接调用某类的静态方法，并将接口方法参数传入，如上述 TextUtils::isEmpty，效果等同 TextUtils.isEmpty(s);

* ClassName :: instanceMethod

	较为特殊,将接口方法参数列表的第一个参数作为方法调用者，其余参数作为方法参数。由于此类接口较少，故选择 Java 提供的 BiFunction 接口作为示例，该接口方法接收一个 T1 类对象和一个 T2 类对象，通过处理后返回 R 类对象：

		interface BiFunction<T1, T2, R> {
			R apply(T1 t1, T2 t2);
		}
		BiFunction<String, String, Boolean> biFunction =new BiFunction<String, String, Boolean>() {
			@Override
			public Boolean apply(String s1, String s2) {
				return s1.equals(s2);
			}
		};
		// ClassName 为接口方法的第一个参数的类名，同时利用接口方法的第一个参数作为方法调用者，
		// 其余参数作为方法参数，实现 s1.equals(s2);
		BiFunction<String, String, Boolean> biFunction = String::equals;

### 构造器引用

Lambda 表达式的第三种形式，其实和方法引用十分相似，只不过方法名替换为 new 。其格式为 ClassName :: new。这时编译器会通过上下文判断传入的参数的类型、顺序、数量等，来调用适合的构造器，返回对象。

## 使用技巧

Android Studio 会在可以转化为 lambda 表达式的代码上进行如图的灰色标识，这时将光标移至灰色区域，按下 Alt + Enter ，选择第一项（方法引用和构造器引用在第二项），IDE 就会自动进行转换。

![](https://github.com/dreamfish797/StudyNotes/tree/master/Android/picture/20170707215115.png)

### 变量捕获

在使用匿名内部类时，若要在内部类中使用外部变量，则需要将此变量定义为 final 变量。因为我们并不知道所实现的接口方法何时会被调用，所以通过设立 final 来确保安全。在 lambda 表达式中，仍然需要遵守这个标准。

不过在 Java 8 中，新增了一个 effective final 功能，只要一个变量没有被修改过引用（基本变量则不能更改变量值），即为实质上的 final 变量，那么不用再在声明变量时加上 final 修饰符。接下来还是通过一个示例解释，示例中共有三句被注释掉的赋值语句，去除任意一句的注释，都会报错：Variable used in lambda expression should be final or effectively final。

	int effectiveFinalInt = 666; // 外部变量
	// 1.effectiveFinalInt = 233;
	button.setOnClickListener( v -> {
		Toast.makeText( effectiveFinalInt + "").show();
		// 2.effectiveFinalInt = 233;
	});
	// 3.effectiveFinalInt = 233;

可以看到，我们可以不做任何声明上的改变即可在 lambda 中使用外部变量，前提是我们以 final 的规则对待这个变量。

### 一点玄学

1. **this 关键字**

	在匿名内部类中，this 关键字指向的是匿名类本身的对象，而在 lambda 中，this 指向的是 lambda 表达式的外部类。

2. **方法数量差异**

	当前 Android Studio 对 Java 8 新特性编译时采用脱糖（desugar）处理，lambda 表达式经过编译器编译后，每一个 lambda 表达式都会增加 1~2 个方法数。而 Android 应用的方法数不能超过 65536 个。虽然一般应用较难触发，但仍需注意。