package com.filmon.maven;

import java.io.File;

public class Image {
    private File source;
    private String destination;
    private int width;
    private Integer cropWidth;
    private Integer cropHeight;

    public File getSource() {
        return source;
    }

    public void setSource(final File source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(final String destination) {
        this.destination = destination;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(final Integer width) {
        this.width = width;
    }
    
    public void setCropWidth(Integer width) {
        this.cropWidth = width;
    }
    public void setCropHeight(Integer height) {
        this.cropHeight = height;
    }
    Integer getCropWidth() {
        return cropWidth;
    }
    Integer getCropHeight() {
        return cropHeight;
    }
}
