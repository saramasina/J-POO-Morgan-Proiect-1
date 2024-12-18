package org.poo.platform.commands.workflow.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.accounts.Account;
import org.poo.platform.exchange.CurrencyConverter;
import org.poo.platform.exchange.Exchange;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public final class SendMoney implements Command {
    private Account giver;
    private Account receiver;
    private double amount;
    private final int timestamp;
    private String fromCurrency;
    private String toCurrency;
    private final double oldAmount;
    private Account accountFrom;
    private Account accountTo;
    private final String description;
    private final CurrencyConverter converter = new CurrencyConverter();

    public SendMoney(final String account, final String receiver, final double amount,
                     final int timestamp, final String description, final ArrayList<User> users,
                     final ArrayList<Exchange> exchangeRates, final String alias) {
        this.timestamp = timestamp;
        this.amount = amount;
        oldAmount = amount;
        this.description = description;
        for (User user : users) {
            for (Account acc : user.getAccounts()) {
                // the giver account is found
                if (account != null && account.equals(acc.getIban())) {
                    this.giver = acc;
                    toCurrency = acc.getCurrency();
                    accountFrom = acc;
                }
                // the receiver account can be an IBAN or an alias
                if (receiver != null && receiver.equals(acc.getIban())) {
                    this.receiver = acc;
                    fromCurrency = acc.getCurrency();
                    accountTo = acc;
                }
                if (alias != null && alias.equals(acc.getAlias())) {
                    this.receiver = acc;
                    fromCurrency = acc.getCurrency();
                    accountTo = acc;
                }
            }
        }
        for (Exchange exchange : exchangeRates) {
            converter.addExchangeRate(exchange.getFrom(), exchange.getTo(), exchange.getRate());
        }
    }

    @Override
    public void operation() {
        if (fromCurrency != null && toCurrency != null && !fromCurrency.equals(toCurrency)) {
            amount = converter.convert(toCurrency, fromCurrency, amount);
        }
        // the giver account has enough funds to do the transfer
        if (giver != null && receiver != null && (giver.getBalance() - oldAmount >= 0)) {
            giver.setBalance(giver.getBalance() - oldAmount);
            receiver.setBalance(receiver.getBalance() + amount);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", description);
            outputNode.put("timestamp", timestamp);
            outputNode.put("amount", oldAmount + " " + toCurrency);
            outputNode.put("senderIBAN", giver.getIban());
            outputNode.put("receiverIBAN", receiver.getIban());
            outputNode.put("transferType", "sent");

            accountFrom.getTransactions().add(outputNode);

            ObjectNode outputNodeTo = mapper.createObjectNode();
            outputNodeTo.put("description", description);
            outputNodeTo.put("timestamp", timestamp);
            outputNodeTo.put("amount", amount + " " + fromCurrency);
            outputNodeTo.put("senderIBAN", giver.getIban());
            outputNodeTo.put("receiverIBAN", receiver.getIban());
            outputNodeTo.put("transferType", "received");

            accountTo.getTransactions().add(outputNodeTo);
        } else if (giver != null
                && (giver.getBalance() - oldAmount < giver.getMinBalance()) && !giver.isFrozen()) {
            // if the balance is 0,
            // the output will only pe printed the first time the owner tries to make a transaction
            if (giver.getBalance() == 0) {
                giver.setFrozen(true);
            }
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", "Insufficient funds");
            outputNode.put("timestamp", timestamp);

            accountFrom.getTransactions().add(outputNode);
        }
    }
}
