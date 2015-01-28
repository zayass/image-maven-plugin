package com.filmon.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Mojo(name="scale", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class ScaleImageMojo extends AbstractMojo {

    @Parameter( defaultValue = "${project.build.outputDirectory}", required = true )
    private File outputDirectory;

    @Parameter(required = false)
    private List<Image> images;

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(final List<Image> images) {
        this.images = images;
    }

    public void execute() throws MojoExecutionException {

        for (Image imageDefinition : images) {
            processImage(imageDefinition);
        }
    }

    private void processImage(Image imageDefinition) throws MojoExecutionException {
        final File input  = imageDefinition.getSource();
        final String destination = imageDefinition.getDestination();

        File output = new File(destination);
        if (!output.isAbsolute()) {
            output = new File(outputDirectory, destination);
        }

        if (!input.exists()) {
            throw new MojoExecutionException(String.format("Input file %s does not exists", input.getAbsolutePath()));
        }

        if (output.exists()) {
            getLog().info(String.format("Output file %s skipped because it already exists", output.getAbsolutePath()));
            return;
        }

        final Integer width = imageDefinition.getWidth();

        BufferedImage thumbnail = Scalr.resize(
                getInputImage(input),
                Scalr.Method.ULTRA_QUALITY,
                width
        );

        thumbnail = crop(imageDefinition, width, thumbnail);

        writeImage(thumbnail, getFormatName(input), output);

        getLog().info(String.format("%s:%s generated", output, width));
    }

    private BufferedImage crop(Image imageDefinition, Integer width, BufferedImage thumbnail) throws MojoExecutionException {
        final Integer cropHeight = imageDefinition.getCropHeight();
        final Integer cropWidth = imageDefinition.getCropWidth();

        if (cropHeight == null || cropWidth == null) {
            return thumbnail;
        }
        
        int heightDelta = cropHeight - thumbnail.getHeight();
        int widthDelta = cropWidth - thumbnail.getWidth();
        
        
        int maxDelta = Math.max(heightDelta, widthDelta);
        if (maxDelta > 0) {
            final Color color = imageDefinition.getColor();
            if (color == null) {
                thumbnail = Scalr.pad(thumbnail, maxDelta / 2);
            } else {
                thumbnail = Scalr.pad(thumbnail, maxDelta / 2, color);
            }
        }
        
        int x = (thumbnail.getWidth() - cropWidth) / 2;
        int y = (thumbnail.getHeight() - cropHeight) / 2;
        
        return Scalr.crop(thumbnail, x, y, cropWidth, cropHeight);
    }

    private String getFormatName(final File file) throws MojoExecutionException {
        final ImageInputStream imageStream = getInputStream(file);

        final Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageStream);

        if (!imageReaders.hasNext()) {
            closeStream(imageStream);
            throw notAnImage(file);
        }

        final ImageReader imageReader = imageReaders.next();

        try {
            return imageReader.getFormatName();
        } catch (IOException e) {
            throw notAnImage(file);
        } finally {
            closeStream(imageStream);
            imageReader.dispose();
        }
    }

    private ImageInputStream getInputStream(final File file) throws MojoExecutionException {
        final ImageInputStream imageStream;
        try {
            imageStream = ImageIO.createImageInputStream(file);
        } catch (IOException e) {
            throw readFailed(file);
        }

        if (imageStream == null) {
            throw notAnImage(file);
        }
        return imageStream;
    }

    private void closeStream(final ImageInputStream imageStream) {
        try {
            imageStream.close();
        } catch (IOException ignored) {
        }
    }

    private void writeImage(final BufferedImage thumbnail, final String format, final File output) throws MojoExecutionException {
        if (output.exists() && !output.canWrite()) {
            throw writeFailed(output);
        }

        final File dir = output.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw writeFailed(output);
        }

        try {
            ImageIO.write(thumbnail, format, output);
        } catch (IOException e) {
            throw writeFailed(output);
        } catch (NullPointerException e) {
            throw writeFailed(output);
        }
    }

    private BufferedImage getInputImage(final File file) throws MojoExecutionException {
        if (!file.canRead()) {
            throw readFailed(file);
        }

        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw notAnImage(file);
        }
    }

    private MojoExecutionException readFailed(final File file) {
        return new MojoExecutionException(String.format(
                "Cannot read input file %s", file.getAbsolutePath()
        ));
    }

    private MojoExecutionException writeFailed(final File file) {
        return new MojoExecutionException(String.format(
                "Cannot write output file %s",
                file.getAbsolutePath()
        ));
    }

    private MojoExecutionException notAnImage(final File file) {
        return new MojoExecutionException(String.format(
                "input file %s not an image/unknown format", file.getAbsolutePath()
        ));
    }
}