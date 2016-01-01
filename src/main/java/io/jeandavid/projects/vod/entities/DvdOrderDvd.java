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

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author jd
 */
@Entity
public class DvdOrderDvd implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  private Dvd dvd;
  
  @ManyToOne
  @JsonBackReference("dvdOrderDvds")
  private DvdOrder dvdOrder;
  
  private Integer quantity;
  private Float price;

  public Float getPrice() {
    return price;
  }

  public void setPrice(Float price) {
    this.price = price;
  }
  
  public Dvd getDvd() {
    return dvd;
  }

  public void setDvd(Dvd dvd) {
    this.dvd = dvd;
  }

  public DvdOrder getDvdOrder() {
    return dvdOrder;
  }

  public void setDvdOrder(DvdOrder dvdOrder) {
    this.dvdOrder = dvdOrder;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
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
    if (!(object instanceof DvdOrderDvd)) {
      return false;
    }
    DvdOrderDvd other = (DvdOrderDvd) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.jeandavid.projects.vod.entities.DvdOrderDvd[ id=" + id + " ]";
  }
  
  public Float computePrice() {
    return dvd.getPrice()*quantity;
  }

//  @Override
//  public int compareTo(Object o) {
//    if(o instanceof DvdOrderDvd) {
//      return -1;
//    } else {
//      Dvd toCompare = ((DvdOrderDvd) o).getDvd();
//      if(toCompare.getId() > this.getDvd().getId()) {
//        return -1;
//      } else if(toCompare.getId() < this.getDvd().getId()) {
//        return 1;
//      }
//    }
//    return 0;
//  }
  
}
