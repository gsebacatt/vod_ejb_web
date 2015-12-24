/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.jeandavid.projects.vod.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;

/**
 *
 * @author jd
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="person_type", discriminatorType=DiscriminatorType.STRING )
public abstract class Person implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  
  private String firstName;
  private String lastName;

  @ManyToMany
  @JsonIgnore
  private Set<Dvd> dvds = new HashSet<Dvd>();

  public Set<Dvd> getDvds() {
    return dvds;
  }

  public void addDvd(Dvd dvd) {
    if(!getDvds().contains(dvd)) {
      getDvds().add(dvd);
    }
    if(!dvd.getPersons().contains(this)) {
      dvd.getPersons().add(this);
    }
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
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(Object object);

  @Override
  public String toString() {
    return "io.jeandavid.projects.vod.entities.Person[ id=" + id + " ]";
  }
  
}
