package github.llxxbb.taskLoader;

/**
 * Embed `TaskLooperImpl` instance,
 * Maintain the lastId property for looping page tasks.
 * @param <T>
 */
public abstract class TaskProcessorIdBase<T> implements TaskProcesser<T> {

    private long lastId = 0;

    public TaskProcessorIdBase(TaskLooperConfig cfg) throws Exception {
        new TaskLooperImpl<>(this, cfg);
    }

    public long getLastId() {
        return lastId;
    }

    public void setLastId(int taskNum, long lastId) {
        // 只提取两分钟内的数据
        if (taskNum > 0) this.lastId = lastId;
        else this.lastId = 0;
    }
}
