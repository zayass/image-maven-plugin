package com.filmon.maven;

import java.util.Map;

public class ProcessingChain {
    private static volatile ProcessingChain instance = null;
    
    private Map pluginContext;

    public ProcessingChain(Map pluginContext) {
        this.pluginContext = pluginContext;
    }

    public static ProcessingChain getInstance(Map pluginContext) {
        if (instance == null) {
            synchronized (ProcessingChain.class) {
                if (instance == null) {
                    instance = new ProcessingChain(pluginContext);
                }
            }
        }
        
        return instance;
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
