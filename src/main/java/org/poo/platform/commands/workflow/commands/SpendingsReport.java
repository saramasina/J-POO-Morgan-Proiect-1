package org.poo.platform.commands.workflow.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.accounts.Account;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SpendingsReport implements Command {
    private final int startTimestamp;
    private final int endTimestamp;
    private final int timestamp;
    private Account account;
    private final ArrayNode output;

    public SpendingsReport(final String account, final int startTimestamp,
                           final int endTimestamp, final int timestamp,
                           final ArrayList<User> users, final ArrayNode output) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.timestamp = timestamp;
        this.output = output;
        for (User user : users) {
            for (Account accountUser : user.getAccounts()) {
                if (accountUser.getIban().equals(account)) {
                    this.account = accountUser;
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
                outputNode.put("error",
                        "This kind of report is not supported for a saving account");

                objectNode.set("output", outputNode);

                objectNode.putPOJO("timestamp", timestamp);
                output.add(objectNode);
            } else {
                ObjectNode objectNode = mapper.createObjectNode();
                ArrayNode copyOutput = mapper.createArrayNode();
                ArrayNode commerciants = mapper.createArrayNode();
                objectNode.put("command", "spendingsReport");

                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("IBAN", account.getIban());
                outputNode.put("balance", account.getBalance());
                outputNode.put("currency", account.getCurrency());

                // create a new arrayNode with the commerciants
                ObjectNode nodeCommerciant;
                for (JsonNode node : account.getTransactions()) {
                    if (node.get("timestamp").asInt() >= startTimestamp
                            && node.get("timestamp").asInt() <= endTimestamp
                            && node.get("description").asText().equals("Card payment")) {
                        copyOutput.add(node);
                        nodeCommerciant = mapper.createObjectNode();
                        nodeCommerciant.put("commerciant", node.get("commerciant").asText());
                        nodeCommerciant.put("total", node.get("amount").asDouble());
                        commerciants.add(nodeCommerciant);

                    }
                }

                List<JsonNode> nodeList = new ArrayList<>();
                commerciants.forEach(nodeList::add);

                // sort the commerciants alphabetically, by their name
                nodeList.sort(Comparator.comparing(node -> node.get("commerciant").asText()));

                // reconstruct the arrayNode with the sorted commerciants
                commerciants = mapper.createArrayNode();
                nodeList.forEach(commerciants::add);

                outputNode.putPOJO("transactions", copyOutput);
                outputNode.putPOJO("commerciants", commerciants);

                objectNode.set("output", outputNode);

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

            objectNode.putPOJO("timestamp", timestamp);
            output.add(objectNode);
        }
    }
}
