package com.pe.platform.profiles.interfaces.rest.resources;

public record ProfileResource(
        Long id,
        String firstName,
        String lastName,
        String email,
        String image,
        String dni,
        String address,
        String phone,
        Long profileId) { }
