package org.poo.platform.commands.workflow_commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.Account;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class DeleteAccount extends Command {
    private User user;
    private Account account;
    private int timestamp;
    private ArrayNode output;

    public DeleteAccount(String IBAN, int timestamp, String email, ArrayList<User> users, ArrayNode output) {
        this.timestamp = timestamp;
        this.output = output;
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                this.user = user;
                for (Account account : user.getAccounts()) {
                    if (account.getIBAN().equals(IBAN)) {
                        this.account = account;
                    }
                }
            }
        }
    }

    @Override
    public void operation() {
        if (account != null && account.getBalance() == 0) {
            user.getAccounts().remove(account);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("command", "deleteAccount");

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("success", "Account deleted");
            outputNode.putPOJO("timestamp", timestamp);

            objectNode.set("output", outputNode);

            // Add additional fields
            objectNode.putPOJO("timestamp", timestamp);
            output.add(objectNode);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("command", "deleteAccount");

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
            outputNode.put("timestamp", timestamp);

            objectNode.set("output", outputNode);

            // Add additional fields
            objectNode.putPOJO("timestamp", timestamp);
            output.add(objectNode);

            ObjectNode outputNodeUser = mapper.createObjectNode();
            outputNodeUser.put("description", "Account couldn't be deleted - there are funds remaining");
            outputNodeUser.put("timestamp", timestamp);

            account.getTransactions().add(outputNodeUser);
        }
    }
}
