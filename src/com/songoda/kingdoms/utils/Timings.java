package com.songoda.kingdoms.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.Bukkit;


/**
 * Small class that helps with timing stuff
 */
public class Timings {

    /**
     * Times a task, will print the result with log level finer
     *
     * @param name     the name of the task
     * @param executor the task to be timed
     */
    public static void time(String name, Runnable runnable) {
        LocalDateTime start = LocalDateTime.now();
        runnable.run();
        LocalDateTime end = LocalDateTime.now();

        Duration duration = Duration.between(start, end);
        String time = LocalTime.MIDNIGHT.plus(duration).format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
        Bukkit.getLogger().info("Timings: " + name + " took " + time);
    }
    
    public interface TimingsExecutor {

        void execute();
    }
    
}