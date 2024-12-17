package org.poo.platform.commands.workflow_commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.Account;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpendingsReport extends Command {
    private int startTimestamp;
    private int endTimestamp;
    private int timestamp;
    private Account account;
    private ArrayNode output;
    private ArrayNode copyOutput;
    private ArrayNode commerciants;
    private User user;

    public SpendingsReport(String account, int startTimestamp, int endTimestamp, int timestamp, ArrayList<User> users, ArrayNode output) {
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
            if (account.getType().equals("savings")) {
                ObjectNode outputNode = mapper.createObjectNode();
                ObjectNode objectNode = mapper.createObjectNode();

                objectNode.put("command", "spendingsReport");
                outputNode.put("error", "This kind of report is not supported for a saving account");

                objectNode.set("output", outputNode);

                // Add additional fields
                objectNode.putPOJO("timestamp", timestamp);
                output.add(objectNode);
                return;
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                copyOutput = mapper.createArrayNode();
                commerciants = mapper.createArrayNode();
                objectNode.put("command", "spendingsReport");

                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("IBAN", account.getIBAN());
                outputNode.put("balance", account.getBalance());
                outputNode.put("currency", account.getCurrency());

                ObjectNode nodeCommerciant;
                for (JsonNode node : account.getTransactions()) {
                    if (node.get("timestamp").asInt() >= startTimestamp && node.get("timestamp").asInt() <= endTimestamp && node.get("description").asText().equals("Card payment")) {
                        copyOutput.add(node);
                        nodeCommerciant = mapper.createObjectNode();
                        nodeCommerciant.put("commerciant", node.get("commerciant").asText());
                        nodeCommerciant.put("total", node.get("amount").asDouble());
                        commerciants.add(nodeCommerciant);

                    }
                }

                List<JsonNode> nodeList = new ArrayList<>();
                commerciants.forEach(nodeList::add);

                nodeList.sort(Comparator.comparing(node -> node.get("commerciant").asText()));

                // Reconstruim ArrayNode cu nodurile sortate
                commerciants = mapper.createArrayNode();
                nodeList.forEach(commerciants::add);

                outputNode.putPOJO("transactions", copyOutput);
                outputNode.putPOJO("commerciants", commerciants);

                objectNode.set("output", outputNode);

                // Add additional fields
                objectNode.putPOJO("timestamp", timestamp);
                output.add(objectNode);
            }
        } else {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("command", "spendingsReport");

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", "Account not found");
            outputNode.put("timestamp", timestamp);

            objectNode.set("output", outputNode);

            // Add additional fields
            objectNode.putPOJO("timestamp", timestamp);
            output.add(objectNode);
        }
    }
}
