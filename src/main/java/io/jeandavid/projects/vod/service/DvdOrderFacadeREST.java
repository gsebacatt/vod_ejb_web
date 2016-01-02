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
import io.jeandavid.projects.vod.entities.DvdProvider;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.Session.LockRequest;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author jd
 */
@Stateless
@Path("dvd_order")
public class DvdOrderFacadeREST extends AbstractFacade<DvdOrder> {

  @PersistenceContext(unitName = "io.jeandavid.projects_vod_war_1.0-SNAPSHOTPU")
  private EntityManager em;

  private SessionFactory sessionFactory = null;
  
  public SessionFactory getSessionFactory() {
    if(sessionFactory == null) {
      sessionFactory = em.getEntityManagerFactory().unwrap(SessionFactory.class);
    }
    return sessionFactory;
  }
  
  public DvdOrderFacadeREST() {
    super(DvdOrder.class);
  }

  @POST
  @Override
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public DvdOrder create(DvdOrder entity) {   
    super.create(entity);
    return entity;
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public DvdOrder edit(@PathParam("id") Long id, DvdOrder entity) {
    return super.edit(entity);
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") Long id) {
    super.remove(super.find(id));
  }

  @GET
  @Path("{id}/dvd_order_dvd")
  @Produces(MediaType.APPLICATION_JSON)
  public Set<DvdOrderDvd> getDvds(@PathParam("id") Long id) {
    DvdOrder order = super.find(id);
    return new HashSet<>(order.getDvdOrderDvds());
  }  
  
  @POST
  @Path("{id}/dvd")
  @Consumes(MediaType.APPLICATION_JSON)
  public void addDvd(@PathParam("id") Long id, Dvd dvd) {
    DvdOrder order = super.find(id);
    Session session = em.unwrap(Session.class);
    int quantity = dvd.getQuantity();
    session.refresh(dvd);
    order.addDvd(dvd, quantity, session);
  }    

  @DELETE
  @Path("{id}/dvd/{dvdId}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void removeDvd(@PathParam("id") Long id, @PathParam("dvdId") Long dvdId, Map<String, Object> requestBody) {
    Dvd dvd = em.find(Dvd.class, dvdId);
    DvdOrder dvdOrder = em.find(DvdOrder.class, id);
    if(dvdOrder.getInternalState() < DvdOrder.PAID)
      dvdOrder.removeDvd(dvd, (Integer) requestBody.get("quantity"), em.unwrap(Session.class));
  }
  
  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public DvdOrder find(@PathParam("id") Long id) {
    return super.find(id);
  }

  @GET
  @Path("{id}/sub_dvd_order")
  @Produces(MediaType.APPLICATION_JSON)
  public Set<DvdOrder> getSubDvdOrders(@PathParam("id") Long id) {
    DvdOrder dvdOrder = super.find(id);
    return new HashSet<>(dvdOrder.getSubDvdOrders());
  }
  
  @GET
  @Override
  @Produces(MediaType.APPLICATION_JSON)
  public List<DvdOrder> findAll() {
    return super.findAll();
  }

  @GET
  @Path("{from}/{to}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<DvdOrder> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
    return super.findRange(new int[]{from, to});
  }

  @GET
  @Path("count")
  @Produces(MediaType.TEXT_PLAIN)
  public String countREST() {
    return String.valueOf(super.count());
  }
  
  @POST
  @Path("{id}/payment")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public DvdOrder pay(@PathParam("id") Long id, Map<String, Object> requestBody) {
    DvdOrder order = super.find(id);
    if(order.getInternalState() < DvdOrder.PAID) {  
      Session session = this.getSessionFactory().openSession();
      Transaction tr = session.getTransaction();
      tr.begin();
      order.pay();
      session.saveOrUpdate(session.merge(order));
      session.flush();
      tr.commit();
      session.close();    
      this.transformIntoSubOrders(order);   
    }
    return order;
  }
  
  @GET
  @Path("{id}/payment")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> pay(@PathParam("id") Long id) {
    DvdOrder order = super.find(id);
    return new HashMap<>();
  }  
 
  @POST
  @Path("{id}/shipment")
  public void ship(@PathParam("id") Long id) {
    DvdOrder dvdOrder = super.find(id);
    if(dvdOrder.getInternalState() == DvdOrder.SHIPPED) {
      return;    
    }
    boolean ship = true;
    for(DvdOrder subOrder : dvdOrder.getSubDvdOrders()) {
      if(subOrder.getInternalState() != DvdOrder.PACKAGED) {
        ship = false;
      }
    }
    if(ship) {
      dvdOrder.switchInternalState(DvdOrder.SHIPPED);
    }
    super.edit(dvdOrder);
  }  
  
  @Override
  protected EntityManager getEntityManager() {
    return em;
  }
 
  @Asynchronous
  public void transformIntoSubOrders(DvdOrder dvdOrder) {
    Session rootSession = em.unwrap(Session.class);
    rootSession.refresh(dvdOrder);
    HashMap <DvdProvider, List<Dvd>> split = dvdOrder.sortByDvdProvider();
    for(Entry<DvdProvider, List<Dvd>> entry : split.entrySet()) { 
      Session session = this.getSessionFactory().openSession();
      Transaction tr = session.beginTransaction();      
      DvdOrder subOrder = new DvdOrder();
      subOrder.switchInternalState(DvdOrder.PENDING);
      session.persist(subOrder);
      subOrder.setDvdProvider(entry.getKey());
      dvdOrder.addSubOrder(subOrder);
      for(Dvd dvd : entry.getValue()) {
        for(DvdOrderDvd dvdOrderDvd : new HashSet<>(dvdOrder.getDvdOrderDvds())) {
          if(dvdOrderDvd.getDvd().equals(dvd)) {
            subOrder.addDvdOrderDvd(dvdOrderDvd);
            session.saveOrUpdate(session.merge(dvdOrderDvd));
            dvdOrder.getDvdOrderDvds().remove(dvdOrderDvd);
            rootSession.save(dvdOrder);
          }
        }
      }
      subOrder.computePrice();
      session.persist(subOrder);
      session.flush();
      tr.commit();      
      session.close();
      doThePackaging(subOrder);
    }
  }
  
  @Asynchronous
  public void doThePackaging(DvdOrder dvdOrder) {
    if(dvdOrder.getInternalState() > DvdOrder.PENDING || dvdOrder.getInternalState() < DvdOrder.PAID)
      return;
    boolean pending = false;
    Session session = this.getSessionFactory().openSession();
    session.refresh(dvdOrder);
    dvdOrder.switchInternalState(DvdOrder.PACKAGED);
    Transaction tr = session.beginTransaction();
    TreeSet<DvdOrderDvd> sortedDvdOrderDvds = dvdOrder.getSortedDvdOrderDvds();
    for(DvdOrderDvd dvdOrderDvd : sortedDvdOrderDvds) {
      Dvd dvd = (Dvd) session.load(Dvd.class, dvdOrderDvd.getDvd().getId());      
      LockRequest lockRequest = session.buildLockRequest(LockOptions.UPGRADE);
      lockRequest.lock(dvd);
      Integer occurenciesNumber = dvdOrderDvd.getQuantity();
      if(dvd.getQuantity() >= occurenciesNumber) {
        dvd.setQuantity(dvd.getQuantity() - occurenciesNumber);
        session.saveOrUpdate(session.merge(dvd));
      } else {  
        pending = true;
        break;
      }
    }
    if(!pending) {
      session.saveOrUpdate(dvdOrder);
      session.flush();
      tr.commit();      
    } 
    session.close();
  }
}
