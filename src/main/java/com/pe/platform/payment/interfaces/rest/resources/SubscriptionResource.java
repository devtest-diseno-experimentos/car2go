package com.pe.platform.payment.interfaces.rest.resources;

public record SubscriptionResource(Integer price, String description, Boolean paid,Long profileId) {
}
