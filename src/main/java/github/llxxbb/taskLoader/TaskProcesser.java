package github.llxxbb.taskLoader;

import java.util.List;

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
