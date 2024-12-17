package org.poo.platform.commands.workflow_commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.Account;
import org.poo.platform.Card;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CheckCardStatus extends Command {
    private int timestamp;
    private User user;
    private Account account;
    private ArrayNode output;
    private Card card;

    public CheckCardStatus(String cardNumber, int timestamp, ArrayList<User> users, ArrayNode output) {
        this.timestamp = timestamp;
        this.output = output;
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        this.user = user;
                        this.account = account;
                        this.card = card;
                    }
                }
            }
        }
    }

    public void operation() {
        if (account != null && user != null) {
//            if (account.isFrozen() == true) {
//                ObjectMapper mapper = new ObjectMapper();
//                ObjectNode outputNode = mapper.createObjectNode();
//                outputNode.put("description", "The card is frozen");
//                outputNode.put("timestamp", timestamp);
//
//                user.getTransactions().add(outputNode);
//            }
            if (account.getBalance() == 0 || (account.getBalance() - account.getMinBalance() <= 30)) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("description", "You have reached the minimum amount of funds, the card will be frozen");
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

            // Add additional fields
            objectNode.putPOJO("timestamp", timestamp);
            output.add(objectNode);
        }
    }
}
