package org.poo.platform;

import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Utils;

public class Card {
    @Getter @Setter
    private String cardNumber;
    @Getter @Setter
    private String status;

    public Card() {
        cardNumber = Utils.generateCardNumber();
        status = "active";
    }

    public Card(Card card) {
        cardNumber = card.getCardNumber();
        status = card.getStatus();
    }
}
