package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Test {
    public static Stopwatch stopwatch = new Stopwatch();
    public static String photo = "yen110.jpg";
    public static int WHITE = new Color(240, 240, 240).getRGB();
    public static int BLACK = new Color(15, 15, 15).getRGB();
    public static final int THREADS = 4;
    public static final int TEST_CASES = 30;

    public static void main(String[] a) throws Throwable {

        int cl = new Color(255, 255, 255).getRGB();

        File f = new File("./photos/noisy/" + photo);        //Input Photo File

        BufferedImage img = ImageIO.read(f);

        FilterParallel filterPar = new FilterParallel(img, THREADS, WHITE, BLACK);
//        System.out.println("Parallel: " + testPerformance(filterPar));

        FilterSerial filterSer = new FilterSerial(img, WHITE, BLACK);
//        System.out.println("Serial: " + testPerformance(filterSer));


//            stopwatch.start();
        BufferedImage imgFilteredPar = filterPar.filterWithMedian();
        File outputPar = new File("./photos/median/" + photo);
        ImageIO.write(imgFilteredPar, "jpg", outputPar);
//            System.out.println("Parallel:" + stopwatch.getDuration());
//
//            stopwatch.restart();
//            FilterSerial filterSer = new FilterSerial(img);
//        BufferedImage imgFilteredSer = filterSer.filterWithMedian();
//        File outputSer = new File("./photos/median/" + photo);
//        ImageIO.write(imgFilteredSer, "jpg", outputSer);
//            System.out.println("Serial:" + stopwatch.getDuration());
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
