package org.poo.platform;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CurrencyConverter {
    private Map<String, Map<String, Double>> exchangeGraph = new HashMap<>();

    public void addExchangeRate(String from, String to, double rate) {
        exchangeGraph.putIfAbsent(from, new HashMap<>());
        exchangeGraph.get(from).put(to, rate);

        // Adaugă și rata inversă pentru conversie bidirecțională, dacă este valabilă
        exchangeGraph.putIfAbsent(to, new HashMap<>());
        exchangeGraph.get(to).put(from, 1 / rate);
    }

    public double convert(String from, String to, double amount) {
        // BFS pentru a găsi rata de schimb
        Queue<String> queue = new LinkedList<>();
        Map<String, Double> visited = new HashMap<>();
        queue.add(from);
        visited.put(from, 1.0); // Rata inițială este 1.0

        while (!queue.isEmpty()) {
            String current = queue.poll();
            double currentRate = visited.get(current);

            // Iterează prin vecini
            for (Map.Entry<String, Double> neighbor : exchangeGraph.get(current).entrySet()) {
                if (!visited.containsKey(neighbor.getKey())) {
                    double newRate = currentRate * neighbor.getValue();
                    visited.put(neighbor.getKey(), newRate);

                    if (neighbor.getKey().equals(to)) {
                        return amount / newRate; // Găsit rata finală
                    }

                    queue.add(neighbor.getKey());
                }
            }
        }

        throw new IllegalArgumentException("Conversia nu este posibilă.");
    }

}
