latex input:		mmd-article-header
Title:		Java Notes
Author:		Ethan C. Petuchowski
Base Header Level:		1
latex mode:		memoir
Keywords:		Math, DSP, Digital Signal Processing, Fourier Transform
CSS:		http://fletcherpenney.net/css/document.css
xhtml header:		<script type="text/javascript" src="http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
</script>
copyright:			2014 Ethan Petuchowski
latex input:		mmd-natbib-plain
latex input:		mmd-article-begin-doc
latex footer:		mmd-memoir-footer

Useful external libraries and frameworks
========================================

JUnit
-----
**5/20/14**

[JUnit Homepage][],
[JUnit Wikipedia][]

* Simple framework for writing repeatable tests
* Uses [xUnit][] unit-testing architecture

From the [Github Wiki & Tutorial][]

* There are various [assertions][] you can make like
	* `testAssertEquals(msg, input, expected)`
	* `testAssertNotNull(msg, input)`
	* `assertThat(input, both(containsString("a")).and(containsString("b")))`
	* `assertThat(input, anyOf(equalTo("bad"), equalTo("good"))`
* There are provided abstractions for [test fixtures][]

[test fixtures]: https://github.com/junit-team/junit/wiki/Test-fixtures
[assertions]: https://github.com/junit-team/junit/wiki/Assertions
[JUnit Wikipedia]: http://en.wikipedia.org/wiki/JUnit
[xUnit]: http://en.wikipedia.org/wiki/XUnit
[JUnit Homepage]: http://junit.org/
[Github Wiki & Tutorial]: https://github.com/junit-team/junit/wiki


Gson
----
**5/20/14**

[Gson Wiki][],
[On Google Code][]

* Open source Google library for de/serializing Java objects to/from JSON using `toJson()` and `fromJson()`
* Can work with arbitrary Java objects including pre-existing objects that you do not have source-code of
* Support for Java Generics
* Support "arbitrarily complex objects"
* Fields present in the JSON but not the object to be deserialized to are *ignored* 
	* This is a "feature" because it makes things more flexible
* In general, this looks like a simple and useful tool
* You use `GsonBuilder()` instead of `Gson()` to allow more customization

[Gson Wiki]: http://en.wikipedia.org/wiki/Gson
[On Google Code]: https://code.google.com/p/google-gson/


Collections
===========

Maps
----
**5/12/14**

[SO Maps][]

### HashMap
* **no guarantees** about iteration order

### NavigableMap (interface) 

### TreeMap
* A `SortedMap`  which allows searches for "closest matches"
	* `lowerEntry`, `floorEntry`, `ceilingEntry`, `higherEntry`,
	  `=> Map.Entry<K,V>`
	* `lowerKey`, ..., `=> K`
* Can be traversed in ascending *or* descending order

* Iterates/sorted **according to `compareTo()`** (or supplied `Comparator`)
* Requires `O(log(n))` `containsKey`, `get`, `put`, and `remove`
* It's a Red-Black tree based `NavigableMap`

### LinkedHashMap

* **iterates in order of insertion**
* Contains both a hashmap *and* a **double-linked-list** in ordered by *insertion-order*
	* In other words, when you call `map.entrySet()` the *first* one out is
     the *first* one you *inserted*
* If you (re-)insert a `key` that the map already `contains`, the order is
  *not* affected

[SO Maps]: http://stackoverflow.com/questions/2889777/difference-between-hashmap-linkedhashmap-and-sortedmap-in-java


Java I/O
========

**5/12/14**
[docs.oracle-tutorial](http://docs.oracle.com/javase/tutorial/essential/io/index.html)

I/O Streams
-----------

[Oracle's Java Tuts](http://docs.oracle.com/javase/tutorial/essential/io/streams.html)

* Represents an input source *or* an output destination
* Could be disk files, devices, other programs, a network socket, or memory arrays
* Could be any *kind* of data (bytes, primitives, objects, etc.)
* Streams can manipulate and transform the data
* **Input** streams *read* from a source, one item at a time
* **Output** streams *write* to a destination, one item at a time


### Byte Streams

* Byte streams perform I/O of 8-bit bytes
* All byte stream classes descend from `InputStream` and `OutputStream`
* E.g. `FileInputStream` and `FileOutputStream`, which in particular `read()`s/`write()`s from files
	* Otw they act much the same as any other byte stream
* Only use byte streams when there is no higher-level abstraction of your use-case available
	* But note that those higher-level abstractions are *built* on byte streams


### Character Streams

* Uses Unicode conventions
* All character stream classes descend from `Reader` and `Writer`
* For file I/O, we have `FileReader` and `FileWriter`


### Buffered Streams

* The aforementioned I/O classes use *unbuffered I/O* -- where each read/write is
  handled directly by the underlying OS
    * This can be inefficient, so we use *buffered I/O* streams
* **Buffered I/O streams read data from a memory area known as a *buffer*; the native
  input API is called only when the buffer is empty**
    * Writing data is analogously done to a buffer, with the OS only writing-out
      when the buffer is full
* One can **convert an unbuffered into a buffered stream by passing the unbuffered stream
  object into the constructor for a buffered stream class**
  
		inputStream = new BufferedReader(new FileReader("xanadu.txt"));
		
	* This is that thing you've seen oh-so-many times
* *Flushing* the buffer is when you write out non-full buffer (use `flush()`)

### Scanners

	s = new Scanner(new BufferedReader(new FileReader("xanadu.txt")));

* A `Scanner` breaks input into *tokens*
* By default it uses whitespace to separate tokens
* To change it to `comma with optional following whitespace`, use:

		s.useDelimiter(",\\s*");
		
### Data streams

Support I/O of primitive data types

### Object streams

* Support I/O of objects
* Objects that support serialization implement `Serializable`

E.g.

	out.writeObject(obj);
	Object obj = in.readObject();
	
	
GSON
----

[On Google Code](https://code.google.com/p/google-gson/)

* A library that can convert Java Objects into their JSON representation, and back.
* Gson can work with arbitrary Java objects including pre-existing objects that you
  do not have source-code of.
* Provides simple `toJson()` and `fromJson()` methods to convert Java objects to JSON and vice-versa

Java concurrency
================

**5/12/14**
[Wikipedia](http://en.wikipedia.org/wiki/Java_concurrency)

* The programmer must ensure read and write access to objects is properly coordinated
  (or "synchronized") between threads.
* Thread synchronization ensures that objects are modified by only one thread at a time
* And that threads are prevented from accessing partially updated objects during
  modification by another thread.
* The Java language has built-in constructs to support this coordination.
* Multiple processes can only be realized with multiple JVMs, so we really just care
  about *multi*-***threaded*** programming
  
#### Thread objects

* **Threads share the process's resources, including memory** and open files.
* This makes for efficient, but potentially problematic, communication.
* Every application has at least one thread called the `main thread`.
* The `main thread` has the ability to create additional threads from the
  `Runnable` or `Callable` object
* Each thread can be scheduled on a different CPU core
* Every JVM implementation can map Java threads to native OS threads in a different way
* Each thread is associated with an instance of the `class Thread`

##### Two ways to start a thread

###### Provide a Runnable object

	public class HelloRunnable implements Runnable {
	   public void run() {
	       System.out.println("Hello from thread!");
	   }
	   public static void main(String[] args) {
	       (new Thread(new HelloRunnable())).start();
	   }
	}

###### Subclass Thread

	public class HelloThread extends Thread {
	   public void run() {
	       System.out.println("Hello from thread!");
	   }
	   public static void main(String[] args) {
	       (new HelloThread()).start();
	   }
	}

#### Thread basics

* Use `Thread.interrupt()` to tell a thread to stop what it is doing and do something else.
* Wait for the completion of another thread with `Thread.join()`
* To synchronize threads, Java uses **monitors**, a mechanism allowing only one thread
  at a time to execute a region of code protected by the monitor
  
##### Cache coherence:

* After we exit a synchronized block, we release the monitor
* This flushes the cache to main memory, making writes made by this thread visible to other threads.
* So now, before we can enter a synchronized block, we acquire the monitor, invalidating the
  local processor cache, so that variables will be reloaded from main memory, so are now able
  to see all of the writes made by the previous release.


### Java Memory Model

Ref:
[Wikipedia](http://en.wikipedia.org/wiki/Java_Memory_Model)

* On modern platforms, code is frequently reordered by the compiler, the processor and
  the memory subsystem to achieve maximum performance.
* On multiprocessor architectures, individual processors may have their own local caches that
  are out of sync with main memory.
* It is generally undesirable to require threads to remain perfectly in sync with one
  another because this would be too costly from a performance point of view.
* This means that at any given time, different threads may see different values for the
  same shared data.
* **The Java Memory Model (JMM) defines** the allowable behavior of multithreaded
  programs, and therefore describes **when such reorderings are possible**.
* It places execution-time constraints on the relationship between threads and main
  memory in order to achieve consistent and reliable Java applications.
* By doing this, it makes it possible to reason about code execution in a multithreaded
  environment, even in the face of optimizations performed by the dynamic compiler, the
  processor(s) and the caches.
* The *Java Language Specification* requires a *Java Virtual Machine* to observe
  `within-thread` `as-if-serial` semantics.
* The major caveat of this is that `as-if-serial` semantics do *not* prevent different threads
  from having different views of the data.
* Everything that happens before the release of a lock will be seen to be ordered before
  and visible to everything that happens after a subsequent acquisition of that same lock.


### Synchronized

Ref:

* [SO-1](http://stackoverflow.com/questions/442564/avoid-synchronizedthis-in-java)
* [SO-2](http://stackoverflow.com/questions/574240/synchronized-block-vs-synchronized-method)
* [SO-3](http://stackoverflow.com/questions/1085709/what-does-synchronized-mean)

Here is a quote from Sun:

> Synchronized methods enable a simple strategy for preventing thread interference and
> memory consistency errors: if an object is visible to more than one thread, all reads
> or writes to that object's variables are done through synchronized methods.


#### Block vs Method

Synchronized block

	public void blah() {
	  synchronized (this) {
	    // do stuff
	  }
	}

is semantically equivalent to synchronized method

	public synchronized void blah() {
	  // do stuff
	}

* The only real difference is that a synchronized block can choose which object
  it synchronizes on.
* A `synchronized` method can only use `this`
	* Or the corresponding `class` instance for a synchronized `static` class method


#### synchronized(this) block

* Unlike synchronized methods, synchronized statements must specify the object that
  provides the intrinsic lock

#### synchronized method

Two Effects:

1. When one thread is executing a synchronized method for an object, all other
   threads that invoke synchronized methods for the same object block (suspend
   execution) until the first thread is done with the object.
2. When a synchronized method exits, it automatically establishes a happens-before
   relationship with any subsequent invocation of a synchronized method for the same
   object. This guarantees that changes to the state of the object are visible to
   all threads. (not sure what this means)
   
Other notes:

* Constructors cannot be synchronized — using the synchronized keyword with a
  constructor is a syntax error. Synchronizing constructors doesn't make sense,
  because only the thread that creates an object should have access to it while
  it is being constructed.

#### Other

* Every object has an intrinsic lock associated with it
	* This is what the implementation of `synchronized` relies upon to use the
	  `monitor` synchronization pattern
* Note that of course we *could* also use an "explicit lock" such as
  `java.util.concurrent.locks.ReentrantLock`, which provides the same implications
  for memory behavior.
  

### Volatile Fields

* Guarantees that every thread accessing a volatile field will read its current value
  before continuing, instead of (potentially) using a cached value.
* However, there is no guarantee about the relative ordering of volatile reads and
  writes with regular reads and writes, meaning that it's generally not a useful
  threading construct.
* Volatile reads and writes establish a happens-before relationship, much like acquiring
  and releasing a mutex.
	* This relationship is simply a guarantee that memory writes by one specific statement
	  are visible to another specific statement.


Inheritance
===========

### Private fields

**5/9/14**

* `private` fields are not inherited by subclasses.
* Both `protected` *and* `public` fields *are* inherited by subclasses,
  *no matter* whether they are in the same `package`.
* Instances of subclasses of course *do **contain*** the private fields
  of their superclasses, they just have no access to them.
  
### Useful chart

**5/9/14**

| Modifier    | Class | Package | Subclass | World|
|:-----------:|:-----:|:-------:|:--------:|:----:|
| public      |   ✔   |    ✔    |    ✔     |   ✔  |
| protected   |   ✔   |    ✔    |    ✔     |   ✘  |
| no modifier |   ✔   |    ✔    |    ✘     |   ✘  |
| private     |   ✔   |    ✘    |    ✘     |   ✘  |


#### Notes on chart

`no modifier` is even stricter than `protected`, and what it *doesn't*
allow is `subclass` access, even though it retains `package` access. I
find this counterintuitive, personally.

### instanceof

**5/9/14**

`child instanceof Parent` will return **`true`**


### The question mark wildcard type

**5/9/14**
[SO](http://stackoverflow.com/questions/3009745/what-does-the-question-mark-in-java-generics-type-parameter-mean)

E.g.

	List<? extends HasWord> wordList = toke.tokenize();
	
This means we'll allow anything that extends `HasWord`, plus `null`, but we don't
care to name the type because we'll just be calling methods that are specified by
`HasWord`.


Other things one simply must know about
=======================================

Reflection
----------
**5/21/14**

This is all just a quote from [StOve](http://stackoverflow.com/questions/37628/what-is-reflection-and-why-is-it-useful/37638#37638)

"Reflection" is a language's ability to inspect and dynamically call classes, methods, attributes, etc. at runtime. For example, all objects in Java has the method getClass, which lets you determine its class even if you don't know it at compile time (like if you declared it as Object) - this might seem trivial, but such reflection is not by default possible in less dynamic languages such as C++.

More advanced uses lets you list and call methods, constructors, etc.

Reflection is important since it lets you write programs that does not have to "know" everything at compile time, making them more dynamic, since they can be tied together at runtime. The code can be written against known interfaces, but the actual classes to be used can be instantiated using reflection from configuration files.

Lots of modern frameworks uses reflection extensively for this very reason.

Most other modern languages uses reflection as well, and in script languages like Python can be said to be even more tightly integrated, since it matches more naturally with the general programming model for those languages.

### Notes from a [great tutorial][]

[great tutorial]: http://tutorials.jenkov.com/java-reflection/index.html

Using Java Reflection you can inspect Java classes at runtime. Inspecting classes is often the first thing you do when using Reflection. From the classes you can obtain information about

* Class Name
* Class Modifies (public, private, synchronized etc.)
* Package Info
* Superclass
* Implemented Interfaces
* Constructors
* Methods
* Fields
* Annotations

You get this from the **`Class` object**, which you get from `Class class = MyObject.class`

### Notes from the [Oracle docs][]

[Oracle docs]: http://docs.oracle.com/javase/tutorial/reflect/

#### Uses

* creating Visual Development Environments (ie. visual IDE features)
* or a Debugger
* or Test Tools

#### Drawbacks

* Slow performance
* Can't be run in certain restricted security contexts (such as in an Applet)
* Allows you to access `private` fields and do unexpected side-effecting and break portability


Annotations
-----------
**5/21/14**

[Oracle Tutorial](http://docs.oracle.com/javase/tutorial/java/annotations/)

* An annotation is metadata about a program that is not part of the program itself
* Info for the compiler -- e.g. detect errors or suppress warnings
* Compile/Deploy-time processing -- generate code for processing XML files
* Runtime processing

### Basics

* Starts with an `@` character
* Can be `@Title(key = value, key2 = value2)` or `@Title(value)`
* You can stack multiple annotations onto one declaration, but style-wise one
  should only have one per line
  
#### Placement options

##### Pre Java 8
* Placed before any sort of *declaration*
	* Class, method, field, etc.
	
##### Java 8+
* Constructor: `new @Annot MyObject();`
* Type cast: `m = (@Ann A) v;`
* Implements: `class A<T> implements @Annot B<@Annot T> {...}`
* Exception declaration: `void meth() throws @Annot Excep {...}`

### Declaring an Annotation

**This part of the explanation is not helpful.**

	@interface AnnotationName {
	  String annotVar1();
	  int annotVar2() default 3;
	  String[] vars3();
	}

Now we can use it like

	@AnnotationName(
	  annotVar1 = "asdf",
	  annotVar2 = 44, 		 // or don't include it bc there's a default
	  vars3 = {"a","b","c"} // no semicolon
	)	

### Predefined annotation types

#### @Deprecated

The compiler generates a warning whenever a program uses something with this annotation.

	/**
	 * @deprecated   // put this in the JavaDoc
	 * reason for deprecation
	 */
	@Deprecated
	static void meth() { }

#### @Override

Informs the compiler that the following element is meant to override something declared
in a superclass. **This annotation is not required, but if you fail to correctly
override something when you *said you were trying to*, you'll get a compiler warning.**

#### The other ones don't look so important

### Why would I use annotations?

[StOve](http://stackoverflow.com/questions/37628/what-is-reflection-and-why-is-it-useful)

Using **reflection** over annotations is how *JUnit* works out your methods are.
When you run JUnit, it uses reflection to look through your classes for methods
tagged with the `@Test` annotation, and then calls them when running the unit test.

JAR
---
**5/20/14**

[JAR Wikipedia][]

* **J**ava **AR**chive (compressed collection of Java files)
* Used to aggregate many Java class files and associated metadata and resources
  (text, images, etc.) into one file to distribute applicatoin software or libraries
  on the Java platform.
* Built on the *ZIP* file format
* Allows Java runtimes to efficiently deploy a set of classes and their associated resources
* Optional **manifest file** can add metadata like
	* dependencies on other JARs (`Class-Path: `)
	* digital signatures
	* Which class to call `main()` on to start the application (`Main-Class: `)
	* Versioning (`Specification-Version: "1.2"`)
* **WAR** (**W**eb application **AR**chive) -- a JAR used to deliver Java server stuff
  and static web pages that together constitute a web application.

[JAR Wikipedia]: http://en.wikipedia.org/wiki/JAR_(file_format)

Equals and HashCode
-------------------

[SO](http://stackoverflow.com/questions/27581)

### Equals

Must be 

* reflexive -- `a.equals(a) == true`
* symmetric -- `a.equals(b) iff b.equals(a)`
* transitive -- `a.equals(b) && b.equals(c) => a.equals(c)`
* consistent -- always returns the same value for the same [unchanged] object

If you *don't* `@Override public boolean equals(Object o){}`, each instance is
*equal only to itself*. If this is what you want: *don't override* `equals` (e.g. `Thread`).


### Hash Code

Rules

* `a.equals(b) => a.hashCode() == b.hashCode()`


Comparable vs. Comparator
-------------------------

Refs:
[SO](http://stackoverflow.com/questions/4108604),
[digizol](http://www.digizol.com/2008/07/java-sorting-comparator-vs-comparable.html)

* Both are **`interfaces`** you can implement
* **Comparable says "I can compare *myself* with another object"**
* **Comparator says "I can compare two *other* objects with each other"**
* **Use `comparable` if it's your class, otw use `comparator`**


### Comparable

* **Says "I can compare *myself* with another object"**
* Allows you to define comparison logic for *your own* types.

#### Signature
  
	java.lang.Comparable: int compareTo(Object o1) {
		
		case this > o2 => x > 0;
		case this = o2 => x = 0;
		case this < o2 => x < 0;
	
	}
	
#### How to declare

	public class MyClass implements Comparable<MyClass> {
	
		public int compareTo(MyClass o) {
			return this.field - o.field;
		}
	
	}

#### How to use

Simply declare it as in the "How to declare" section above.

### Comparator

* **Says "I can compare two *other* objects with each other"**
* Allows *you* to define comparison logic for types you don't control.
* E.g. you could write a new way to compare strings by `extending
  Comparator`.

#### Signature
  
	java.lang.Comparator: int compare(Object o1, Object o2) {
		
		case o1 > o2 => x > 0;
		case o1 = o2 => x = 0;
		case o1 < o2 => x < 0;
	
	}

#### How to declare

	public class MyClassSortByField implements Comparator<MyClass> {
	
		public int compare(MyClass o1, MyClass o2) {
			o1.getField().compareTo(o2.getField());
		}
	
	}
	
#### How to use

Make a method like this


	public static Comparator<Fruit> FruitNameComparator 
                         = new Comparator<Fruit>() {

	    public int compare(Fruit fruit1, Fruit fruit2) {
	
	      String fruitName1 = fruit1.getFruitName().toUpperCase();
	      String fruitName2 = fruit2.getFruitName().toUpperCase();
	
	      //ascending order
	      return fruitName1.compareTo(fruitName2);
	
	      //descending order
	      //return fruitName2.compareTo(fruitName1);
	    }

	};
		
And then do this
	
	import java.util.Arrays;
	
	Fruit[] fruits = new Fruit[4];
 
	Fruit pineappale = new Fruit("Pineapple", "Pineapple description",70); 
	Fruit apple = new Fruit("Apple", "Apple description",100); 
	Fruit orange = new Fruit("Orange", "Orange description",80); 
	Fruit banana = new Fruit("Banana", "Banana description",90); 

	fruits[0] = pineappale;
	fruits[1] = apple;
	fruits[2] = orange;
	fruits[3] = banana;

	Arrays.sort(fruits); // ClassCastException
	
	Arrays.sort(fruits, Fruit.FruitNameComparator);  // works

Asides
======

[`this` keyword](http://stackoverflow.com/questions/577575/using-the-keyword-this-in-java)

It can be used to access enclosing instances from within a nested class:

	public class MyClass {
	    String name = "asd";
	    private class InnerClass {
	        String name = "bte";
	        public String getOuterName() {
	            return MyClass.this.name; // "asd"
	        }
	    }
	}