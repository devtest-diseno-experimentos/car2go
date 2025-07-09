package com.pe.platform.interaction.interfaces;

import com.pe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.pe.platform.interaction.domain.model.aggregates.Review;
import com.pe.platform.interaction.domain.services.ReviewCommandService;
import com.pe.platform.interaction.interfaces.rest.dto.CreateReviewRequest;
import com.pe.platform.interaction.interfaces.rest.dto.ReviewDTO;
import com.pe.platform.vehicle.domain.model.aggregates.Vehicle;
import com.pe.platform.vehicle.domain.model.valueobjects.vehicleStatus;
import com.pe.platform.vehicle.domain.services.VehicleCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final VehicleCommandService vehicleCommandService;
    private final ReviewCommandService reviewCommandService;

    public ReviewController(VehicleCommandService vehicleCommandService, ReviewCommandService reviewCommandService) {
        this.vehicleCommandService = vehicleCommandService;
        this.reviewCommandService = reviewCommandService;
    }


    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody CreateReviewRequest reviewRequest) {
        int vehicleId = reviewRequest.getVehicleId();
        String notes = reviewRequest.getNotes();
        vehicleStatus status = vehicleStatus.valueOf(reviewRequest.getStatus());

        Optional<Vehicle> vehicleOptional = vehicleCommandService.findById(vehicleId);
        if (vehicleOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Vehicle not found");
        }

        if (reviewCommandService.getReviewByVehicleId(vehicleId).isPresent()) {
            return ResponseEntity.status(400).body("Review already exists for this vehicle");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String reviewedBy = String.valueOf(userDetails.getId());

        Review newReview = reviewCommandService.createReview(vehicleId, reviewedBy, notes, status);
        ReviewDTO reviewDTO = new ReviewDTO(newReview);

        return ResponseEntity.ok(reviewDTO);
    }



    @GetMapping("/{carId}")
    public ResponseEntity<ReviewDTO> getReviewByCarId(@PathVariable("carId") int carId) {
        Optional<Review> review = reviewCommandService.getReviewByVehicleId(carId);

        if (review.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        ReviewDTO reviewDTO = new ReviewDTO(review.get());
        return ResponseEntity.ok(reviewDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_MECHANIC')")
    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<Review> reviews = reviewCommandService.getAllReviews();

        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviewDTOs);
    }

    @PreAuthorize("hasAuthority('ROLE_MECHANIC')")
    @GetMapping("/me")
    public ResponseEntity<List<ReviewDTO>> getMyReviews() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String userId = String.valueOf(userDetails.getId());

        List<Review> reviews = reviewCommandService.getAllReviews().stream()
                .filter(review -> userId.equals(review.getReviewedBy()))
                .toList();

        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviewDTOs);
    }

    @PatchMapping("/{reviewId}/status")
    public ResponseEntity<?> updateReviewStatus(@PathVariable("reviewId") Long reviewId, @RequestBody String newStatus) {
        vehicleStatus status;
        try {
            newStatus = newStatus.replace("\"", "");
            status = vehicleStatus.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Invalid status value");
        }

        Optional<Review> reviewOptional = reviewCommandService.getReviewById(reviewId);
        if (reviewOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Review not found");
        }

        Review review = reviewOptional.get();
        Vehicle vehicle = review.getVehicle();
        vehicle.setStatus(status);
        vehicleCommandService.saveVehicle(vehicle);

        return ResponseEntity.ok("Review status updated successfully");
    }

    @PatchMapping("/{reviewId}/notes")
    public ResponseEntity<?> updateReviewNotes(@PathVariable("reviewId") Long reviewId, @RequestBody String newNotes) {

        newNotes = newNotes.replace("\"", "");

        if (newNotes == null || newNotes.trim().isEmpty()) {
            return ResponseEntity.status(400).body("Notes cannot be empty");
        }

        Optional<Review> reviewOptional = reviewCommandService.getReviewById(reviewId);
        if (reviewOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Review not found");
        }

        Review review = reviewOptional.get();
        review.setNotes(newNotes);
        reviewCommandService.saveReview(review);

        return ResponseEntity.ok("Review notes updated successfully");
    }

}
