package com.ipmessenger;

import javax.swing.*;

public class checkingAvailIps extends Thread {

    private JList<String> list;
    private DefaultListModel ips;
    private String subnet;

    public checkingAvailIps(JList<String> list, DefaultListModel ips, String subnet) {
        this.list = list;
        this.ips = ips;
        this.subnet = subnet;
    }

    @Override
    public void run() {
        super.run();

        for (int i=0;i<255;i++) {
            for (int j = 0; j < 255; j++) {
                checkingByPing c = new checkingByPing(subnet,i,j,list,ips);
                c.start();
            }
        }

    }
}
