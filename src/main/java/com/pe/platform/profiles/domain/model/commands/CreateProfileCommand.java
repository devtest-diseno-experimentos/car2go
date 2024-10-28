package com.pe.platform.profiles.domain.model.commands;

public record CreateProfileCommand(String firstName,
                                   String lastName,
                                   String email,
                                   String dni,
                                   String address,
                                   String image,
                                   String phoneNumber,
                                   String role,
                                   String password) {

    public CreateProfileCommand {
        validateNonEmpty(firstName, "First name");
        validateNonEmpty(lastName, "Last name");
        validateNonEmpty(email, "Email");
        validateNonEmpty(dni, "DNI");
        validateNonEmpty(address, "Address");
        validateNonEmpty(image, "Image");
        validateNonEmpty(phoneNumber, "Phone number");
        validateNonEmpty(role, "Role");
        validateNonEmpty(password, "Password");
    }

    private static void validateNonEmpty(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank");
        }
    }
}
