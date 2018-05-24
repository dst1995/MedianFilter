package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
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

    private BufferedImage imageMain;
    private int white;
    private int black;
    private int imgWidth;
    private int imgHeight;
    private int threads;
    private List<BufferedImage> subImages;

    public FilterParallel(BufferedImage imageMain, int threads) {
        this.imageMain = imageMain;
        this.threads = threads;
        this.imgHeight = imageMain.getHeight();
        this.imgWidth = imageMain.getWidth();
        this.subImages = new ArrayList<BufferedImage>(Collections.nCopies(threads, null));
    }

    public FilterParallel(BufferedImage imageMain, int threads, int white, int black) {
        this.imageMain = imageMain;
        this.white = white;
        this.black = black;
        this.threads = threads;
        this.imgHeight = imageMain.getHeight();
        this.imgWidth = imageMain.getWidth();
        this.subImages = new ArrayList<BufferedImage>(Collections.nCopies(threads, null));
    }

    //dont know if correct dimensions
    public BufferedImage filterWithMedian() {
        int subHeight = imageMain.getHeight() / threads;
        MedianFilterThread[] workers = new MedianFilterThread[threads];

        int totalHeight = imageMain.getHeight();
        int totalWidth = imageMain.getWidth();
        imageMain.getRGB(594, 499);
        for (int i = 0; i < threads; i++) {
            int startHeight = subHeight * i;
            BufferedImage subImg = imageMain.getSubimage(0, startHeight, this.imgWidth, subHeight);
            workers[i] = new MedianFilterThread(i, subImg);
            workers[i].start();
        }

        for (MedianFilterThread worker : workers) {
            try {
                worker.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //concatonate al the subimages
        int heightCurr = 0;
        BufferedImage concatImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = concatImage.createGraphics();
        for (int j = 0; j < subImages.size(); j++) {
            BufferedImage si = subImages.get(j);
            g2d.drawImage(subImages.get(j), 0, heightCurr, null);
            heightCurr += subImages.get(j).getHeight();
        }
        g2d.dispose();

        return concatImage;
    }

    class MedianFilterThread extends Thread {

        private int startX;
        private int startY;
        private int width;
        private int height;
        private BufferedImage subImg;
        private int id;     // id of the thread

        private MedianFilterThread(int startX, int startY, int width, int height) {
            this.startX = startX;
            this.startY = startY;
            this.width = width - 1;
            this.height = height;
        }

        private MedianFilterThread(int id, BufferedImage image) {
            this.subImg = image;
            this.id = id;
            this.startX = image.getMinX();
            this.startY = image.getMinY();
            this.width = image.getWidth();
            this.height = image.getHeight();
        }

        @Override
        public void run() {
            for (int i = this.startX; i < this.width; i++) {
                if (i >= this.subImg.getWidth()) continue;
                for (int j = this.startY; j < this.height+1; j++) {
                    if (j >= this.subImg.getHeight()) break;

//                    int pixelColor = new Color(imageMain.getRGB(i, j)).getRGB();
//                    if (!(pixelColor > white || pixelColor < black)) continue;
                    this.subImg.setRGB(i, j, calcMedianPixel(i, j));
                }
            }
            subImages.set(this.id, this.subImg);
        }

        private int calcMedianPixel(int i, int j) {
            Color[] pixel = new Color[9];

            int k = 0;
            for (int x = i - 1; x <= i + 1; x++) {
                for (int y = j - 1; y <= j + 1; y++, k++) {
                    if (!isOutOfBoundsSub(x, y)) {
                        pixel[k] = new Color(this.subImg.getRGB(x, y));
                    } else {
                        pixel[k] = getMainPixel(x, y);
                    }
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

        private boolean isOutOfBoundsSub(int i, int j) {
            if (i < 0 || i >= this.width) {
                return true;
            } else if (j < 0 || j >= this.height) {
                return true;
            }
            return false;
        }

        private Color getMainPixel(int x, int y) {
            int mainHeight = y + this.id * this.height;
            if ((x >= 0 && x < imgWidth)
                    &&  (mainHeight > 0 && mainHeight < imgHeight)) {
                return new Color(imageMain.getRGB(x, mainHeight));
            } else {
                return Color.WHITE;
            }
        }
    }
}