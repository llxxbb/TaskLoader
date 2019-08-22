# TaskLoader

Automatic to load tasks, dispatch them to multiple threads to process it.
    
## Features

- Don't worry about how often and how many tasks will be load
- Configurations can be changed at run time.

## Usage

## maven

```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.llxxbb</groupId>
    <artifactId>TaskLoader</artifactId>
    <version>0.2.1</version>
</dependency>
```

### First : implement `TaskProcesser<T>` interface

```java
public interface TaskProcesser<T> {
    /**
     * Load task by "{@link TaskLooperImpl}"
     * Attention: don't throw any `Exception`, which can break the loop of the process.
     * @param limit the max task will be needed,
     * @return tasks will be processed
     */
    List<T> getTasks(int limit);

    /**
     * Process one task
     * @param task task will be processed
     */
    void doTask(T task);
}
```
Suppose we had implemented it and name it `MyProcesser`, whtch can process `MyTask`.

### Second : create instance of `TaskLooperImpl`

```java
    TaskProcesser<MyTask> myProcesser = new MyProcesser();
    TaskLooperConfig cfg = new TaskLooperConfig();
    cfg.setThreadNum(16);
    cfg.setLimit(100);
    TaskLooper myLooper =  new TaskLooperImpl<MyTask>(myProcesser, cfg);

```

Then `myLooper` will automatically fetch and process the tasks.

### Adjust at runtime.

`TaskLooper` have a `setConfig` method which receive a `TaskLooperConfig` parameter.
Through which we can stop and restart the tasks process, do other things like:

    - change works thread number
    - change the size of the tasks for one fetch.
    - how long to sleep when idle

