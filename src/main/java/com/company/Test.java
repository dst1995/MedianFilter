package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Test {
    public static Stopwatch stopwatch = new Stopwatch();

    public static final String PHOTO = "landscape.jpg"; //photo to filter, found in photos/noisy
    public static final int TEST_CASES = 40; // how many times the algorithm has to run for a performance metric

    //test serial implementation
    public static final boolean TEST = false;

    //test (new) parallel implementation
    public static final boolean MULTITHREAD_TEST = true;
    public static final int[] TEST_THREADS = {1, 2};   //which thread amounts to test

    public static final int OUTPUT = 2; //what kind of image output, 1: serial, 2: parallel, 3: both
    public static final int THREADS = 2;  //thread amount for parallel output

    public static void main(String[] a) throws Throwable {

        File f = new File("./photos/noisy/" + PHOTO);        //Input Photo File
        BufferedImage img = ImageIO.read(f);

        FilterParallel filterPar = new FilterParallel(img, THREADS);
        FilterSerial filterSer = new FilterSerial(img);

        if(TEST) {
            System.out.println("Serial: " + testPerformance(filterSer));
        }

        if(MULTITHREAD_TEST) {
            for (int thread : TEST_THREADS) {
                System.out.println(thread + "#new: " + testPerformance(new FilterParallel(img, thread)));
            }
        }

        if(OUTPUT == 1 || OUTPUT == 3) { // serial
            BufferedImage imgFilteredSer = filterSer.filterWithMedian();
            File outputSer = new File("./photos/median/" + "ser_" + PHOTO);
            ImageIO.write(imgFilteredSer, "jpg", outputSer);
        }
        if(OUTPUT == 2 || OUTPUT == 3) { // parallel
            BufferedImage imgFilteredPar = filterPar.filterWithMedian();
            File outputPar = new File("./photos/median/" + "par_" + PHOTO);
            ImageIO.write(imgFilteredPar, "jpg", outputPar);
        }

        System.out.println("Finished");
    }

    public static double testPerformance(MedianFilter filter) {
        ArrayList<Long> times = new ArrayList<>();

        for (int i = 0; i < TEST_CASES + 1; i++) {
            stopwatch.restart();
            BufferedImage imgFilteredPar = filter.filterWithMedian();
            times.add(stopwatch.getDuration());
        }

        times.remove(0);
        long sum = 0;
        for (long t : times) sum += t;
        double avg = sum / times.size();
        return avg;
    }
}
