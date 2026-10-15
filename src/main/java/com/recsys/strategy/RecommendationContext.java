package com.recsys.strategy;

import com.recsys.model.Item;

import java.util.List;
import java.util.Map;

/** Everything a strategy needs about one user, with every signal normalised to the 0..1 range. */
public class RecommendationContext {
    private final Map<String, Double> preferences;
    private final Map<String, Double> behavior;
    private final double maxPreference;
    private final double maxBehavior;
    private final double maxPopularity;

    public RecommendationContext(Map<String, Double> preferences, Map<String, Double> behavior, List<Item> items) {
        this.preferences = preferences;
        this.behavior = behavior;
        this.maxPreference = preferences.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        this.maxBehavior = behavior.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        this.maxPopularity = items.stream().mapToInt(Item::getPopularity).max().orElse(0);
    }

    public double preferenceMatch(Item item) {
        return maxPreference == 0 ? 0 : preferences.getOrDefault(item.getCategory(), 0.0) / maxPreference;
    }

    public double behaviorMatch(Item item) {
        return maxBehavior == 0 ? 0 : behavior.getOrDefault(item.getCategory(), 0.0) / maxBehavior;
    }

    public double popularity(Item item) {
        return maxPopularity == 0 ? 0 : item.getPopularity() / maxPopularity;
    }
}
