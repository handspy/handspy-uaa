package pt.up.hs.uaa.service.dto;

import pt.up.hs.uaa.domain.User;

public class ContactDTO {

    private Long id;

    private String login;

    private String firstName;

    private String lastName;

    private String organization;

    private String email;

    private String imageUrl;

    private String country;

    public ContactDTO() {
        // Empty constructor needed for Jackson.
    }

    public ContactDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.organization = user.getOrganization();
        this.email = user.getEmail();
        this.imageUrl = user.getImageUrl();
        this.country = user.getCountry();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "ContactDTO{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", organization='" + organization + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", country='" + country + '\'' +
            "}";
    }

}
