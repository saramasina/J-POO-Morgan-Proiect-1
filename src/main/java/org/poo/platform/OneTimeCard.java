package org.poo.platform;

import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Utils;

public class OneTimeCard extends Card{
    @Getter @Setter
    private String cardNumber;
    @Getter @Setter
    private String status;

    public OneTimeCard() {
        cardNumber = Utils.generateCardNumber();
    }

    public OneTimeCard(Card card) {
        cardNumber = card.getCardNumber();
        status = card.getStatus();
    }
}
