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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jd
 */
@Entity
@XmlRootElement
public class DvdOrder implements Serializable {

  @ManyToOne
  private DvdOrder parentDvdOrder;

  @ManyToOne
  private DvdProvider dvdProvider;
  
  public static final int CREATED = 0;
  public static final int PAID = 1;
  public static final int PENDING = 2;
  public static final int PACKAGED =  3;
  public static final int RECEIVED = 4;
  public static final int SHIPPED = 5;
  
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  
  @OneToMany(mappedBy = "parentDvdOrder")
  @JsonIgnore
  private Set<DvdOrder> subDvdOrders = new HashSet<>();

  public Set<DvdOrder> getSubDvdOrders() {
    return subDvdOrders;
  }

  public DvdOrder getParentDvdOrder() {
    return parentDvdOrder;
  }

  public void setParentDvdOrder(DvdOrder parentDvdOrder) {
    this.parentDvdOrder = parentDvdOrder;
  }
  
  public DvdProvider getDvdProvider() {
    return dvdProvider;
  }

  public void setDvdProvider(DvdProvider dvdProvider) {
    this.dvdProvider = dvdProvider;
  }

  public List<Dvd> getDvds() {
    return dvds;
  }
  
  public void addDvd(Dvd dvd) {
    if(!getDvds().contains(dvd)) {
      for(int i = 0; i < dvd.getQuantity(); i++) 
        getDvds().add(dvd);
    }
    if(!dvd.getDvdOrders().contains(this)) {
      dvd.getDvdOrders().add(this);
    }
  }
  
  @ManyToMany
  @JsonIgnore
  private List<Dvd> dvds = new ArrayList<Dvd>();

  public void setDvds(List<Dvd> dvds) {
    this.dvds = dvds;
  }

  public Float getPrice() {
    Float price = new Float(0);
    for(Dvd dvd : dvds) {
      price += dvd.getPrice();
    }
    return price;
  }
  
  public String getExternalState() {
    switch(internalState) {
      case CREATED : return "created";
      case PAID : return "paid";
      case PENDING : return "pending";
      case RECEIVED : return "received from the provider";
      case PACKAGED : return "packaged";
      case SHIPPED : return "shipped";
    }
    return null;
  }
  
  @JsonIgnore
  private int internalState = CREATED;

  public int getInternalState() {
    return internalState;
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
    if (!(object instanceof DvdOrder)) {
      return false;
    }
    DvdOrder other = (DvdOrder) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.jeandavid.projects.vod.entities.Order[ id=" + id + " ]";
  }
  
  public void pay() {
    this.internalState = PAID;
  }
  
  public HashMap<Long, Integer> countDvdsOccurencies() {
    HashMap result = new HashMap<Long, Integer>() {
      @Override
      public Integer get(Object key) {
          if(!containsKey(key))
              return 0;
          return super.get(key);
      }
    };
    for(Dvd dvd : dvds) {
      result.put(dvd.getId(), (Integer) result.get(dvd.getId()) + 1);
    }
    return null;
  }
  
}
