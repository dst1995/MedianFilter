package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageByte {

    public byte[] imageToByte(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    public BufferedImage byteToImage(byte[] bytes) throws IOException{
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage image = ImageIO.read(in);
        return image;
    }
}
