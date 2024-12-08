package org.poo.platform;

public class Exchange {
    private String from;
    private String to;
    private double rate;
    private int timestamp;

    public Exchange(String from, int timestamp, double rate, String to) {
        this.from = from;
        this.timestamp = timestamp;
        this.rate = rate;
        this.to = to;
    }
}
