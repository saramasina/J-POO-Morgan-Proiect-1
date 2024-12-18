package org.poo.platform.exchange;

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

    public Exchange(final String from, final int timestamp,
                    final double rate, final String to) {
        this.from = from;
        this.timestamp = timestamp;
        this.rate = rate;
        this.to = to;
    }
}
