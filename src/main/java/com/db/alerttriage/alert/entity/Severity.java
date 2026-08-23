package com.db.alerttriage.alert.entity;

import java.math.BigDecimal;

public enum Severity {

    NONE,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static Severity fromCvss(BigDecimal score) {

        if (score.compareTo(new BigDecimal("9.0")) >= 0) {
            return CRITICAL;
        }

        if (score.compareTo(new BigDecimal("7.0")) >= 0) {
            return HIGH;
        }

        if (score.compareTo(new BigDecimal("4.0")) >= 0) {
            return MEDIUM;
        }

        if (score.compareTo(BigDecimal.ZERO) > 0) {
            return LOW;
        }

        return NONE;
    }
}