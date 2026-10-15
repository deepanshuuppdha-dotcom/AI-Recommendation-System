package com.recsys.strategy;

import com.recsys.model.StrategyType;

public final class StrategyFactory {
    private StrategyFactory() {}

    public static RecommendationStrategy create(StrategyType type) {
        return switch (type) {
            case CONTENT_BASED -> new ContentBasedStrategy();
            case BEHAVIOR_BASED -> new BehaviorBasedStrategy();
            case HYBRID -> new HybridStrategy();
        };
    }
}
