package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

public class FilterSerial implements MedianFilter{

    private Stopwatch stopwatch = new Stopwatch();
    private BufferedImage image;
    private int white;
    private int black;

    public FilterSerial(BufferedImage image) {
        this.image = image;
    }
    public FilterSerial(BufferedImage img, int white, int black) {
        this.image = img;
        this.white = white;
        this.black = black;
    }

    public BufferedImage filterWithMedian() {
        Color[] pixel = new Color[9];
        for(int i = 1; i< image.getWidth()-1; i++) {
            for (int j = 1; j < image.getHeight()-1; j++) {
//                int pixelColor = new Color(image.getRGB(i, j)).getRGB();
//                if (!(pixelColor > white || pixelColor < black)) continue;

                image.setRGB(i, j, calcMedianPixel(i, j));
            }
        }
        return image;
    }

    private int calcMedianPixel(int i, int j) {
        Color[] pixel = new Color[9];

        int k = 0;
        for (int x = i - 1; x <= i + 1; x++) {
            for (int y = j - 1; y <= j + 1; y++, k++) {
                if (isOutofbounds(x, y)) {
                    pixel[k] = Color.WHITE;
                } else {
                    pixel[k] = new Color(image.getRGB(x, y));
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

    private boolean isOutofbounds(int i, int j) {
        if (i < 0 || i > image.getWidth()) {
            return true;
        } else if (j < 0 || j > image.getHeight()) {
            return true;
        }
        return false;
    }

}
