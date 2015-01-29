package com.filmon.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import static org.junit.Assert.*;

public class ImageTest {
    @Test public void whiteByName() throws Exception {
        Image img = new Image();
        img.setColor("white");
        assertEquals(255, img.getColor().getRed());
        assertEquals(255, img.getColor().getGreen());
        assertEquals(255, img.getColor().getBlue());
    }
    @Test public void yelloByName() throws Exception {
        Image img = new Image();
        img.setColor("yellow");
        assertEquals(255, img.getColor().getRed());
        assertEquals(255, img.getColor().getGreen());
        assertEquals(0, img.getColor().getBlue());
    }
    @Test public void yelloByValue() throws Exception {
        Image img = new Image();
        img.setColor("0xffff00");
        assertEquals(255, img.getColor().getRed());
        assertEquals(255, img.getColor().getGreen());
        assertEquals(0, img.getColor().getBlue());
    }
    @Test public void exceptionAtNonsense() {
        try {
            Image img = new Image();
            img.setColor("makesNoSense");
            img.getColor();
        } catch (MojoExecutionException ex) {
            return;
        }
        fail("MojoExecutionException should be thrown");
    }
    
       
}
