package org.poo.platform.commands.debug_commands;

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

public class PrintTransactions extends Command {
    private int timestamp;
    private User user;
    private ArrayNode output;

    public PrintTransactions(String email, int timestamp, ArrayList<User> users, ArrayNode output) {
        this.timestamp = timestamp;
        this.output = output;
        for (User u : users) {
            if (u.getEmail().equals(email)) {
                this.user = u;
            }
        }
    }

    @Override
    public void operation() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        ArrayNode outputTransactions = mapper.createArrayNode();
        objectNode.put("command", "printTransactions");

        for (Account account : user.getAccounts()) {
            for (JsonNode node : account.getTransactions()) {
                ObjectNode newNode = node.deepCopy();
                outputTransactions.add(newNode);
            }
        }

        List<JsonNode> nodeList = new ArrayList<>();
        outputTransactions.forEach(nodeList::add);

        nodeList.sort(Comparator.comparing(node -> node.get("timestamp").asInt()));

        // Reconstruim ArrayNode cu nodurile sortate
        outputTransactions = mapper.createArrayNode();
        nodeList.forEach(outputTransactions::add);

        objectNode.putPOJO("output", outputTransactions);

        // Add additional fields
        objectNode.put("timestamp", timestamp);

        output.add(objectNode);
    }
}
