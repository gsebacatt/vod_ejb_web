/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.jeandavid.projects.vod.entities;

import java.io.Serializable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author jd
 */
@Entity
@DiscriminatorValue("AUTHOR")
@XmlRootElement
public class Author extends Person implements Serializable {

  private static final long serialVersionUID = 1L;


  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Author)) {
      return false;
    }
    Author other = (Author) object;
    if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (getId() != null ? getId().hashCode() : 0);
    return hash;
  }
  
  @Override
  public String toString() {
    return "io.jeandavid.projects.vod.entities.Author[ id=" + getId() + " ]";
  }
  
}
