package org.poo.platform.commands.workflow.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.accounts.Account;
import org.poo.platform.Card;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public final class CreateCard implements Command {
    private final int timestamp;
    private User user;
    private final String iban;

    public CreateCard(final String email, final String iban,
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
                    Card card = new Card("classic");
                    addCard(accountUser, card, timestamp, user);
                }
            }
        }
    }

    /**
     *
     * Method that adds a given card to an account and
     * also updates the transactions of the account
     *
     * @param accountUser the account upon which we operate
     * @param card the card that will be added to the account
     * @param timestamp the timestamp of the operation
     * @param user the owner of the account
     */
    public static void addCard(final Account accountUser, final Card card,
                               final int timestamp, final User user) {
        accountUser.getCards().add(card);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("description", "New card created");
        outputNode.put("timestamp", timestamp);
        outputNode.put("card", card.getCardNumber());
        outputNode.put("cardHolder", user.getEmail());
        outputNode.put("account", accountUser.getIban());

        accountUser.getTransactions().add(outputNode);
    }
}
