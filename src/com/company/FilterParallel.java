package com.company;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/*
 * Author: Shenbaga Prasanna,IT,SASTRA University;
 * Program: Median FilterParallel To Reduce Noice in Image
 * Date: 9/7/2013
 * Logic: Captures the colour of 8 pixels around the target pixel.Including the target pixel there will be 9 pixels.
 *        Isolate the R,G,B values of each pixels and put them in an array.Sort the arrays.Get the Middle value of the array
 *        Which will be the Median of the color values in those 9 pixels.Set the color to the Target pixel and move on!
 */
public class FilterParallel {

    private BufferedImage image;
    private Color[] filterColors = {Color.BLACK, Color.WHITE};
    private int threads;

    public FilterParallel(BufferedImage image, int threads) {
        this.image = image;
        this.threads = threads;
    }

    public FilterParallel(BufferedImage image, Color[] filterColors, int threads) {
        this.image = image;
        this.filterColors = filterColors;
        this.threads = threads;
    }

    //dont know if correct dimensions
    public BufferedImage filterWithMedian() {

        int subHeight = image.getHeight() / threads;
        MedianFilter[] workers = new MedianFilter[threads];
        for (int i = 0; i < threads; i++) {
            int height = subHeight * i;
            workers[i] = new MedianFilter(0, height, image.getWidth(), subHeight);
            workers[i].run();
        }

        for (MedianFilter worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return this.image;
    }

    class MedianFilter extends Thread {

        private int startX;     // top left coordinate of the sub image
        private int startY;     // top right coordinate of the sub image
        private int width;      // bottom left coordinate of the sub image
        private int height;     // bottom right coordinate of the sub image

        private MedianFilter(int startX, int startY, int width, int height) {
            this.startX = startX + 1;
            this.startY = startY + 1;
            this.width = width;
            this.height = height;
        }

        @Override
        public void run() {
            for (int i = this.startX; i < this.width - 1; i++) {
                if(i >= image.getWidth()) break;
                for (int j = this.startY; j < this.height - 1; j++) {
                    if(j >= image.getHeight() -1 ) break;

                    if (Arrays.asList(filterColors).contains(new Color(image.getRGB(i, j)))) {
                        image.setRGB(i, j, calcMedianPixel(i, j));
                    }
                }
            }
        }

        private int calcMedianPixel(int i, int j) {
            Color[] pixel = new Color[9];

            int k = 0;
            for (int x = i - 1; x <= i + 1; x++) {
                for (int y = j - 1; y <= j + 1; y++) {
                    pixel[k] = new Color(image.getRGB(x, y));
                    k++;
                }
            }

            return sortPixels(pixel)[4].getRGB();
        }

        private Color[] sortPixels(Color[] arr) {
            Arrays.sort(arr, new Comparator<Color>() {
                @Override
                public int compare(Color o1, Color o2) {
                    return o1.getRGB() - o2.getRGB();
                }
            });
            return arr;
        }
    }
}