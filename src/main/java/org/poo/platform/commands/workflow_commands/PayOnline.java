package org.poo.platform.commands.workflow_commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.*;
import org.poo.platform.commands.Command;

import java.util.ArrayList;
import java.util.Currency;

public class PayOnline extends Command {
    private String toCurrency;
    private String fromCurrency;
    private int timestamp;
    private double rate;
    private double amount;
    private Account account;
    private ArrayNode output;
    private String commerciant;
    private User user;
    private CurrencyConverter converter = new CurrencyConverter();

    public PayOnline(String cardNumber, double amount, String currency, int timestamp, String description, String commerciant, String email, ArrayList<User> users, ArrayList<Exchange> exchangeRates, ArrayNode output) {
        this.timestamp = timestamp;
        this.toCurrency = currency;
        this.amount = amount;
        this.output = output;
        this.commerciant = commerciant;
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                for (Account account : user.getAccounts()) {
                    for (Card card : account.getCards()) {
                        if (card.getCardNumber().equals(cardNumber)) {
                            this.account = account;
                            this.user = user;
                            fromCurrency = account.getCurrency();
                        }
                    }
                }
            }
        }
        for (Exchange exchange : exchangeRates) {
            converter.addExchangeRate(exchange.getFrom(), exchange.getTo(), exchange.getRate());
        }
    }

    @Override
    public void operation() {
        if (account == null) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("command", "payOnline");

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", "Card not found");
            outputNode.putPOJO("timestamp", timestamp);

            objectNode.set("output", outputNode);

            // Add additional fields
            objectNode.putPOJO("timestamp", timestamp);

            output.add(objectNode);
        }
        if (fromCurrency != null && toCurrency != null && !fromCurrency.equals(toCurrency)) {
            amount = converter.convert(fromCurrency, toCurrency, amount);
        }
        if (account != null && (account.getBalance() - amount >= 0)) {
            account.setBalance(account.getBalance() - amount);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", "Card payment");
            outputNode.put("timestamp", timestamp);
            outputNode.put("commerciant", commerciant);
            outputNode.put("amount", amount);

            user.getTransactions().add(outputNode);
        }
        if (account != null && (account.getBalance() - amount < 0)) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", "Insufficient funds");
            outputNode.put("timestamp", timestamp);

            user.getTransactions().add(outputNode);
        }
    }
}
