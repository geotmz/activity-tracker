package src;

import src.master.Master;
import src.worker.Worker;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProcessManager {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 1) {
            System.err.println("Required argument: number of workers (int)");
            System.exit(2);
        }
        try {
            Integer.valueOf(args[0]);
        } catch (Exception e) {
            System.err.println("Argument worker count is not integer");
            System.exit(2);
        }

        LinkedList<Process> children = new LinkedList<>();

        Process master = execProcess(Master.class, args);
        for (int i = 0; i < Integer.parseInt(args[0]); ++i) {
            children.add(execProcess(Worker.class, args));
        }

        // total number of spawned processes: n+1 (master)
        try {
            master.waitFor();
            for (var c : children) {
                if (c.waitFor(500, TimeUnit.MILLISECONDS)) {
                    System.err.println("shut down cleanly pid: " + c.pid() + "\texit val: " + c.exitValue());
                }
            }
        } finally {
            for (var c : children) {
                c.destroy();
            }
        }
    }

    // https://stackoverflow.com/a/723914
    private static Process execProcess(Class<?> cls, String[] args) throws IOException {
        List<String> command = new LinkedList<>();

        command.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        // class path java flag
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(cls.getName());
        Collections.addAll(command, args);

        return new ProcessBuilder(command).inheritIO().start();
    }
}
