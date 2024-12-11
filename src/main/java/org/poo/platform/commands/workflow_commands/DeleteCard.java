package org.poo.platform.commands.workflow_commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.Account;
import org.poo.platform.Card;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class DeleteCard extends Command {
    private int timestamp;
    private Account account;
    private Card card;
    private User user;

    public DeleteCard(String cardNumber, int timestamp, ArrayList<User> users) {
        this.timestamp = timestamp;
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        this.account = account;
                        this.card = card;
                        this.user = user;
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

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", "The card has been destroyed");
            outputNode.put("card", card.getCardNumber());
            outputNode.put("cardHolder", user.getEmail());
            outputNode.put("account", account.getIBAN());
            outputNode.put("timestamp", timestamp);

            user.getTransactions().add(outputNode);
        }
    }
}
