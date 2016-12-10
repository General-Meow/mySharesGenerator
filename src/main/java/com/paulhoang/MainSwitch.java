package com.paulhoang;

/**
 * Created by paul on 09/12/2016.
 */
public class MainSwitch {
    private boolean kill = false;
    private static MainSwitch instance;

    private MainSwitch(){

    }

    public static MainSwitch getInstance(){
        if(instance == null){
            instance = new MainSwitch();
        }

        return instance;
    }

    public void killGenerate(){
        this.kill = true;
    }

    public void resetSwitch(){
        this.kill = false;
    }

    public boolean getKill(){
        return this.kill;
    }
}
