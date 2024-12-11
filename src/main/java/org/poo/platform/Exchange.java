package org.poo.platform;

import lombok.Getter;
import lombok.Setter;

public class Exchange {
    @Getter @Setter
    private String from;
    @Getter @Setter
    private String to;
    @Getter @Setter
    private double rate;
    @Getter @Setter
    private int timestamp;

    public Exchange(String from, int timestamp, double rate, String to) {
        this.from = from;
        this.timestamp = timestamp;
        this.rate = rate;
        this.to = to;
    }
}
