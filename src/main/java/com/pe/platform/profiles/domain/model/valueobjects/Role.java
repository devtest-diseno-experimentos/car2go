package com.pe.platform.profiles.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Role {

    public static final String SELLER = "SELLER";
    public static final String BUYER = "BUYER";

    @Column(name = "role", nullable = false)
    private String value;

    // Constructor privado para evitar instancias externas
    private Role() {}

    // Constructor validado
    public Role(String value) {
        if (!SELLER.equals(value) && !BUYER.equals(value)) {
            throw new IllegalArgumentException("Invalid role: " + value);
        }
        this.value = value;
    }

    // Método de fábrica para SELLER
    public static Role seller() {
        return new Role(SELLER);
    }

    // Método de fábrica para BUYER
    public static Role buyer() {
        return new Role(BUYER);
    }

    public String getValue() {
        return value;
    }

    // Comparación y hashcode para manejo como Value Object
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return value.equals(role.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
