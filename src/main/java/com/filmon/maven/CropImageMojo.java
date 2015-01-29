package com.filmon.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.image.BufferedImage;

@Mojo(name="crop", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class CropImageMojo extends AbstractImageMojo {

    @Override
    protected BufferedImage processImage(Image imageDefinition, BufferedImage inputImage) throws MojoExecutionException {
        final Integer cropHeight = imageDefinition.getCropHeight();
        final Integer cropWidth = imageDefinition.getCropWidth();

        if (cropHeight == null || cropWidth == null) {
            return inputImage;
        }

        int heightDelta = cropHeight - inputImage.getHeight();
        int widthDelta = cropWidth - inputImage.getWidth();


        int maxDelta = Math.max(heightDelta, widthDelta);
        if (maxDelta > 0) {
            final Color color = imageDefinition.getColor();
            if (color == null) {
                inputImage = Scalr.pad(inputImage, maxDelta / 2);
            } else {
                inputImage = Scalr.pad(inputImage, maxDelta / 2, color);
            }
        }

        int x = (inputImage.getWidth() - cropWidth) / 2;
        int y = (inputImage.getHeight() - cropHeight) / 2;

        return Scalr.crop(inputImage, x, y, cropWidth, cropHeight);
    }
}
