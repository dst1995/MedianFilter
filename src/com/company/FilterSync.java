package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

public class FilterSync {

    private BufferedImage image;
    private Color[] filterColors = {Color.BLACK, Color.WHITE};

    public FilterSync(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage filterWithMedian() {
        Color[] pixel = new Color[9];
        for(int i = 1; i< image.getWidth()-1; i++) {
            for (int j = 1; j < image.getHeight() - 1; j++) {
                if (!Arrays.asList(filterColors).contains(new Color(image.getRGB(i, j)))) continue;

                pixel[0] = new Color(image.getRGB(i - 1, j - 1));
                pixel[1] = new Color(image.getRGB(i - 1, j));
                pixel[2] = new Color(image.getRGB(i - 1, j + 1));
                pixel[3] = new Color(image.getRGB(i, j + 1));
                pixel[4] = new Color(image.getRGB(i + 1, j + 1));
                pixel[5] = new Color(image.getRGB(i + 1, j));
                pixel[6] = new Color(image.getRGB(i + 1, j - 1));
                pixel[7] = new Color(image.getRGB(i, j - 1));
                pixel[8] = new Color(image.getRGB(i, j));

                Arrays.sort(pixel, new Comparator<Color>() {
                    @Override
                    public int compare(Color o1, Color o2) {
                        return o1.getRGB() - o2.getRGB();
                    }
                });

                image.setRGB(i, j, pixel[4].getRGB());
            }
        }
        return image;
    }
}
