package org.poo.platform.commands.workflow.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.accounts.Account;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public final class Report implements Command {
    private final int startTimestamp;
    private final int endTimestamp;
    private final int timestamp;
    private Account account;
    private final ArrayNode output;

    public Report(final String account, final int startTimestamp, final int endTimestamp,
                  final int timestamp, final ArrayList<User> users,
                  final ArrayNode output) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.timestamp = timestamp;
        this.output = output;
        for (User userIter : users) {
            for (Account accountUser : userIter.getAccounts()) {
                if (accountUser.getIban().equals(account)) {
                    this.account = accountUser;
                }
            }
        }
    }

    @Override
    public void operation() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        if (account != null) {
            ArrayNode copyOutput = mapper.createArrayNode();
            objectNode.put("command", "report");

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("IBAN", account.getIban());
            outputNode.put("balance", account.getBalance());
            outputNode.put("currency", account.getCurrency());
            for (JsonNode node : account.getTransactions()) {
                if (node.get("timestamp").asInt() >= startTimestamp
                        && node.get("timestamp").asInt() <= endTimestamp) {
                    copyOutput.add(node);
                }
            }
            outputNode.putPOJO("transactions", copyOutput);

            objectNode.set("output", outputNode);
        } else {
            objectNode.put("command", "report");

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", "Account not found");
            outputNode.put("timestamp", timestamp);

            objectNode.set("output", outputNode);
        }
        objectNode.putPOJO("timestamp", timestamp);
        output.add(objectNode);
    }
}
