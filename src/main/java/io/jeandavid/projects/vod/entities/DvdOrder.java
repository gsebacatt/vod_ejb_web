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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.hibernate.LockOptions;
import org.hibernate.Session;

/**
 *
 * @author jd
 */
@Entity
public class DvdOrder implements Serializable {

  
  @OneToMany(mappedBy = "dvdOrder")
  @JsonIgnore
  private Set<DvdOrderDvd> dvdOrderDvds = new HashSet<>();

  public  Set<DvdOrderDvd> getDvdOrderDvds() {
    return dvdOrderDvds;
  }

  @ManyToOne
  private DvdOrder parentDvdOrder;

  @ManyToOne
  private DvdProvider dvdProvider;
  
  private Date created;
  private Date updated;

  public Date getCreated() {
    return created;
  }

  public Date getUpdated() {
    return updated;
  }

  @PrePersist
  protected void onCreate() {
    created = new Date();
  }

  @PreUpdate
  protected void onUpdate() {
    updated = new Date();
  }
  
  public static final int CREATED = 0;
  public static final int PAID = 1;
  public static final int PENDING = 2;
  public static final int PACKAGED =  3;
  public static final int SHIPPED = 4;
  
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

  public void addSubOrder(DvdOrder subOrder) {
    if(!getSubDvdOrders().contains(subOrder)) {
      getSubDvdOrders().add(subOrder);
    }
    if(subOrder.getParentDvdOrder() != this) {
      subOrder.setParentDvdOrder(this);
    }    
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
  
  public void addDvd(Dvd dvd, int quantity, Session session) {
    DvdOrderDvd temp = new DvdOrderDvd();
    temp.setQuantity(quantity);
    session.persist(temp);        
    dvd.addDvdOrderDvd(temp);
    this.addDvdOrderDvd(temp);
    session.saveOrUpdate(session.merge(temp));
    session.saveOrUpdate(session.merge(dvd));   
    float price = temp.computePrice();
    temp.setPrice(price);
    session.saveOrUpdate(session.merge(temp));
    this.price = new Float(this.price + price);    
    session.saveOrUpdate(session.merge(this));
  }
  
  public void addDvdOrderDvd(DvdOrderDvd dvdOrderDvd) {
    if(!dvdOrderDvds.contains(dvdOrderDvd)) {
      this.dvdOrderDvds.add(dvdOrderDvd);
    }
    if(dvdOrderDvd.getDvdOrder() != this) {
      dvdOrderDvd.setDvdOrder(this);  
    }
  }  
  
  public void removeDvd(Dvd dvd, int quantity, Session session) {
    DvdOrderDvd temp = null;
    for(DvdOrderDvd dvdOrderDvd : this.dvdOrderDvds) {
      if(dvdOrderDvd.getDvd().equals(dvd)) {
        temp = dvdOrderDvd;
      }
    }
    if(temp == null)
      return;
    
    if(temp.getQuantity() <= quantity) {
      session.delete(temp);
    } else {
      temp.setQuantity(temp.getQuantity() - quantity);
      this.price -= (temp.getPrice() - temp.computePrice());
      temp.setPrice(temp.computePrice());
      session.merge(temp);
    }
  }
  

  private Float price = new Float(0);

  public Float getPrice() {
    return price;
  }
  
  public String getExternalState() {
    switch(internalState) {
      case CREATED : return "created";
      case PAID : return "paid";
      case PENDING : return "pending";
      case PACKAGED : return "packaging";
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
  
  public HashMap<Dvd, Integer> countDvdsOccurencies() {
    HashMap result = new HashMap<Dvd, Integer>() {
      @Override
      public Integer get(Object key) {
        if(!containsKey(key))
          return 0;
        return super.get(key);
      }
    };
    for(DvdOrderDvd dvdOrderDvd : dvdOrderDvds) {
      result.put(dvdOrderDvd.getDvd(), (Integer) result.get(dvdOrderDvd.getDvd()) + dvdOrderDvd.getQuantity());
    }
    return result;
  }
  
  public HashMap<DvdProvider, List<Dvd>> sortByDvdProvider() {
    HashMap<DvdProvider, List<Dvd>> result = new HashMap<DvdProvider, List<Dvd>>() {
      @Override
      public List get(Object key) {
        if(!containsKey(key))
          return new ArrayList<>();
        return super.get(key);
      }    
    };
    for(DvdOrderDvd dvdOrderDvd : dvdOrderDvds) {
      List<Dvd> dvds = result.get(dvdOrderDvd.getDvd().getDvdProvider());
      dvds.add(dvdOrderDvd.getDvd());
      result.put(dvdOrderDvd.getDvd().getDvdProvider(), dvds);
    }
    return result;
  }
  
  public void computePrice() {
    Float result = new Float(0);
    for(DvdOrderDvd dvdOrderDvd : dvdOrderDvds) {
      result += dvdOrderDvd.computePrice();
    }
    this.price = result;
  }
  
  @JsonIgnore
  public TreeSet<DvdOrderDvd> getSortedDvdOrderDvds() {
    TreeSet<DvdOrderDvd> result = new TreeSet<>();
    for(DvdOrderDvd dvdOrderDvd : this.getDvdOrderDvds()) {
      result.add(dvdOrderDvd);
    }
    return result;
  }  
  
  public void switchInternalState(int state) {
    this.internalState = state;
  }

  public void updatePrice(Float price) {
    this.price = price;
  }
}
