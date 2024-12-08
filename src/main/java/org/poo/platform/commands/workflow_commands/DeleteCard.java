package org.poo.platform.commands.workflow_commands;

import org.poo.platform.Account;
import org.poo.platform.Card;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class DeleteCard extends Command {
    private int timestamp;
    private Account account;
    private Card card;

    public DeleteCard(String cardNumber, int timestamp, ArrayList<User> users) {
        this.timestamp = timestamp;
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        this.account = account;
                        this.card = card;
                    }
                }
            }
        }
    }

    @Override
    public void operation() {
        if (account != null) {
            int index = account.getCards().indexOf(card);
            account.getCards().remove(index);
        }
    }
}
