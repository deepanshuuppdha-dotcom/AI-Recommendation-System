package com.recsys.service;

import com.recsys.dao.SettingsDAO;
import com.recsys.exception.InvalidSettingException;
import com.recsys.model.AlgorithmSettings;
import com.recsys.model.StrategyType;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsService {
    private final SettingsDAO settingsDAO = new SettingsDAO();

    public AlgorithmSettings load() throws SQLException {
        return AlgorithmSettings.fromMap(settingsDAO.getAll());
    }

    /** Validates the raw form values, then saves them. */
    public void update(Map<String, String> raw) throws InvalidSettingException, SQLException {
        Map<String, String> clean = new LinkedHashMap<>();

        StrategyType strategy;
        try {
            strategy = StrategyType.valueOf(raw.getOrDefault(AlgorithmSettings.KEY_STRATEGY, ""));
        } catch (IllegalArgumentException e) {
            throw new InvalidSettingException("Choose one of the available strategies.");
        }
        clean.put(AlgorithmSettings.KEY_STRATEGY, strategy.name());

        double sum = 0;
        sum += weight(raw, AlgorithmSettings.KEY_W_PREF, "Preference weight", clean);
        sum += weight(raw, AlgorithmSettings.KEY_W_BEHAVIOR, "Behavior weight", clean);
        sum += weight(raw, AlgorithmSettings.KEY_W_POPULARITY, "Popularity weight", clean);
        if (sum <= 0) {
            throw new InvalidSettingException("At least one weight must be greater than 0.");
        }

        int topN;
        try {
            topN = Integer.parseInt(raw.getOrDefault(AlgorithmSettings.KEY_TOP_N, "").trim());
        } catch (NumberFormatException e) {
            throw new InvalidSettingException("Number of recommendations must be a whole number.");
        }
        if (topN < 1 || topN > 20) {
            throw new InvalidSettingException("Number of recommendations must be between 1 and 20.");
        }
        clean.put(AlgorithmSettings.KEY_TOP_N, String.valueOf(topN));

        settingsDAO.saveAll(clean);
    }

    private double weight(Map<String, String> raw, String key, String label, Map<String, String> clean)
            throws InvalidSettingException {
        double v;
        try {
            v = Double.parseDouble(raw.getOrDefault(key, "").trim());
        } catch (NumberFormatException e) {
            throw new InvalidSettingException(label + " must be a number between 0 and 1.");
        }
        if (v < 0 || v > 1) {
            throw new InvalidSettingException(label + " must be between 0 and 1.");
        }
        clean.put(key, String.valueOf(v));
        return v;
    }
}
