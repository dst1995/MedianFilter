package com.company;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import javax.imageio.*;
/*
 * Author: Shenbaga Prasanna,IT,SASTRA University;
 * Program: Median Filter To Reduce Noice in Image
 * Date: 9/7/2013
 * Logic: Captures the colour of 8 pixels around the target pixel.Including the target pixel there will be 9 pixels.
 *        Isolate the R,G,B values of each pixels and put them in an array.Sort the arrays.Get the Middle value of the array
 *        Which will be the Median of the color values in those 9 pixels.Set the color to the Target pixel and move on!
 */
class MedianFilter{



    public static void main(String[] a)throws Throwable{
        Color[] filterColors = {Color.BLACK, Color.WHITE};
        String photo = "yen110.jpg";

        File f=new File("./photos/noisy/" + photo);                               //Input Photo File
        Color[] pixel=new Color[9];

        File output=new File("./photos/median/" + photo);
        BufferedImage img=ImageIO.read(f);
        for(int i=1;i<img.getWidth()-1;i++)
            for(int j=1;j<img.getHeight()-1;j++)
            {
                if (!Arrays.asList(filterColors).contains(new Color(img.getRGB(i,j)))) continue;

                pixel[0]=new Color(img.getRGB(i-1,j-1));
                pixel[1]=new Color(img.getRGB(i-1,j));
                pixel[2]=new Color(img.getRGB(i-1,j+1));
                pixel[3]=new Color(img.getRGB(i,j+1));
                pixel[4]=new Color(img.getRGB(i+1,j+1));
                pixel[5]=new Color(img.getRGB(i+1,j));
                pixel[6]=new Color(img.getRGB(i+1,j-1));
                pixel[7]=new Color(img.getRGB(i,j-1));
                pixel[8]=new Color(img.getRGB(i,j));
                Arrays.sort(pixel, new Comparator<Color>() {
                    @Override
                    public int compare(Color o1, Color o2) {
                        return o1.getRGB() - o2.getRGB();
                    }
                });
                img.setRGB(i,j, pixel[4].getRGB());
            }
        ImageIO.write(img,"jpg",output);
    }
}
