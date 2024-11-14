package com.pe.platform.payment.interfaces.rest.resources;

import com.pe.platform.payment.domain.model.valueobjects.SubscriptionStatus;

public record SubscriptionResource(Integer price, String description, SubscriptionStatus status, Long profileId) {
}
