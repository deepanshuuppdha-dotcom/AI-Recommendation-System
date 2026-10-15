package com.recsys.strategy;

import com.recsys.model.AlgorithmSettings;
import com.recsys.model.Item;

/**
 * Weighted blend: (w1 * preference + w2 * behavior + w3 * popularity) / (w1 + w2 + w3).
 * The weights come from the admin's system settings.
 */
public class HybridStrategy implements RecommendationStrategy {
    @Override
    public double score(Item item, RecommendationContext ctx, AlgorithmSettings s) {
        double total = s.getWeightPreference() + s.getWeightBehavior() + s.getWeightPopularity();
        if (total <= 0) return 0;
        return (s.getWeightPreference() * ctx.preferenceMatch(item)
              + s.getWeightBehavior() * ctx.behaviorMatch(item)
              + s.getWeightPopularity() * ctx.popularity(item)) / total;
    }
}
