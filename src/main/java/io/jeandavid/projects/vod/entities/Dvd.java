/*
 * The MIT License
 *
 * Copyright 2015 jd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.jeandavid.projects.vod.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Where;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jd
 */
@Entity
public class Dvd extends Searchable implements Serializable {

  @JsonIgnore
  @OneToMany(mappedBy = "dvd")
  private Set<DvdOrderDvd> dvdOrderDvds = new HashSet<>();

  public Set<DvdOrderDvd> getDvdOrderDvds() {
    return dvdOrderDvds;
  }

  public void addDvdOrderDvd(DvdOrderDvd dvdOrderDvd) {
    if(!dvdOrderDvds.contains(dvdOrderDvd)) {
      this.dvdOrderDvds.add(dvdOrderDvd);
    }
    if(dvdOrderDvd.getDvd() != this) {
      dvdOrderDvd.setDvd(this);  
    }
  }
  
  @ManyToOne
  private DvdProvider dvdProvider;

  public DvdProvider getDvdProvider() {
    return dvdProvider;
  }

  public void setDvdProvider(DvdProvider dvdProvider) {
    this.dvdProvider = dvdProvider;
  }

  private int quantity;

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
  
  @JsonIgnore
  @ManyToMany(mappedBy = "dvds")
  private Set<Person> persons = new HashSet<Person>();

  public void setPersons(Set<Person> persons) {
    this.persons = persons;
  }

  private float price;

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }
  
  public Set<Person> getPersons() {
    return persons;
  }
  
  public void addPerson(Person person) {
    if(!getPersons().contains(person)) {
      getPersons().add(person);
    }
    if(!person.getDvds().contains(this)) {
      person.getDvds().add(this);
    }
  }
  
  @ManyToMany(mappedBy = "dvds")
  @JsonIgnore
  @Where(clause="personType='AUTHOR'")
  private final Set<Person> authors = new HashSet<Person>();
  
  public Set<Person> getAuthors() {
    return authors;
  }
  
  @ManyToMany(mappedBy = "dvds")
  @JsonIgnore
  @Where(clause="personType='DIRECTOR'")
  private final Set<Person> directors = new HashSet<Person>();
  
  public Set<Person> getDirectors() {
    return directors;
  }
  
  
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String title;
  
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  
  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Dvd)) {
      return false;
    }
    Dvd other = (Dvd) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.jeandavid.projects.vod.entities.Dvd[ id=" + id + " ]";
  }

  public static Criteria search(Criteria criteria, Map<String, Object> fields) {
    if(fields.get("title") != null) 
      criteria.add(Restrictions.ilike("title", fields.get("title").toString(), MatchMode.ANYWHERE)); 
    return criteria;
  }
 
  public Dvd reload(EntityManager em) {
    Session session = em.unwrap(Session.class);
    session.beginTransaction();
    Dvd dvd = (Dvd) session.get(Dvd.class, this.id);
    session.getTransaction().commit();
    return dvd;
  }
}
