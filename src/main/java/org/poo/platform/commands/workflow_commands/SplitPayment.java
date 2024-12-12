package org.poo.platform.commands.workflow_commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.Account;
import org.poo.platform.CurrencyConverter;
import org.poo.platform.Exchange;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;
import java.util.List;

public class SplitPayment extends Command {
    private ArrayList<Account> accounts = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private List<String> accountsIBAN;
    private double amount;
    private double totalAmount;
    private int timestamp;
    private CurrencyConverter converter = new CurrencyConverter();
    private String currency;

    public SplitPayment(List<String> accounts, double amount, String currency, int timestamp, ArrayList<User> users, ArrayList<Exchange> exchangeRates) {
        this.timestamp = timestamp;
        this.amount = amount / accounts.size();
        totalAmount = amount;
        this.currency = currency;
        accountsIBAN = accounts;
        for (User user : users) {
            for (Account accountUser : user.getAccounts()) {
                for (String account : accounts) {
                    if (account.equals(accountUser.getIBAN())) {
                        this.accounts.add(accountUser);
                        this.users.add(user);
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
        for (User user : users) {
            Account accountUser = accounts.remove(0);
            double amountToPay;
            if (!accountUser.getCurrency().equals(currency)) {
                amountToPay = converter.convert(accountUser.getCurrency(), currency, amount);
            } else {
                amountToPay = amount;
            }
            if (accountUser.getBalance() - amountToPay < accountUser.getMinBalance()) {
                for (User userError : users) {
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode outputNode = mapper.createObjectNode();
                    outputNode.put("description", "Insufficient funds");
                    outputNode.put("timestamp", timestamp);

                    userError.getTransactions().add(outputNode);
                }
                return;
            }
            accountUser.setBalance(accountUser.getBalance() - amountToPay);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", String.format("Split payment of %.2f %s", totalAmount, currency));
            outputNode.put("timestamp", timestamp);
            outputNode.put("currency", currency);
            outputNode.put("amount", amount);
            outputNode.putPOJO("involvedAccounts", accountsIBAN);

            user.getTransactions().add(outputNode);
        }
    }
}
