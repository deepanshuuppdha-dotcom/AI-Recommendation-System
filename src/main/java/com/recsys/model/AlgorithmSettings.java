package com.recsys.model;

import java.util.LinkedHashMap;
import java.util.Map;

/** Admin-configurable algorithm parameters, loaded from the system_settings table. */
public class AlgorithmSettings {
    public static final String KEY_W_PREF = "weight_preference";
    public static final String KEY_W_BEHAVIOR = "weight_behavior";
    public static final String KEY_W_POPULARITY = "weight_popularity";
    public static final String KEY_TOP_N = "top_n";
    public static final String KEY_STRATEGY = "strategy";

    private double weightPreference = 0.5;
    private double weightBehavior = 0.3;
    private double weightPopularity = 0.2;
    private int topN = 6;
    private StrategyType strategy = StrategyType.HYBRID;

    public static AlgorithmSettings fromMap(Map<String, String> m) {
        AlgorithmSettings s = new AlgorithmSettings();
        s.weightPreference = parse(m.get(KEY_W_PREF), s.weightPreference);
        s.weightBehavior = parse(m.get(KEY_W_BEHAVIOR), s.weightBehavior);
        s.weightPopularity = parse(m.get(KEY_W_POPULARITY), s.weightPopularity);
        s.topN = (int) parse(m.get(KEY_TOP_N), s.topN);
        try {
            s.strategy = StrategyType.valueOf(m.getOrDefault(KEY_STRATEGY, "HYBRID"));
        } catch (IllegalArgumentException e) {
            s.strategy = StrategyType.HYBRID;
        }
        return s;
    }

    public Map<String, String> toMap() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(KEY_STRATEGY, strategy.name());
        m.put(KEY_W_PREF, String.valueOf(weightPreference));
        m.put(KEY_W_BEHAVIOR, String.valueOf(weightBehavior));
        m.put(KEY_W_POPULARITY, String.valueOf(weightPopularity));
        m.put(KEY_TOP_N, String.valueOf(topN));
        return m;
    }

    private static double parse(String v, double fallback) {
        try { return v == null ? fallback : Double.parseDouble(v); }
        catch (NumberFormatException e) { return fallback; }
    }

    public double getWeightPreference() { return weightPreference; }
    public double getWeightBehavior() { return weightBehavior; }
    public double getWeightPopularity() { return weightPopularity; }
    public int getTopN() { return topN; }
    public StrategyType getStrategy() { return strategy; }
}
