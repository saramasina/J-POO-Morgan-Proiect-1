package org.poo.platform.commands.workflow.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.accounts.Account;
import org.poo.platform.Card;
import org.poo.platform.User;
import org.poo.platform.commands.Command;
import java.util.ArrayList;

public final class CheckCardStatus implements Command {
    private int timestamp;
    private User user;
    private Account account;
    private ArrayNode output;
    private Card card;
    private static final int MINDIFFERENCE = 30;

    public CheckCardStatus(final String cardNumber, final int timestamp,
                           final ArrayList<User> users, final ArrayNode output) {
        this.timestamp = timestamp;
        this.output = output;
        for (User userIter : users) {
            for (Account accountIter : userIter.getAccounts()) {
                for (Card cardIter : accountIter.getCards()) {
                    if (cardIter.getCardNumber().equals(cardNumber)) {
                        this.user = userIter;
                        this.account = accountIter;
                        this.card = cardIter;
                    }
                }
            }
        }
    }

    /**
     * Freezes the card if the balance is 0 or approaching
     * the minimum balance (operation done only if this command is called!)
     */
    public void operation() {
        if (account != null && user != null) {
            if (account.getBalance() == 0
                    || (account.getBalance() - account.getMinBalance() <= MINDIFFERENCE)) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("description",
                        "You have reached the minimum amount of funds, the card will be frozen");
                outputNode.put("timestamp", timestamp);

                card.setStatus("frozen");

                account.getTransactions().add(outputNode);
            }
        } else {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("command", "checkCardStatus");

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", "Card not found");
            outputNode.putPOJO("timestamp", timestamp);

            objectNode.set("output", outputNode);

            objectNode.putPOJO("timestamp", timestamp);
            output.add(objectNode);
        }
    }
}
