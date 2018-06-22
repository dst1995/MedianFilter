package com.company.csp.processor;

import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class FilterThread extends Thread {

    private int startX;
    private int startY;
    private int width;
    private int height;
    private BufferedImage subImg;
    private BufferedImage filteredSubImage;
    private List<FilterListener> listeners = new ArrayList<>();

    public FilterThread(BufferedImage image) {
        this.subImg = image;
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

        // remove first and last row
        this.filteredSubImage = this.subImg.getSubimage(this.startX, this.startY + 1,
                                                            this.width, this.height - 2);

        //notify listeners
        for (FilterListener hl : listeners)
            hl.filterFinished();

    }

    private int calcMedianPixel(int i, int j) {
        Color[] pixel = new Color[9];

        int k = 0;
        for (int x = i - 1; x <= i + 1; x++) {
            for (int y = j - 1; y <= j + 1; y++, k++) {
                if (!isOutOfBoundsSub(x, y)) {
                    pixel[k] = new Color(this.subImg.getRGB(x, y));
                } else {
                    pixel[k] = Color.WHITE;
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

    public void addListener(FilterListener toAdd) {
        listeners.add(toAdd);
    }

    public BufferedImage getFilteredSubImage() {
        return filteredSubImage;
    }
}
