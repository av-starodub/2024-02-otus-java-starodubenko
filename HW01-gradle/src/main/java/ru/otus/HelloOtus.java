package ru.otus;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HelloOtus {
    public static void main(String[] args) {
        Stopwatch timer = Stopwatch.createStarted();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100_000_000; i++) {
            list.add("Hello Otus!");
        }
        Lists.reverse(list);
        timer.stop();
        System.out.println("Running time: " + timer.elapsed(TimeUnit.MILLISECONDS) + "ms.");
    }
}
