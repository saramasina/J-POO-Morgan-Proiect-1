package org.poo.platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Utils;

public class Card {
    @Getter @Setter
    private String cardNumber;
    @Getter @Setter
    private String status;
    @Getter @Setter @JsonIgnore
    private String type;

    public Card() {
        cardNumber = Utils.generateCardNumber();
    }

    public Card(Card card) {
        cardNumber = card.getCardNumber();
        status = card.getStatus();
    }
}
