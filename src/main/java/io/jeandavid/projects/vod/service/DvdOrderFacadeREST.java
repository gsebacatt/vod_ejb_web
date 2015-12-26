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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
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

/**
 *
 * @author jd
 */
@Stateless
@Path("dvd_order")
public class DvdOrderFacadeREST extends AbstractFacade<DvdOrder> {

  @PersistenceContext(unitName = "io.jeandavid.projects_vod_war_1.0-SNAPSHOTPU")
  private EntityManager em;

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
  @Path("{id}/dvd")
  @Produces(MediaType.APPLICATION_JSON)
  public Set<Dvd> getDvds(@PathParam("id") Long id) {
    DvdOrder order = super.find(id);
    return new HashSet<>(order.getDvds());
  }  
  
  @POST
  @Path("{id}/dvd")
  @Consumes(MediaType.APPLICATION_JSON)
  public void addDvd(@PathParam("id") Long id, Dvd dvd) {
    DvdOrder order = super.find(id);
    order.addDvd(dvd.reload(em));
  }    

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public DvdOrder find(@PathParam("id") Long id) {
    return super.find(id);
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
    order.pay();
    super.edit(order);
    return order;
  }
  
  @GET
  @Path("{id}/payment")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> pay(@PathParam("id") Long id) {
    DvdOrder order = super.find(id);
    return new HashMap<>();
  }  
  
  @Override
  protected EntityManager getEntityManager() {
    return em;
  }
  
}
