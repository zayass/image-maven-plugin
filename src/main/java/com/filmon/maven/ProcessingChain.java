package com.filmon.maven;

import java.util.Map;

public class ProcessingChain {
    private Map pluginContext;

    public ProcessingChain(Map pluginContext) {
        this.pluginContext = pluginContext;
    }

    @SuppressWarnings("unchecked")
    public void enqueue(Image image) {
        pluginContext.put(getKey(image), true);
    }

    public boolean isInQueue(Image image) {
        return pluginContext.containsKey(getKey(image));
    }
    
    private String getKey(Image image) {
        return getClass().getCanonicalName() + image.getSource().getAbsolutePath();
    }
}
