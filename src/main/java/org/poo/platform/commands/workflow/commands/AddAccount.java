package org.poo.platform.commands.workflow.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.accounts.Account;
import org.poo.platform.accounts.ClassicAccount;
import org.poo.platform.accounts.SavingsAccount;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public final class AddAccount implements Command {
    private String currency;
    private String accountType;
    private int timestamp;
    private double interestRate;
    private User user;

    public AddAccount(final String email, final String currency, final String accountType,
                      final int timestamp, final double interestRate, final ArrayList<User> users) {
        this.currency = currency;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.interestRate = interestRate;
        for (User userIter : users) {
            if (userIter.getEmail().equals(email)) {
                this.user = userIter;
            }
        }
    }

    @Override
    public void operation() {
        Account account;
        if (accountType.equals("classic")) {
            account = new ClassicAccount(currency);
        } else {
            account = new SavingsAccount(currency, interestRate);
        }
        user.addAccount(account);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("description", "New account created");
        outputNode.put("timestamp", timestamp);

        account.getTransactions().add(outputNode);
    }
}
