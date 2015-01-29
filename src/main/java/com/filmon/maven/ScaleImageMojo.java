package com.filmon.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;

@Mojo(name="scale", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class ScaleImageMojo extends AbstractImageMojo {

    @Override
    protected BufferedImage processImage(Image imageDefinition, BufferedImage inputImage) throws MojoExecutionException {
        final Integer width = imageDefinition.getWidth();

        return Scalr.resize(
                inputImage,
                Scalr.Method.ULTRA_QUALITY,
                width
        );
    }
}