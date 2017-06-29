# 浅谈Java 浅拷贝&深拷贝
---

上一篇文章 设计模式：原型模式 讲的是拷贝对象，文中提到了深拷贝和浅拷贝的概念，我自己在学习Java的时候也没注意，虽然Java中对象回收工作由GC帮我们做了，但在码代码时如果不注意也会埋下隐藏的BUG，今天我们深入探究一下深拷贝和浅拷贝。

我们在写代码时经常会需要将一个对象传递给另一个对象，Java语言中对于基本型变量采用的是值传递，而对于非基本类型对象传递时采用的引用传递也就是地址传递，而很多时候对于非基本类型对象传递我们也希望能够象值传递一样，使得传递之前和之后有不同的内存地址。在两种情况就是我们今天要讨论的 **浅拷贝** 和 **深拷贝** 。
	
	有Java基础的同学可能发现上面论述有些不严谨，String 类型在传递时其实也是值传递，因为 String 类型是不可变对象。

### 浅拷贝 

被复制对象的所有变量都含有与原来的对象相同的值，而所有的对其他对象的引用仍然指向原来的对象。换言之，浅复制仅仅复制所考虑的对象，而不复制它所引用的对象。

下面我们看一个例子：
	
	public class Book implements Cloneable {
		String bookName;
		double price;
		Person author;
	
		public Book(String bn, double price, Person author) {
			bookName = bn;
			this.price = price;
			this.author = author;
		}
	
		public Object clone() {
			Book b = null;
			try {
				b = (Book) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return b;
		}
	
		public static void main(String args[]) {
			Person p = new Person("Dream", 34);
			Book book1 = new Book("Java开发", 30.00, p);
			Book book2 = (Book) b1.clone();
			book2.price = 44.00;
			book2.author.setAge(45);
			book2.author.setName("Fish");
			book2.bookName = "Android开发";
			System.out.print("age = " + book1.author.getAge() + "  name = " 
			+ book1.bookName + "     price = " + book1.price);
			System.out.println();
			System.out.print("age = " + book2.author.getAge() + "  name = " 
			+ book2.bookName + "     price = " + book2.price);
		}
	}


> 结果：

> age = 45  name = Java开发     price = 30.0
> 
> age = 45  name = Android开发     price = 44.0

从结果中发现在改变 book2 对象的 name 和 price 属性时 book1 的属性并不会跟随改变，当改变 book2 对象的 author 属性时 book1 的 author 对象的属性也改变了，说明 author 是浅拷贝，和 book1 的 author 是使用同一引用。这时我们就需要使用深拷贝了。

### 深拷贝

被复制对象的所有变量都含有与原来的对象相同的值，除去那些引用其他对象的变量。那些引用其他对象的变量将指向被复制过的新对象，而不再是原有的那些被引用的对象。换言之，深复制把要复制的对象所引用的对象都复制了一遍。

为了解决上面 Person 对象未完全拷贝问题，我们需要用到深拷贝，其实很简单在拷贝book对象的时候加入如下语句：
 
	b.author =(Person)author.clone(); //将Person对象进行拷贝，Person对象需进行了拷贝

> 结果

> age = 34  name = Java开发     price = 30.0
> 
> age = 45  name = Android开发     price = 44.0



上面是用 clone() 方法实现深拷贝，传统重载clone()方法，但当类中有很多引用时，比较麻烦。 当然我们还有一种深拷贝方法，就是将对象 **序列化** 。

把对象写到流里的过程是序列化（Serilization）过程；而把对象从流中读出来的反序列化（Deserialization）过程。应当指出的是，写在流里的是对象的一个拷贝，而原对象仍然存在于JVM里面。

在Java语言里深复制一个对象，常常可以先使对象实现Serializable接口，然后把对象（实际上只是对象的一个拷贝）写到一个流里，再从流里读出来，便可以重建对象。

还是上面的例子，我们重写 clone() 方法：

	public Object deepClone() throws IOException, OptionalDataException, ClassNotFoundException {
        // 将对象写到流里
        OutputStream bo = new ByteArrayOutputStream();
        //OutputStream op = new ObjectOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(this);
 
        // 从流里读出来
        InputStream bi = new ByteArrayInputStream(((ByteArrayOutputStream) bo).toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        return (oi.readObject());
    }


然后在拷贝对象时调用重写的 deepClone() 方法

	Book book2 = (Book) b1.deepClone();

> 结果

> age = 34  name = Java开发     price = 30.0
> 
> age = 45  name = Android开发     price = 44.0


PS：这样做的前提是对象以及对象内部所有引用到的对象都是可串行化的，否则，就需要仔细考察那些不可串行化的对象可否设成transient（自行了解）