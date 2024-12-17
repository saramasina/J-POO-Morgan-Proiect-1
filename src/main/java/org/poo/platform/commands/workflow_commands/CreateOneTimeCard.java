package org.poo.platform.commands.workflow_commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.Account;
import org.poo.platform.Card;
import org.poo.platform.OneTimeCard;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class CreateOneTimeCard extends Command {
    private int timestamp;
    private User user;
    private String IBAN;

    public CreateOneTimeCard(String email, String IBAN, int timestamp, ArrayList<User> users) {
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
                    Card newCard = new Card();
                    newCard.setType("oneTime");
                    newCard.setStatus("active");
                    accountUser.getCards().add(newCard);
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode outputNode = mapper.createObjectNode();
                    outputNode.put("description", "New card created");
                    outputNode.put("timestamp", timestamp);
                    outputNode.put("card", newCard.getCardNumber());
                    outputNode.put("cardHolder", user.getEmail());
                    outputNode.put("account", accountUser.getIBAN());

                    accountUser.getTransactions().add(outputNode);
                }
            }
        }
    }
}
