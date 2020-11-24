package github.llxxbb.taskLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * by lxb
 *
 * @param <T>
 */
public class TaskLooperImpl<T> implements TaskLooper {

    private final TaskProcesser<T> processer;
    private TaskLooperConfig cfg;
    private ExecutorService works;
    private final LinkedBlockingQueue<Runnable> workQueue;
    private static final Logger logger = LoggerFactory.getLogger(TaskLooperImpl.class);
    private final AtomicBoolean terminate = new AtomicBoolean(false);

    public TaskLooperImpl(TaskProcesser<T> processer, TaskLooperConfig cfg) throws Exception {
        if (cfg == null) throw new Exception("cfg can't be null");
        this.processer = processer;
        workQueue = new LinkedBlockingQueue<>();
        this.cfg = cfg.clone();
        if (cfg.cmd == TaskCommand.STOP) {
            return;
        }
        start();
    }

    private void start() {
        logger.info("--------------looper starting------------------");
        logger.info(" thread num : " + cfg.getThreadNum());
        logger.info(" load limit : " + cfg.getLimit());
        logger.info(" idle sleep : " + cfg.idleTime + "(ms)");
        logger.info(" cmd        : " + cfg.cmd.toString());
        works = new ThreadPoolExecutor(cfg.getThreadNum(), cfg.getThreadNum(),
                0L, TimeUnit.MILLISECONDS, workQueue);
        Thread loopThread = new Thread(this::myLoop);
        loopThread.start();
    }

    @Override
    synchronized public void setConfig(TaskLooperConfig cfg) throws Exception {
        if (cfg == null) throw new Exception("cfg can't be null");
        if (this.cfg != null && this.cfg.cmd == TaskCommand.RUN) {
            shutdownWorks();
        }
        this.cfg = cfg.clone();
        if (cfg.cmd == TaskCommand.RUN) {
            start();
        }
    }

    private void shutdownWorks() {
        if (works == null) {
            return;
        }
        if (cfg.cmd == TaskCommand.STOP) {
            return;
        }

        try {
            works.shutdown();
            works.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("tasks interrupted");
        } finally {
            if (!works.isTerminated()) {
                logger.warn("cancel non-finished tasks");
            }
            works.shutdownNow();
        }
        terminate.set(true);
        while (terminate.get()) {
            sleep();
        }
    }

    private void myLoop() {
        int queueLen = cfg.getThreadNum() * 2;
        while (true) {
            // process command
            if (terminate.get()) {
                terminate.set(false);
                logger.info("--------------looper stopped------------------");
                break;
            }
            // check unprocessed
            int size = workQueue.size();
            if (size > queueLen) {
                logger.warn("  There is a backlog of data in the queue");
                sleep();
                continue;
            }
            // get tasks
            List<T> tasks = null;
            try {
                tasks = processer.getTasks(cfg.getLimit());
            } catch (Throwable e) {
                logger.warn("    **** Get tasks occurs error ****", e);
                sleep();
            }
            if (tasks == null || tasks.isEmpty()) {
                sleep();
                continue;
            }
            // run tasks
            for (T t : tasks) {
                try {
                    works.submit(() -> processer.doTask(t));
                } catch (Throwable e) {
                    logger.warn("", e);
                }
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(cfg.idleTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
