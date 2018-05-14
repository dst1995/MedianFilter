package com.company;

import java.util.concurrent.TimeUnit;

/*
    a class used for measuring a time in microseconds
 */
public class Stopwatch {
    private Long startTime;
    private Long endTime;
    private Long duration;

    public Stopwatch() {
        this.duration = new Long(0);
    }

    public Long getDuration() {
        if(this.endTime.longValue() == 0) {
            Long currentTime = System.nanoTime();
            Long runtime = TimeUnit.MICROSECONDS.convert(currentTime - startTime, TimeUnit.NANOSECONDS);
            return runtime + duration;
        } else {
            return duration;
        }
    }

    public void start() {
        this.startTime = System.nanoTime();
        this.endTime = Long.valueOf(0);
    }

    public void stop() {
        this.endTime = System.nanoTime();
        this.duration += TimeUnit.MICROSECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
    }

    public void reset() {
        this.startTime = Long.valueOf(0);
        this.endTime = Long.valueOf(0);
        this.duration = Long.valueOf(0);
    }

    public void restart() {
        this.endTime = Long.valueOf(0);
        this.duration = Long.valueOf(0);
        this.startTime = System.nanoTime();
    }
}
