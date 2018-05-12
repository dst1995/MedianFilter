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

            File output = new File("./photos/median/" + photo);
            BufferedImage img = ImageIO.read(f);

            FilterParallel filter = new FilterParallel(img, 4);
            BufferedImage imgFiltered = filter.filterWithMedian();
            ImageIO.write(imgFiltered, "jpg", output);

            System.out.println(stopwatch.getDuration());
        }
}
