package org.igye.jdebug.debugprocessors.tracemethods;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class ThreadData {
    private String id;
    private String name;
    private PrintStream printStream;
    private List<Float> eventTimes = new ArrayList<>();
    private Stack<String> stack = new Stack<>();

    public ThreadData(String id, String name, PrintStream printStream) {
        this.id = id;
        this.name = name;
        this.printStream = printStream;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Float> getEventTimes() {
        return eventTimes;
    }

    public Stack<String> getStack() {
        return stack;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }
}
