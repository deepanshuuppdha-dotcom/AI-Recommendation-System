package com.recsys.strategy;

import com.recsys.model.AlgorithmSettings;
import com.recsys.model.Item;

/** Strategy pattern: every algorithm scores an item between 0 and 1. */
public interface RecommendationStrategy {

    double score(Item item, RecommendationContext ctx, AlgorithmSettings settings);

    /** Default explanation shown under each recommendation. */
    default String explain(Item item, RecommendationContext ctx) {
        if (ctx.preferenceMatch(item) >= 0.6) return "Matches your interest in " + item.getCategory();
        if (ctx.behaviorMatch(item) >= 0.6) return "Because you often engage with " + item.getCategory();
        return "Popular with other users";
    }
}
