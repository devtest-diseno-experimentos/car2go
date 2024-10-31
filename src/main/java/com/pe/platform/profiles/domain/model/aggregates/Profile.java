package com.pe.platform.profiles.domain.model.aggregates;

import com.pe.platform.iam.domain.model.entities.Role;
import com.pe.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.pe.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.pe.platform.profiles.domain.model.valueobjects.PersonName;
import com.pe.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Embedded
    private PersonName name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String image;

    @Column(nullable = false)
    private String dni;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private Long profileId;

    protected Profile() { }

    // comands
    public Profile (CreateProfileCommand command, Long profileId) {
        this.name = new PersonName(command.firstName(), command.lastName());
        this.email = command.email();
        this.image = command.image();
        this.dni = command.dni();
        this.address = command.address();
        this.phone = command.phone();
        this.profileId = profileId;
    }

    public Profile (UpdateProfileCommand command) {
        this.name = new PersonName(command.firstName(), command.lastName());
        this.email = command.email();
        this.image = command.image();
        this.dni = command.dni();
        this.address = command.address();
        this.phone = command.phone();
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public long getProfilerId() {
        return profileId;
    }

    public String getFirstName() {
        return name.getFirstName();
    }

    public  String getLastName() {
        return name.getLastName();
    }

    public void updateName(String firstName, String lastName) {
        this.name = new PersonName(firstName, lastName);
    }

    public int getId() {
        return id;
    }

}
