package com.ipmessenger;

import javax.swing.*;
import java.util.ArrayList;

public class checkingAvailIps extends Thread {

    private JList<String> list;
    private DefaultListModel ips;
    private String subnet;
    private ArrayList<String> myips;

    public checkingAvailIps(JList<String> list, DefaultListModel ips, String subnet, ArrayList<String> myips) {
        this.list = list;
        this.ips = ips;
        this.subnet = subnet;
        this.myips=myips;
    }

    @Override
    public void run() {
        super.run();

        for (int i=0;i<255;i++) {
            for (int j = 0; j < 255; j++) {
                checkingByPing c = new checkingByPing(subnet,i,j,list,ips,myips);
                c.start();
                try {
//                    sleep(10);

                }catch (Exception e)
                {

                }
            }
        }

    }
}
