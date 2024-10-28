package com.pe.platform.profiles.domain.model.aggregates;

import com.pe.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.pe.platform.profiles.domain.model.valueobjects.PersonName;
import com.pe.platform.profiles.domain.model.valueobjects.Role;
import com.pe.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

@Entity
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

    @Embedded
    private PersonName name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String dni;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String image;

    @Column(nullable = false)
    private String phoneNumber;

    @Embedded
    private Role role;

    @Column
    private String password;

    protected Profile() {
    }

    public Profile(String firstName, String lastName, String email, String dni, String address, String image, String phoneNumber, String role, String password) {
        this.name = new PersonName(firstName, lastName);
        this.email = email;
        this.dni = dni;
        this.address = address;
        this.image = image;
        this.phoneNumber = phoneNumber;
        this.role = new Role(role);
        this.password = password;
    }

    public Profile(CreateProfileCommand command){
        this.name = new PersonName(command.firstName(), command.lastName());
        this.email = command.email();
        this.dni = command.dni();
        this.address = command.address();
        this.image = command.image();
        this.phoneNumber = command.phoneNumber();
        this.role = new Role(command.role());
        this.password = command.password();
    }

}
