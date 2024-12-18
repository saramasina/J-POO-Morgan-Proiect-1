package org.poo.platform.commands.workflow.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.*;
import org.poo.platform.accounts.Account;
import org.poo.platform.commands.Command;
import org.poo.platform.exchange.CurrencyConverter;
import org.poo.platform.exchange.Exchange;

import java.util.ArrayList;

public final class PayOnline implements Command {
    private final String toCurrency;
    private String fromCurrency;
    private final int timestamp;
    private double amount;
    private Account account;
    private final ArrayNode output;
    private final String commerciant;
    private User user;
    private Card card;
    private final CurrencyConverter converter = new CurrencyConverter();

    public PayOnline(final String cardNumber, final double amount, final String currency,
                     final int timestamp, final String commerciant, final String email,
                     final ArrayList<User> users, final ArrayList<Exchange> exchangeRates,
                     final ArrayNode output) {
        this.timestamp = timestamp;
        this.toCurrency = currency;
        this.amount = amount;
        this.output = output;
        this.commerciant = commerciant;
        for (User userIter : users) {
            if (userIter.getEmail().equals(email)) {
                for (Account accountIter : userIter.getAccounts()) {
                    for (Card cardIter : accountIter.getCards()) {
                        if (cardIter.getCardNumber().equals(cardNumber)) {
                            account = accountIter;
                            user = userIter;
                            fromCurrency = accountIter.getCurrency();
                            card = cardIter;
                        }
                    }
                }
            }
        }
        for (Exchange exchange : exchangeRates) {
            converter.addExchangeRate(exchange.getFrom(), exchange.getTo(), exchange.getRate());
        }
    }

    /**
     * Handles a card payment operation, including currency conversion,
     * balance checks, and card status updates.
     * Adds appropriate transaction records to the account based on the operation outcome.
     */
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
                    outputNodeRemoval.put("account", account.getIban());
                    outputNodeRemoval.put("card", card.getCardNumber());
                    outputNodeRemoval.put("cardHolder", user.getEmail());

                    account.getTransactions().add(outputNodeRemoval);

                    // If the card is of type oneTime, we remove it and create a new card instead
                    account.getCards().remove(card);
                    Card newCard = new Card("oneTime");
                    newCard.setStatus(card.getStatus());
                    account.getCards().add(newCard);

                    ObjectNode outputNodeAdd = mapper.createObjectNode();
                    outputNodeAdd.put("description", "New card created");
                    outputNodeAdd.put("timestamp", timestamp);
                    outputNodeAdd.put("account", account.getIban());
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

            objectNode.putPOJO("timestamp", timestamp);

            output.add(objectNode);
        }
    }
}
