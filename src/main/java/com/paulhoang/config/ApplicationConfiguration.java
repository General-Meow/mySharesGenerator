package com.paulhoang.config;

import com.paulhoang.Application;

/**
 * Created by paul on 28/11/2015.
 */
public class ApplicationConfiguration {

    private String profile;
    private int port;
    private String applicationContext;
    private String generate;
    private String generateAdvanced;
    private String kill;
    private String pushEndpoint;

    private static ApplicationConfiguration instance;

    private ApplicationConfiguration() {

    }

    public static ApplicationConfiguration getInstance(){
        if(instance == null)
        {
            instance = new ApplicationConfiguration();
        }
        return instance;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(String applicationContext) {
        this.applicationContext = applicationContext;
    }

    public String getGenerate() {
        return generate;
    }

    public void setGenerate(String generate) {
        this.generate = generate;
    }

    public String getGenerateAdvanced() {
        return generateAdvanced;
    }

    public void setGenerateAdvanced(String generateAdvanced) {
        this.generateAdvanced = generateAdvanced;
    }

    public String getKill() {
        return kill;
    }

    public void setKill(String kill) {
        this.kill = kill;
    }

    public String getPushEndpoint() {
        return pushEndpoint;
    }

    public void setPushEndpoint(String pushEndpoint) {
        this.pushEndpoint = pushEndpoint;
    }
}
