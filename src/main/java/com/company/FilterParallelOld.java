package com.company;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;


public class FilterParallelOld implements MedianFilter {

    private BufferedImage image;
    private int white;
    private int black;
    private int imgWidth;
    private int imgHeight;
    private int threads;

    public FilterParallelOld(BufferedImage image, int threads) {
        this.image = image;
        this.threads = threads;
        this.imgHeight = image.getHeight();
        this.imgWidth = image.getWidth();
    }

    public FilterParallelOld(BufferedImage image, int threads, int white, int black) {
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
        for (int i = 0; i < threads; i++) {
            int height = subHeight * i;
            workers[i] = new MedianFilter(0, height, this.imgWidth, subHeight + height);
            workers[i].start();
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
            this.startX = startX;
            this.startY = startY;
            this.width = width - 1;
            this.height = height;
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