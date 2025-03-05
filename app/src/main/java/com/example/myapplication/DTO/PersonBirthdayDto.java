package com.example.myapplication.DTO;

import com.example.myapplication.entity.PersonBirthday;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonBirthdayDto {
    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "surname")
    private String surname;

    @JsonProperty(value = "date")
    private LocalDate date;

    @JsonProperty(value = "owner")
    private UserDto owner;

    public PersonBirthdayDto() {
    }

    /**
     * Constructor for mapping model to DTO
     */
    public PersonBirthdayDto(PersonBirthday personBirthday) {
        this.name = personBirthday.name;
        this.surname = personBirthday.surname;
        this.date = personBirthday.date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }
}
