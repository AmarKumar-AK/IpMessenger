package com.ipmessenger;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class progressbar extends Thread {
    private JProgressBar pb;
    private boolean stop;
    JFrame frame = new JFrame("Sending");
    public progressbar() {

        pb = new JProgressBar();
        pb.setValue(0);
        JPanel p = new JPanel();
        p.add(pb);
        pb.setString("Sending....");
        frame.add(p);
        stop=true;
        frame.setSize(200,100);
        frame.setVisible(true);
    }

    //    @Override
    public void run() {
        int i=0;
        pb.setString("Sending...");
        while(stop){
            pb.update(pb.getGraphics());

            pb.setValue(i);
            //pb.repaint();

            i=(i+10)%100;
            try{Thread.sleep(80);}catch(Exception e){}
        }
        pb.setValue(100);
        pb.setString("Sent !");
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        frame.dispose();
        pb.update(pb.getGraphics());

    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}