package com.filmon.maven;

import org.apache.maven.plugin.MojoExecutionException;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.Field;

public class Image {
    private File source;
    private String destination;
    private int width;
    private Integer cropWidth;
    private Integer cropHeight;
    private String color;

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

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getCropWidth() {
        return cropWidth;
    }

    public Integer getCropHeight() {
        return cropHeight;
    }

    public Color getColor() throws MojoExecutionException {
        if (color == null) return null;
        try {
            Field f = Color.class.getField(color);
            return (Color) f.get(null);
        } catch (Exception ex) {
            // ignore
        }
        try {
            return Color.decode(color);
        } catch (NumberFormatException ex) {
            throw new MojoExecutionException("Does not represent color: " + color, ex);
        }
    }
}
