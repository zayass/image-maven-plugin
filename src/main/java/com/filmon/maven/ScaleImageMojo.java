package com.filmon.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
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
        final Log log = getLog();

        for (Image imageDefinition : images) {
            final File input  = imageDefinition.getSource();
            final String destination = imageDefinition.getDestination();

            File output = new File(destination);
            if (!output.isAbsolute()) {
                output = new File(outputDirectory, destination);
            }

            if (!input.exists()) {
                log.error(String.format("Input file %s does not exists", input));
                continue;
            }

            if (output.exists()) {
                log.info(String.format("Output file %s skipped because it already exists",  output));
                continue;
            }

            try {
                BufferedImage image = ImageIO.read(input);
                BufferedImage thumbnail = Scalr.resize(
                        image,
                        Scalr.Method.ULTRA_QUALITY,
                        imageDefinition.getWidth()
                );

                ImageReader inputReader = getImageReader(input);
                String format = inputReader.getFormatName();

                ImageIO.write(thumbnail, format, output);

                log.info(String.format("%s:%s generated", output, imageDefinition.getWidth()));
            } catch (IOException e) {
                log.error(input.toString());
                log.error(e);
            }
        }
    }

    private ImageReader getImageReader(final File file) {
        final ImageInputStream imageStream;
        try {
            imageStream = ImageIO.createImageInputStream(file);
        } catch (IOException e) {
            return null;
        }

        final Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageStream);
        if (!imageReaders.hasNext()) {
            return null;
        }

        return imageReaders.next();
    }
}