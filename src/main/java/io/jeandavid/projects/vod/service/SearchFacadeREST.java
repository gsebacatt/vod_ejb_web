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

import io.jeandavid.projects.vod.entities.Searchable;
import io.jeandavid.projects.vod.util.StringUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jd
 */
@Stateless
@Path("search")
public class SearchFacadeREST {
  
  @PersistenceContext(unitName = "io.jeandavid.projects_vod_war_1.0-SNAPSHOTPU")
  private EntityManager em;
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Set search(Map<String, Object> requestBody) throws ClassNotFoundException, Exception {
    List result = null;
    if(requestBody.get("resource") != null) {
      result = searchElement(requestBody);
    }    
    return new HashSet<>(result);
  }
  
  private List<Searchable> searchElement(Map<String, Object> element) throws Exception {
    String resourceName = StringUtils.camelize(element.get("resource").toString());
    Class subject = Class.forName("io.jeandavid.projects.vod.entities." + resourceName);
    if(element.get("id") != null) {
      ArrayList<Searchable> result = new ArrayList<>();
      result.add((Searchable) em.find(subject, new Long((Integer)element.get("id"))));
      return result;
    } else {
      Session session = em.unwrap(Session.class);
      Criteria crit = session.createCriteria(subject);
      crit = (Criteria) subject.getMethod("search", Criteria.class, Map.class)
        .invoke(null, crit, element.get("fields"));
      if(element.get("parentResource") != null) {
        Long[] ids = extractResourcesIdsFromParents(
          (List<Searchable>) searchElement((Map<String, Object>) element.get("parentResource")), resourceName);
        if(ids.length == 0) {
          return new ArrayList();
        } else {
          crit.add(Restrictions.in("id", ids));
        }
      }
      return crit.list();
    }
  }
  
  private Long[] extractResourcesIdsFromParents(List<Searchable> parents, String resourceName) throws Exception {
    Set<Searchable> resources;
    String resourceToCollect = StringUtils.camelize(resourceName);
    TreeSet<Long> ids = new TreeSet<Long>();
    for(Searchable parent : parents) {
      Method method = parent.getClass().getMethod("get" + resourceToCollect + "s");
      resources = (Set<Searchable>) method.invoke(parent);
      for(Searchable resource : resources) {
        ids.add(resource.getId());
      }
    }
    return ids.toArray(new Long[ids.size()]);
  }
  
}
