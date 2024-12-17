package org.poo.platform.commands.workflow_commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.Account;
import org.poo.platform.SavingsAccount;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class ChangeInterestRate extends Command{
    private int timestamp;
    private Account account;
    private ArrayNode output;
    private double interestRate;

    public ChangeInterestRate(double interestRate, String account, int timestamp, ArrayList<User> users, ArrayNode output) {
        this.timestamp = timestamp;
        this.output = output;
        this.interestRate = interestRate;
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
            ObjectMapper mapper = new ObjectMapper();
            if (account.getType().equals("classic")) {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("command", "changeInterestRate");

                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("description", "This is not a savings account");
                outputNode.putPOJO("timestamp", timestamp);

                objectNode.set("output", outputNode);

                // Add additional fields
                objectNode.putPOJO("timestamp", timestamp);

                output.add(objectNode);
            } else {
                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("description", "Interest rate of the account changed to " + interestRate);
                outputNode.putPOJO("timestamp", timestamp);
                account.getTransactions().add(outputNode);

                SavingsAccount savingsAccount = (SavingsAccount) account;
                savingsAccount.setInterestRate(interestRate);
            }
        }
    }
}
