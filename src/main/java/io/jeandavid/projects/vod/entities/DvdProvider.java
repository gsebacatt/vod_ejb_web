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
import java.util.Map;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jd
 */
@Entity
public class DvdProvider extends Searchable implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;
  
  @JsonIgnore
  @OneToMany(mappedBy = "dvdProvider")
  private Set<Dvd> dvds = new HashSet<Dvd>();

  @JsonIgnore
  @OneToMany(mappedBy = "dvdProvider")
  private Set<DvdOrder> dvdOrders = new HashSet<DvdOrder>();

  public void addDvd(Dvd dvd) {
    if(!getDvds().contains(dvd)) {
      getDvds().add(dvd);
    }
    if(dvd.getDvdProvider() != null) {
      dvd.setDvdProvider(this);
    }
  }
  
  public void addDvdOrder(DvdOrder dvdOrder) {
    if(!getDvdOrders().contains(dvdOrder)) {
      getDvdOrders().add(dvdOrder);
    }
    if(dvdOrder.getDvdProvider() != null) {
      dvdOrder.setDvdProvider(this);
    }    
  }
  
  public Set<Dvd> getDvds() {
    return dvds;
  }

  public void setDvds(Set<Dvd> dvds) {
    this.dvds = dvds;
  }

  public Set<DvdOrder> getDvdOrders() {
    return dvdOrders;
  }

  public void setDvdOrders(Set<DvdOrder> dvdOrders) {
    this.dvdOrders = dvdOrders;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
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
    if (!(object instanceof DvdProvider)) {
      return false;
    }
    DvdProvider other = (DvdProvider) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.jeandavid.projects.vod.entities.DvdProvider[ id=" + id + " ]";
  }
  
  public static Criteria search(Criteria criteria, Map<String, Object> fields) {
    criteria.add(Restrictions.ilike("name", fields.get("name").toString(), MatchMode.ANYWHERE));
    return criteria;
  }
  
}
