package org.poo.platform;

import java.util.List;

public class Commerciant {
    private int id;
    private String description;
    private List<String> commerciants;

    public Commerciant(List<String> commerciants, String description, int id) {
        this.commerciants = commerciants;
        this.description = description;
        this.id = id;
    }
}
