package org.poo.platform.commands.workflow_commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.Account;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class Report extends Command{
    private int startTimestamp;
    private int endTimestamp;
    private int timestamp;
    private Account account;
    private ArrayNode output;
    private ArrayNode copyOutput;
    private User user;

    public Report(String account, int startTimestamp, int endTimestamp, int timestamp, ArrayList<User> users, ArrayNode output) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.timestamp = timestamp;
        this.output = output;
        for (User user : users) {
            for (Account accountUser : user.getAccounts()) {
                if (accountUser.getIBAN().equals(account)) {
                    this.account = accountUser;
                    this.user = user;
                }
            }
        }
    }

    @Override
    public void operation() {
        if (account != null) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            copyOutput = mapper.createArrayNode();
            objectNode.put("command", "report");

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("IBAN", account.getIBAN());
            outputNode.put("balance", account.getBalance());
            outputNode.put("currency", account.getCurrency());
            for (JsonNode node : user.getTransactions()) {
                if (node.get("timestamp").asInt() >= startTimestamp && node.get("timestamp").asInt() <= endTimestamp) {
                    copyOutput.add(node);
                }
            }
            outputNode.putPOJO("transactions", copyOutput);

            objectNode.set("output", outputNode);

            // Add additional fields
            objectNode.putPOJO("timestamp", timestamp);
            output.add(objectNode);
        }
    }
}
