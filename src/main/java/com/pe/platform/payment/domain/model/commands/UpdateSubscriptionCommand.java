package com.pe.platform.payment.domain.model.commands;

import com.pe.platform.payment.domain.model.valueobjects.SubscriptionStatus;

public record UpdateSubscriptionCommand(Long profileId, SubscriptionStatus status) {
}
