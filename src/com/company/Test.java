package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Test {
    public static Stopwatch stopwatch = new Stopwatch();
    public static final String PHOTO = "yen110.jpg";
    public static final int WHITE = new Color(240, 240, 240).getRGB();
    public static final int BLACK = new Color(15, 15, 15).getRGB();
    public static final int THREADS = 10;
    public static final int TEST_CASES = 100;
    public static final boolean TEST = false;
    public static final int OUTPUT = 2; //what kind of image output, 1: serial, 2: parallel, 3: both

    public static void main(String[] a) throws Throwable {

        File f = new File("./photos/noisy/" + PHOTO);        //Input Photo File
        BufferedImage img = ImageIO.read(f);

        FilterParallel filterPar = new FilterParallel(img, THREADS);
        FilterSerial filterSer = new FilterSerial(img);

        if(TEST) {
            System.out.println("Parallel: " + testPerformance(filterPar));
            System.out.println("Serial: " + testPerformance(filterSer));
        }

//        int[] threads = {1, 2, 4, 8, 16, 50, 100};
//        for (int thread : threads) {
//            System.out.println(thread + "#: " +  testPerformance(new FilterParallel(img, thread)));
//        }

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
