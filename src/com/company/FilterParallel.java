package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/*
 * Author: Shenbaga Prasanna,IT,SASTRA University;
 * Program: Median FilterParallel To Reduce Noice in Image
 * Date: 9/7/2013
 * Logic: Captures the colour of 8 pixels around the target pixel.Including the target pixel there will be 9 pixels.
 *        Isolate the R,G,B values of each pixels and put them in an array.Sort the arrays.Get the Middle value of the array
 *        Which will be the Median of the color values in those 9 pixels.Set the color to the Target pixel and move on!
 */
public class FilterParallel implements MedianFilter {

    private BufferedImage image;
    private int white;
    private int black;
    private int imgWidth;
    private int imgHeight;
    private int threads;
    private List<BufferedImage> subImages = new ArrayList<>();

    public FilterParallel(BufferedImage image, int threads) {
        this.image = image;
        this.threads = threads;
        this.imgHeight = image.getHeight();
        this.imgWidth = image.getWidth();
    }

    public FilterParallel(BufferedImage image, int threads, int white, int black) {
        this.image = image;
        this.white = white;
        this.black = black;
        this.threads = threads;
        this.imgHeight = image.getHeight();
        this.imgWidth = image.getWidth();
    }

    //dont know if correct dimensions
    public BufferedImage filterWithMedian() {
        int subHeight = image.getHeight() / threads;
        MedianFilter[] workers = new MedianFilter[threads];

        int totalHeight = image.getHeight();
        int totalWidth = image.getWidth();

        //for loop om de bufferedImage op te delen in subImages
        for (int j = 0; j < threads; j++) {
            int height = subHeight * j;
            subImages.set(j, image.getSubimage(0, height, this.imgWidth, subHeight + height));
        }

        for (int i = 0; i < threads; i++) {
            int height = subHeight * i;
            workers[i] = new MedianFilter(subImages.get(i));
            workers[i].start();
        }

        for (MedianFilter worker : workers) {
            try {
                worker.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //concatonate al the subimages
        int heightCurr = 0;
        BufferedImage concatImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = concatImage.createGraphics();
        for (int j = 0; j < subImages.size(); j++) {
            g2d.drawImage(subImages.get(j), 0, heightCurr, null);
            heightCurr += subImages.get(j).getHeight();
        }
        g2d.dispose();

        return concatImage;
    }

    class MedianFilter extends Thread {

        private int startX;     // top left coordinate of the sub image
        private int startY;     // top right coordinate of the sub image
        private int width;      // bottom left coordinate of the sub image
        private int height;     // bottom right coordinate of the sub image

        private MedianFilter(int startX, int startY, int width, int height) {
            this.startX = startX;
            this.startY = startY;
            this.width = width - 1;
            this.height = height;
        }

        private MedianFilter(BufferedImage image) {
            this.startX = image.getMinX();
            this.startY = image.getMinY();
            this.width = image.getWidth();
            this.height = image.getHeight();
        }

        @Override
        public void run() {
            for (int i = this.startX; i < this.width; i++) {
                if (i >= image.getWidth()) break;
                for (int j = this.startY; j < this.height; j++) {
                    if (j >= image.getHeight() - 1) break;

//                    int pixelColor = new Color(image.getRGB(i, j)).getRGB();
//                    if (!(pixelColor > white || pixelColor < black)) continue;
                    image.setRGB(i, j, calcMedianPixel(i, j));
                }
            }
        }

        private int calcMedianPixel(int i, int j) {
            Color[] pixel = new Color[9];

            int k = 0;
            for (int x = i - 1; x <= i + 1; x++) {
                for (int y = j - 1; y <= j + 1; y++) {
                    if (isOutofbounds(x, y)) {
                        pixel[k] = Color.WHITE;
                    } else {
                        pixel[k] = new Color(image.getRGB(x, y));
                    }
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

        private boolean isOutofbounds(int i, int j) {
            if (i < 0 || i > imgWidth) {
                return true;
            } else if (j < 0 || j > imgHeight) {
                return true;
            }
            return false;
        }
    }
}