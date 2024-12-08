package org.poo.platform.commands.workflow_commands;

import org.poo.platform.Account;
import org.poo.platform.Card;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class CreateCard extends Command {
    private int timestamp;
    private User user;
    private String IBAN;

    public CreateCard(String email, String IBAN, int timestamp, ArrayList<User> users) {
        this.timestamp = timestamp;
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                this.user = user;
            }
        }
        this.IBAN = IBAN;
    }

    @Override
    public void operation() {
        if (user != null) {
            for (Account accountUser : user.getAccounts()) {
                if (accountUser.getIBAN().equals(IBAN)) {
                    accountUser.getCards().add(new Card());
                }
            }
        }
    }
}
