package org.poo.platform.commands.debug_commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class PrintUsers extends Command {
    private ArrayList<User> users;
    private int timestamp;
    private ArrayNode output;

    public PrintUsers(ArrayList<User> users_out, int timestamp, ArrayNode output) {
        users = new ArrayList<>();
        for (User user_iter : users_out) {
            users.add(new User(user_iter));
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
