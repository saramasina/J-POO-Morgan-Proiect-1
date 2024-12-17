package org.poo.platform.commands.workflow_commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.*;
import org.poo.platform.commands.Command;
import org.poo.utils.Utils;

import java.util.ArrayList;

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
    private Card card;
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
                            this.card = card;
                        }
                    }
                }
            }
        }
        for (Exchange exchange : exchangeRates) {
            converter.addExchangeRate(exchange.getFrom(), exchange.getTo(), exchange.getRate());
        }
    }

    public static double conditionalRound(double number, int precision) {
        double scale = Math.pow(10, precision); // Pentru a controla precizia
        double roundedValue = Math.round(number * scale) / scale; // Valoarea rotunjită

        // Verificăm dacă numărul este aproape de valoarea rotunjită
        if (Math.abs(number - roundedValue) < 0.0001) {
            return roundedValue;
        }
        return number; // Dacă nu este aproape, returnează numărul original
    }

    @Override
    public void operation() {
        if (account != null) {
            if (fromCurrency != null && toCurrency != null && !fromCurrency.equals(toCurrency)) {
                amount = converter.convert(toCurrency, fromCurrency, amount);
            }
            if (card.getStatus().equals("frozen")) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("description", "The card is frozen");
                outputNode.put("timestamp", timestamp);

                account.getTransactions().add(outputNode);
                return;
            }
            if (account.getBalance() - amount >= account.getMinBalance()) {
                account.setBalance(account.getBalance() - amount);

                ObjectMapper mapper = new ObjectMapper();
                conditionalRound(amount, 1);
                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("description", "Card payment");
                outputNode.put("timestamp", timestamp);
                outputNode.put("commerciant", commerciant);
                outputNode.put("amount", amount);

                account.getTransactions().add(outputNode);

                if (card.getType().equals("oneTime")) {
                    ObjectNode outputNodeRemoval = mapper.createObjectNode();
                    outputNodeRemoval.put("description", "The card has been destroyed");
                    outputNodeRemoval.put("timestamp", timestamp);
                    outputNodeRemoval.put("account", account.getIBAN());
                    outputNodeRemoval.put("card", card.getCardNumber());
                    outputNodeRemoval.put("cardHolder", user.getEmail());

                    account.getTransactions().add(outputNodeRemoval);

                    account.getCards().remove(card);
                    Card newCard = new Card();
                    newCard.setType("oneTime");
                    newCard.setStatus(card.getStatus());
                    account.getCards().add(newCard);

                    ObjectNode outputNodeAdd = mapper.createObjectNode();
                    outputNodeAdd.put("description", "New card created");
                    outputNodeAdd.put("timestamp", timestamp);
                    outputNodeAdd.put("account", account.getIBAN());
                    outputNodeAdd.put("card", newCard.getCardNumber());
                    outputNodeAdd.put("cardHolder", user.getEmail());

                    account.getTransactions().add(outputNodeAdd);
                }
            } else if (account.getBalance() - amount < account.getMinBalance()) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("description", "Insufficient funds");
                outputNode.put("timestamp", timestamp);

                account.getTransactions().add(outputNode);
            }
        } else {
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
    }
}
