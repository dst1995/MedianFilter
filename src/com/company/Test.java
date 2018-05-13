package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Test {
        public static Stopwatch stopwatch = new Stopwatch();

        public static void main(String[] a) throws Throwable {


            stopwatch.start();
            Color[] filterColors = {Color.BLACK, Color.WHITE};
            String photo = "yen110.jpg";

            File f = new File("./photos/noisy/" + photo);        //Input Photo File


            BufferedImage img = ImageIO.read(f);

            FilterParallel filterPar = new FilterParallel(img, 5);
            BufferedImage imgFilteredPar = filterPar.filterWithMedian();
            File outputPar = new File("./photos/median/" + photo + "_par");
            ImageIO.write(imgFilteredPar, "jpg", outputPar);
            System.out.println("Parallel:" + stopwatch.getDuration());

            FilterSync filterSer = new FilterSync(img);
            BufferedImage imgFilteredSer = filterSer.filterWithMedian();
            File outputSer = new File("./photos/median/" + photo + "_ser");
            ImageIO.write(imgFilteredSer, "jpg", outputSer);
            System.out.println("Serial:" + stopwatch.getDuration());
        }
}
