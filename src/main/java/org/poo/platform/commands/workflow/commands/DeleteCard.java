package org.poo.platform.commands.workflow.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.accounts.Account;
import org.poo.platform.Card;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public final class DeleteCard implements Command {
    private final int timestamp;
    private Account account;
    private Card card;
    private User user;

    public DeleteCard(final String cardNumber, final int timestamp,
                      final ArrayList<User> users) {
        this.timestamp = timestamp;
        for (User userIter : users) {
            for (Account accountIter : userIter.getAccounts()) {
                for (Card cardIter : accountIter.getCards()) {
                    if (cardIter.getCardNumber().equals(cardNumber)) {
                        account = accountIter;
                        card = cardIter;
                        user = userIter;
                    }
                }
            }
        }
    }

    @Override
    public void operation() {
        if (account != null) {
            account.getCards().remove(card);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", "The card has been destroyed");
            outputNode.put("card", card.getCardNumber());
            outputNode.put("cardHolder", user.getEmail());
            outputNode.put("account", account.getIban());
            outputNode.put("timestamp", timestamp);

            account.getTransactions().add(outputNode);
        }
    }
}
