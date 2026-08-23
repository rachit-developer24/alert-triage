package com.db.alerttriage.alert.dto;

import com.db.alerttriage.alert.entity.Alert;

public record CreateAlertResult(Alert alert , boolean created) {
}
