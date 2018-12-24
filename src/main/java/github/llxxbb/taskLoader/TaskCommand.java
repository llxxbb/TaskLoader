package github.llxxbb.taskLoader;

/**
 * by lxb 2018-12-8
 *
 * Command for task
 */
public enum TaskCommand {
    STOP(0), RUN(1);

    private final int cmd;

    TaskCommand(int cmd) {
        this.cmd = cmd;
    }
}
