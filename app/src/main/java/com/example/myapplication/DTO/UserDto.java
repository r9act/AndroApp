package com.example.myapplication.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    @JsonProperty(value = "id")
    private Long id;
    @JsonProperty(value = "androidId")
    private Long androidId;
    @JsonProperty(value = "name")
    private String firstName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAndroidId() {
        return androidId;
    }

    public void setAndroidId(Long androidId) {
        this.androidId = androidId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
