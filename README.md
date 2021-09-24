# TaskLoader

Automatic to load tasks by the capability of the process, and dispatches them to multiple threads to process them.    
## Features

- Pull tasks but no push tasks to process, so no overload problem.
- Configurations can be changed at run time, include thread number, idle time, load size, stop it or run it.

## Basic usage

### maven

```xml
<dependency>
  <groupId>io.github.llxxbb</groupId>
  <artifactId>TaskLoader</artifactId>
  <version>0.2.5</version>
</dependency>
```

### coding

- First : implement `TaskProcesser<T>` interface.

  - **getTasks()**: You can get your tasks by it. The backend will call this in a dead loop, no `Throwable` can breaks it. You can use a `cmd` which defined in `TaskLooperConfig ` to stop it.
  - **doTask()**: use this to process your task. if `Throwable`occurs just ignore it.

- Second : create instance of `TaskLooperImpl` to start processing. 

  Suppose we had implemented `TaskProcesser<T>` as `MyProcesser`, which can process `MyTask`.

  ```java
  TaskProcesser<MyTask> myProcesser = new MyProcesser();
  TaskLooperConfig cfg = new TaskLooperConfig();
  cfg.setThreadNum(16);
  cfg.setLimit(100);
  TaskLooper myLooper =  new TaskLooperImpl<MyTask>(myProcesser, cfg);
  ```

- enjoy!

## Advanced usage

### Adjust at runtime

`TaskLooper` have a `setConfig` method which receive a `TaskLooperConfig` parameter.
Through which we can stop and restart the tasks process, and can do other things like:

    - change works thread number
    - change the size of the tasks for one fetch.
    - how long to sleep when idle

### If your tasks based on id

There is a convenient way : extends from `TaskProcessorIdBase`. Which embeds an instance of  `TaskLooper`  and help you hold the last id of your tasks.

```
public class MyProcessor extends TaskProcessorIdBase<MyTask>{
    @Override
    public List<MyTask> getTasks(int limit) {
        // your code like follows
        // ...
        // List<MyTask> results = dao.get(getLastId());
        // setLastId(results.size(), results.get(results.size()-1).id);
        // return results;
        // ...
    }

    @Override
    public void doTask(MyTask task) {
        // do your task logic
    }
}
```

## ChangeLog

### 0.2.5 2021-08-31

- fix: the tasks would be lost when queue is full.

### 0.2.4 2020-11-27

- add `queueLen` property to `TaskLooperConfig`
- add a base implement for `TaskLooper`  that identified by id 

### 0.2.3 2020-11-24

- let exception do dot break the task loop
- ignore the tasks which occur error.

### 0.2.2 

The first release