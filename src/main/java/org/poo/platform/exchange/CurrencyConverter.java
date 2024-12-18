package org.poo.platform.exchange;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public final class CurrencyConverter {
    private final Map<String, Map<String, Double>> exchangeGraph = new HashMap<>();

    /**
     * Adds the exchange rates as nodes in my HashMap
     * Each node has a rate associated
     *
     * @param from   The source currency.
     * @param to     The target currency.
     * @param rate   The target's rate
     */
    public void addExchangeRate(final String from, final String to,
                                final double rate) {
        exchangeGraph.putIfAbsent(from, new HashMap<>());
        exchangeGraph.get(from).put(to, rate);

        // add the inverse rate for bidirectional conversion
        exchangeGraph.putIfAbsent(to, new HashMap<>());
        exchangeGraph.get(to).put(from, 1 / rate);
    }

    /**
     * Converts an amount from one currency to another
     * using BFS on an exchange rate graph.
     *
     * @param from   The source currency.
     * @param to     The target currency.
     * @param amount The amount to convert.
     * @return The converted amount in the target currency.
     * @throws IllegalArgumentException If no conversion path exists.
     */
    public double convert(final String from, final String to,
                          final double amount) {
        // BFS to find the shortest path between two nodes
        Queue<String> queue = new LinkedList<>();
        Map<String, Double> visited = new HashMap<>();
        queue.add(from);
        // initial rate is 0
        visited.put(from, 1.0);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            double currentRate = visited.get(current);

            // Iterating through neighbours
            for (Map.Entry<String, Double> neighbor : exchangeGraph.get(current).entrySet()) {
                if (!visited.containsKey(neighbor.getKey())) {
                    double newRate = currentRate * neighbor.getValue();
                    visited.put(neighbor.getKey(), newRate);

                    // final rate found
                    if (neighbor.getKey().equals(to)) {
                        return amount * newRate;
                    }

                    queue.add(neighbor.getKey());
                }
            }
        }

        throw new IllegalArgumentException("Conversion not possible");
    }

}
