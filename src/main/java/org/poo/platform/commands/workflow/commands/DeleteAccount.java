package org.poo.platform.commands.workflow.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.accounts.Account;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public final class DeleteAccount implements Command {
    private User user;
    private Account account;
    private int timestamp;
    private ArrayNode output;

    public DeleteAccount(final String iban, final int timestamp, final String email,
                         final ArrayList<User> users, final ArrayNode output) {
        this.timestamp = timestamp;
        this.output = output;
        for (User userIter : users) {
            if (userIter.getEmail().equals(email)) {
                user = userIter;
                for (Account accountIter : user.getAccounts()) {
                    if (accountIter.getIban().equals(iban)) {
                        account = accountIter;
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

            objectNode.putPOJO("timestamp", timestamp);
            output.add(objectNode);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("command", "deleteAccount");

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("error",
                    "Account couldn't be deleted - see org.poo.transactions for details");
            outputNode.put("timestamp", timestamp);

            objectNode.set("output", outputNode);

            objectNode.putPOJO("timestamp", timestamp);
            output.add(objectNode);

            ObjectNode outputNodeUser = mapper.createObjectNode();
            outputNodeUser.put("description",
                    "Account couldn't be deleted - there are funds remaining");
            outputNodeUser.put("timestamp", timestamp);

            account.getTransactions().add(outputNodeUser);
        }
    }
}
