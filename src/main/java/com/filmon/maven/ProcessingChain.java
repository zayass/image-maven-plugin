package com.filmon.maven;

import java.io.File;
import java.util.Map;

public class ProcessingChain {
    private Map pluginContext;

    public ProcessingChain(Map pluginContext) {
        this.pluginContext = pluginContext;
    }

    @SuppressWarnings("unchecked")
    public void enqueue(File image) {
        pluginContext.put(getKey(image), true);
    }

    public boolean isInQueue(File image) {
        return pluginContext.containsKey(getKey(image));
    }
    
    private String getKey(File image) {
        return getClass().getCanonicalName() + image.getAbsolutePath();
    }
}
