package com.company.csp.params;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class MedianSend implements Serializable {

    private byte[] subImage;
    private Integer id;

    public MedianSend(int id, byte[] subImage) {
        this.id = id;
        this.subImage = subImage;
    }

    public byte[] getSubImage() {
        return subImage;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "id: " + this.id + " with img size: " + this.subImage.length;
    }
}
