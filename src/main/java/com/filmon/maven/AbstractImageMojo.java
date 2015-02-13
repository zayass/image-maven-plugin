package com.filmon.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractImageMojo extends AbstractMojo {
    private ProcessingChain processingChain;
    
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;
    @Parameter(required = false)
    private List<Image> images;

    protected File getOutputDirectory() {
        return outputDirectory;
    }

    protected List<Image> getImages() {
        return images;
    }

    protected abstract BufferedImage processImage(Image imageDefinition, BufferedImage inputImage) throws MojoExecutionException;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        processingChain = new ProcessingChain(getPluginContext());
        
        for (Image imageDefinition : getImages()) {
            processImageInternal(imageDefinition);
        }
    }

    private void processImageInternal(Image imageDefinition) throws MojoExecutionException {
        final String destination = imageDefinition.getDestination();
        final Integer width = imageDefinition.getWidth();


        File output = new File(destination);
        if (!output.isAbsolute()) {
            output = new File(getOutputDirectory(), destination);
        }

        if (isFresh(output)) {
            getLog().info(String.format("Output file %s skipped because it is fresh", output.getAbsolutePath()));
            return;
        }

        final File input = !processingChain.isInQueue(output) ?
                imageDefinition.getSource() : output;


        BufferedImage inputImage = getInputImage(input);
        BufferedImage outputImage = processImage(imageDefinition, inputImage);
        
        if (outputImage == null || outputImage == inputImage) {
            return;
        }
        
        writeImage(outputImage, getFormatName(input), output);
        processingChain.enqueue(output);
        
        getLog().info(String.format("%s:%s generated", output, width));
    }

    private boolean isFresh(File output) {
        return output.exists() && 
                !processingChain.isInQueue(output);
    }


    private BufferedImage getInputImage(final File file) throws MojoExecutionException {
        if (!file.exists()) {
            throw new MojoExecutionException(String.format("Input file %s does not exists", file.getAbsolutePath()));
        }
        
        if (!file.canRead()) {
            throw readFailed(file);
        }

        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw notAnImage(file);
        }
    }

    protected MojoExecutionException readFailed(final File file) {
        return new MojoExecutionException(String.format(
                "Cannot read input file %s", file.getAbsolutePath()
        ));
    }

    protected MojoExecutionException writeFailed(final File file) {
        return new MojoExecutionException(String.format(
                "Cannot write output file %s",
                file.getAbsolutePath()
        ));
    }

    protected MojoExecutionException notAnImage(final File file) {
        return new MojoExecutionException(String.format(
                "input file %s not an image/unknown format", file.getAbsolutePath()
        ));
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

}
