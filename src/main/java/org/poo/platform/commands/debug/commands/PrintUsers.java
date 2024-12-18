package org.poo.platform.commands.debug.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public final class PrintUsers implements Command {
    private ArrayList<User> users;
    private int timestamp;
    private ArrayNode output;

    public PrintUsers(final ArrayList<User> usersOut, final int timestamp,
                      final ArrayNode output) {
        users = new ArrayList<>();
        for (User userIter : usersOut) {
            users.add(new User(userIter));
        }
        this.timestamp = timestamp;
        this.output = output;
    }

    @Override
    public void operation() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", "printUsers");


        objectNode.putPOJO("output", users);
        objectNode.put("timestamp", timestamp);

        output.add(objectNode);
    }
}
