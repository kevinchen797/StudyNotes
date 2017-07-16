#Builder模式
---
最近工作之余一直在断断续续的研究**媒体选择库**，在 GitHub 上搜了好多库对比看了看，在学习研究过程中发现其中都运用了 Builder模式，今天就一起学习一下 Builder模式，顺便看看它在 Android 源码中的应用。

我们在实际开发中，必然会遇到一些需求需要构建一个十分复杂的对象，譬如本人最近开发的项目中就需要构建一个媒体库选择器，类似微信和众多app中都有的图片、视屏资源选择器。这个选择器(Selector)是相对比较复杂的，它需要很多属性，比如：
> * 媒体资源类型： 图片/视屏
> * 选择模式： 单选/多选
> * 多选上限
> * 是否支持预览
> * 是否支持裁剪
> * 选择器UI风格样式
> * ......

通常我们可以通过构造函数的参数形式去写一个实现类

	Selector(int type)

	Selector(int type, int model)

	Selector(int type, int model, int maxSize)

	Selector(int type, int model, int maxsize, boolean isPreview)

	......

再或者可以使用 getter 和 setter 方法去设置各个属性

	public class Selector {
	    private int type; // 媒体资源类型： 图片/视屏
	    private int model; // 选择模式： 单选/多选
	    private int maxSize; // 多选上限
	    private boolean isPreview; // 是否支持预览
		
		private ...... // 更多属性就不一一举例了，大家脑部一下	
	
	    public int getType() {
	        return type;
	    }
	
	    public void setType(int type) {
	        this.type = type;
	    }
	
	    public int getModel() {
	        return model;
	    }
	
	    public void setModel(int model) {
	        this.model = model;
	    }
	
	    public int getMaxSize() {
	        return maxSize;
	    }
	
	    public void setMaxSize(int maxSize) {
	        this.maxSize = maxSize;
	    }
	
	    public boolean isPreview() {
	        return isPreview;
	    }
	
	    public void setPreview(boolean isPreview) {
	        this.isPreview = isPreview;
	    }

		.......
	}

先分析一下这两种构建对象的方式：

第一种方式通过重载构造方法实现，在参数不多的情况下，是比较方便快捷的，一旦参数多了，就会产生大量的构造方法，代码可读性大大降低，并且难以维护，对调用者来说也是一种灾难；

第二种方法可读性不错，也易于维护，但是这样子做对象会产生不确定性，当你构造 Selector 时想要传入全部参数的时候，那你就必需将所有的 setter 方法调用完成之后才算创建完成。然而一部分的调用者看到了这个对象后，以为这个对象已经创建完毕，就直接使用了，其实 Selector 对象并没有创建完成，另外，这个 Selector 对象也是可变的，不可变类的所有好处也将随之消散。

写到这里其实大家已经想到了，肯定有更好的办法去解决这个问题，是的，你猜对了， 今天我们的主角 Builder模式 就是为解决这类问题而生的。下面我们一起看看 Builder模式 是如何优雅的处理这类尴尬的。

### 模式的定义

将一个复杂对象的构建与它的表示分离，使得同样的构建过程可以创建不同的表示。 

### 模式的使用场景

1. 相同的方法，不同的执行顺序，产生不同的事件结果时；
2. 多个部件或零件，都可以装配到一个对象中，但是产生的运行结果又不相同时；
3. 产品类非常复杂，或者产品类中的调用顺序不同产生了不同的效能，这个时候使用建造者模式非常合适；

### 化解上述尴尬的过程

Builder模式 属于创建型，一步一步将一个复杂对象创建出来，允许用户在不知道内部构建细节的情况下，可以更精细地控制对象的构造流程。还是上面同样的需求，使用 Builder模式 实现如下：

	public class Selector {
	    private final int type; // 媒体资源类型： 图片/视屏
	    private final int model; // 选择模式： 单选/多选
	    private final int maxSize; // 多选上限
	    private final boolean isPreview; // 是否支持预览
		
		private final ...... // 更多属性就不一一举例了，大家脑部一下
		
		// 私有构造方法
		private Selector(SelectorBuilder selectorBuilder){
        this.type = selectorBuilder.type;
        this.model = selectorBuilder.model;
        this.maxSize = selectorBuilder.maxSize;
        this.isPreview = selectorBuilder.isPreview;
		......

		/** 由于所有属性都是 final 修饰的，所以只提供 getter 方法 **/

		public int getType() {
	        return type;
	    }
	
	    public int getModel() {
	        return model;
	    }
	
	    public int getMaxSize() {
	        return maxSize;
	    }
	
	    public boolean isPreview() {
	        return isPreview;
	    }

		.......
		
		// Builder 方法
		public static class SelectorBuilder{
	        private int type; // 媒体资源类型： 图片/视屏
		    private int model; // 选择模式： 单选/多选
		    private int maxSize; // 多选上限
		    private boolean isPreview; // 是否支持预览
			
			private ...... // 更多属性就不一一举例了，大家脑部一下
	        public SelectorBuilder() {
				// 设置各个属性的默认值
				this.type = 1;
				this.model = 1;
				this.maxSize = 9;
				this.isPreview = true;
				......
			}
	
	        public SelectorBuilder setType(int type){
	            this.type = type;
	            return this;
	        }
	
	        public SelectorBuilder setModel(int model) {
	        	this.model = model;
	            return this;
	        }
	
	        public SelectorBuilder setMaxSize(int maxSize) {
	        	this.maxSize = maxSize;
	            return this;
	        }

			...... // 全部的setter方法就省略了
	
	        public Selector build() {
	            return new Selector(this);
	        }
    	}
    }

值得注意的是 Selector 的构造方法是私有的，并且所有属性都是 final 修饰的，是不可变属性，对调用者也只提供 getter 方法，SelectorBuilder 内部类可以根据调用者的具体需求随意接收任意多个参数，应为我们再 SelectorBuilder 的构造方法中为每一个参数都设置了默认值，即使调用者调用时漏传某个参数，也不会影响整个创建过程。当我们将我们需用的所有参数传入后，随后调用 build() 构造 Selector 对象，代码如下：

	new Selector.SelectorBuilder()
             	.setType(2)
                .setModel(2)
                .setMaxSize(3)
				. ......
                .build();

是不是很简洁？意不意外？惊不惊喜？你没看错，就是这么厉害！


### Android源码中的模式实现

在Android源码中，我们最常用到的Builder模式就是AlertDialog.Builder， 使用该Builder来构建复杂的AlertDialog对象。简单示例如下 :

	// 显示基本的AlertDialog  
    private void showDialog(Context context) {  
        AlertDialog.Builder builder = new AlertDialog.Builder(context);  
        builder.setIcon(R.drawable.icon);  
        builder.setTitle("Title");  
        builder.setMessage("Message");  
        builder.setPositiveButton("Button1",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                        setTitle("点击了对话框上的Button1");  
                    }  
                });  
        builder.setNeutralButton("Button2",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                        setTitle("点击了对话框上的Button2");  
                    }  
                });  
        builder.setNegativeButton("Button3",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                        setTitle("点击了对话框上的Button3");  
                    }  
                });  
        builder.create().show();  // 构建AlertDialog， 并且显示
    } 


## 优缺点

当然在代码世界，永远没有绝对的完美，我们只是在走向完美的道路上尽力去填补一个个坑而已。 Builder模式 有它的好处，给我们带来了方便，但同时也会牺牲一些美好，这是不可避免的。

### 优点

* 良好的封装性， 使用建造者模式可以使客户端不必知道产品内部组成的细节；
* 建造者独立，容易扩展；
* 在对象创建过程中会使用到系统中的一些其它对象，这些对象在产品对象的创建过程中不易得到。

### 缺点

* 会产生多余的Builder对象，消耗内存；
* 对象的构建过程暴露。