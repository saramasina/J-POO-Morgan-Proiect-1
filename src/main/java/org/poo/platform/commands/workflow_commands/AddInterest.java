package org.poo.platform.commands.workflow_commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.Account;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class AddInterest extends Command{
    private int timestamp;
    private Account account;
    private ArrayNode output;

    public AddInterest(String account, int timestamp, ArrayList<User> users, ArrayNode output) {
        this.timestamp = timestamp;
        this.output = output;
        for (User user : users) {
            for (Account accountUser : user.getAccounts()) {
                if (accountUser.getIBAN().equals(account)) {
                    this.account = accountUser;
                }
            }
        }
    }

    @Override
    public void operation() {
        if (account != null) {
            if (account.getType().equals("classic")) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("command", "addInterest");

                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("description", "This is not a savings account");
                outputNode.putPOJO("timestamp", timestamp);

                objectNode.set("output", outputNode);

                // Add additional fields
                objectNode.putPOJO("timestamp", timestamp);

                output.add(objectNode);
            }
        }
    }
}
