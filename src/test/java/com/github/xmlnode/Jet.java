package com.github.xmlnode;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigInteger;
import java.util.List;

public class Jet {
    private String make;
    private String model;
    @JacksonXmlProperty(isAttribute = true)
    private BigInteger speed;
    @JacksonXmlElementWrapper(localName = "owners")
    @JsonProperty(value = "owner")
    private List<Owner> owners;

    public Jet(String make, String model, BigInteger speed, List<Owner> owners) {
        this.make = make;
        this.model = model;
        this.speed = speed;
        this.owners = owners;
    }

    public String getMake() {
        return this.make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigInteger getSpeed() {
        return this.speed;
    }

    public void setSpeed(BigInteger speed) {
        this.speed = speed;
    }

    public List<Owner> getOwners() {
        return this.owners;
    }

    public void setOwners(List<Owner> owners) {
        this.owners = owners;
    }
}
