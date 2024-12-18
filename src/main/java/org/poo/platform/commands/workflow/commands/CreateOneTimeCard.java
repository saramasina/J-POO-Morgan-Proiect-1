package org.poo.platform.commands.workflow.commands;

import org.poo.platform.accounts.Account;
import org.poo.platform.Card;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public final class CreateOneTimeCard implements Command {
    private final int timestamp;
    private User user;
    private final String iban;

    public CreateOneTimeCard(final String email, final String iban,
                             final int timestamp, final ArrayList<User> users) {
        this.timestamp = timestamp;
        for (User userIter : users) {
            if (userIter.getEmail().equals(email)) {
                user = userIter;
            }
        }
        this.iban = iban;
    }

    @Override
    public void operation() {
        if (user != null) {
            for (Account accountUser : user.getAccounts()) {
                if (accountUser.getIban().equals(iban)) {
                    Card newCard = new Card("oneTime");
                    CreateCard.addCard(accountUser, newCard, timestamp, user);
                }
            }
        }
    }
}
