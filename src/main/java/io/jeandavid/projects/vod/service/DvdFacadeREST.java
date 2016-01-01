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
package io.jeandavid.projects.vod.service;

import io.jeandavid.projects.vod.entities.Dvd;
import io.jeandavid.projects.vod.entities.DvdOrder;
import io.jeandavid.projects.vod.entities.DvdOrderDvd;
import io.jeandavid.projects.vod.entities.Person;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jd
 */
@Stateless
@Path("dvd")
public class DvdFacadeREST extends AbstractFacade<Dvd> {

  @EJB
  private DvdOrderFacadeREST dvdOrderSessioBean;
  
  @PersistenceContext(unitName = "io.jeandavid.projects_vod_war_1.0-SNAPSHOTPU")
  private EntityManager em;

  public DvdFacadeREST() {
    super(Dvd.class);
  }

  @POST
  @Override
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Dvd create(Dvd entity) {
    return super.create(entity);
  }

  @PUT
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Dvd edit(@PathParam("id") Long id, Dvd entity) {
    return super.edit(entity);
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") Long id) {
    super.remove(super.find(id));
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Dvd find(@PathParam("id") Long id) {
    return super.find(id);
  }

  @POST
  @Path("{id}/arrival")
  @Consumes(MediaType.APPLICATION_JSON) 
  public void addArrival(@PathParam("id") Long id, Map<String, Object> requestBody) throws NamingException {
    Dvd dvd = super.find(id);
    dvd.setQuantity(dvd.getQuantity() + (int) requestBody.get("quantity"));
    Session session = em.unwrap(Session.class);
    Set<Long> ids = new TreeSet<>();
    for(DvdOrderDvd dvdOrderDvd : dvd.getDvdOrderDvds()) {
      ids.add(dvdOrderDvd.getDvdOrder().getId());
    }
    Criteria crit = session.createCriteria(DvdOrder.class);
    List<DvdOrder> dvdOrders = crit.
      add(Restrictions.in("id", ids.toArray())).
      add(Restrictions.like("internalState", DvdOrder.PENDING)).
      addOrder(Order.desc("created")).
      list();
    for(DvdOrder dvdOrder : dvdOrders) {
      dvdOrderSessioBean.doThePackaging(dvdOrder);
    }
  }
  
  @GET
  @Path("{id}/author")
  @Produces(MediaType.APPLICATION_JSON)
  public Set<Person> getAuthors(@PathParam("id") Long id) {
    Dvd dvd = super.find(id);
    return new HashSet<Person>(dvd.getAuthors());
  }

  @GET
  @Path("{id}/director")
  @Produces(MediaType.APPLICATION_JSON)
  public Set<Person> getDirectors(@PathParam("id") Long id) {
    Dvd dvd = super.find(id);
    return new HashSet<Person>(dvd.getDirectors());
  }
  
  @GET
  @Override
  @Produces(MediaType.APPLICATION_JSON)
  public List<Dvd> findAll() {
    return super.findAll();
  }

  @GET
  @Path("{from}/{to}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Dvd> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
    return super.findRange(new int[]{from, to});
  }

  @GET
  @Path("count")
  @Produces(MediaType.TEXT_PLAIN)
  public String countREST() {
    return String.valueOf(super.count());
  }
  
  @Override
  protected EntityManager getEntityManager() {
    return em;
  }
  
}
