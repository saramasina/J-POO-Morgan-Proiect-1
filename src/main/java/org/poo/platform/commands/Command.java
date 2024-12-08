package org.poo.platform.commands;

import java.util.List;

public class Command {
    private String command;
    private String email;
    private String account;
    private String currency;
    private double amount;
    private double minBalance;
    private String target;
    private String description;
    private String cardNumber;
    private String commerciant;
    private int timestamp;
    private int startTimestamp;
    private int endTimestamp;
    private String receiver;
    private String alias;
    private String accountType;
    private double interestRate;
    private List<String> accounts;

    public Command(String command, List<String> accounts, double interestRate, String accountType, String alias, String receiver, int endTimestamp, int startTimestamp, int timestamp, String commerciant, String cardNumber, String description, String target, double minBalance, double amount, String currency, String account, String email) {
        this.command = command;
        this.accounts = accounts;
        this.interestRate = interestRate;
        this.accountType = accountType;
        this.alias = alias;
        this.receiver = receiver;
        this.endTimestamp = endTimestamp;
        this.startTimestamp = startTimestamp;
        this.timestamp = timestamp;
        this.commerciant = commerciant;
        this.cardNumber = cardNumber;
        this.description = description;
        this.target = target;
        this.minBalance = minBalance;
        this.amount = amount;
        this.currency = currency;
        this.account = account;
        this.email = email;
    }

    public void operation() {

    }

    public Command() {

    }
}
