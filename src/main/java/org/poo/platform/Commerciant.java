package org.poo.platform;

import java.util.List;

public class Commerciant {
    private int id;
    private String description;
    private List<String> commerciants;

    public Commerciant(final List<String> commerciants,
                       final String description, final int id) {
        this.commerciants = commerciants;
        this.description = description;
        this.id = id;
    }
}
