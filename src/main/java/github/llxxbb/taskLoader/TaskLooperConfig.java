package github.llxxbb.taskLoader;


/**
 * Configuration for `TaskLooper`
 */
public class TaskLooperConfig implements Cloneable {

    /**
     * The max number of tasks can be hold by the queue.
     * If the tasks number is over it, `TaskLoader` will sleep
     * until the number below it.
     */
    public int queueLen = 1000;

    public TaskCommand cmd = TaskCommand.RUN;
    /**
     * how many milliseconds to sleep when ther is no task.
     */
    public int idleTime = 1;

    public int getThreadNum() {
        return threadNum;
    }

    /**
     * @param threadNum if less than 1 , the Task Looper will be stop
     */
    public void setThreadNum(int threadNum) {
        if (threadNum <= 0) {
            cmd = TaskCommand.STOP;
            return;
        }
        this.threadNum = threadNum;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) throws Exception {
        if (limit < 1) {
            throw new Exception("limit must great than 0");
        }
        if (limit > 2000) {
            throw new Exception("limit can't be over 2000");
        }
        this.limit = limit;
    }

    /**
     * The limit records number for task laod.
     */
    private int limit = 100;
    /**
     * how many thread will be run
     */
    private int threadNum = 1;

    public TaskLooperConfig clone() throws CloneNotSupportedException {
        return (TaskLooperConfig) super.clone();
    }

}
