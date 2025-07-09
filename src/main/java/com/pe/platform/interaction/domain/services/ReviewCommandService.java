package com.pe.platform.interaction.domain.services;

import com.pe.platform.interaction.domain.model.aggregates.Review;
import com.pe.platform.vehicle.domain.model.valueobjects.vehicleStatus;

import java.util.List;
import java.util.Optional;

public interface ReviewCommandService {

    List<Review> getAllReviews();

    Optional<Review> getReviewByVehicleId(int vehicleId);

    Optional<Review> getReviewById(Long reviewId);

    Review createReview(int vehicleId, String reviewedBy, String notes, vehicleStatus status);

    void saveReview(Review review);
}
