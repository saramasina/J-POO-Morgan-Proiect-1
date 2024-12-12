package org.poo.platform.commands.workflow_commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.Account;
import org.poo.platform.CurrencyConverter;
import org.poo.platform.Exchange;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class SendMoney extends Command {
    private Account giver;
    private Account receiver;
    private double amount;
    private int timestamp;
    private String fromCurrency;
    private String toCurrency;
    private double rate;
    private String from;
    private double oldAmount;
    private User userFrom;
    private User userTo;
    private String description;
    private String alias;
    private CurrencyConverter converter = new CurrencyConverter();

    public SendMoney(String account, String receiver, double amount, int timestamp, String description, ArrayList<User> users, ArrayList<Exchange> exchangeRates, String alias) {
        this.timestamp = timestamp;
        this.amount = amount;
        oldAmount = amount;
        this.description = description;
        this.alias = alias;
        for (User user : users) {
            for (Account acc : user.getAccounts()) {
                if (account != null && account.equals(acc.getIBAN())) {
                    this.giver = acc;
                    toCurrency = acc.getCurrency();
                    this.userFrom = user;
                }
                if (receiver != null && receiver.equals(acc.getIBAN())) {
                    this.receiver = acc;
                    fromCurrency = acc.getCurrency();
                    userTo = user;
                }
                if (alias != null && alias.equals(acc.getAlias())) {
                    this.receiver = acc;
                    fromCurrency = acc.getCurrency();
                    userTo = user;
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
            amount = converter.convert(fromCurrency, toCurrency, amount);
        }
        if (giver != null && receiver != null && (giver.getBalance() - oldAmount >= 0)) {
            giver.setBalance(giver.getBalance() - oldAmount);
            receiver.setBalance(receiver.getBalance() + amount);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", description);
            outputNode.put("timestamp", timestamp);
            outputNode.put("amount", oldAmount + " " + toCurrency);
            outputNode.put("senderIBAN", giver.getIBAN());
            outputNode.put("receiverIBAN", receiver.getIBAN());
            outputNode.put("transferType", "sent");

            userFrom.getTransactions().add(outputNode);
        } else if (giver != null && (giver.getBalance() - oldAmount < 0) && !giver.isFrozen()) {
            giver.setFrozen(true);
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", "Insufficient funds");
            outputNode.put("timestamp", timestamp);

            userFrom.getTransactions().add(outputNode);
        }
    }
}
