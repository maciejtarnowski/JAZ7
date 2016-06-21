package rest.services;

import domain.Person;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/people")
@Stateless
public class PersonResource {
    private final static Integer perPage = 10;

    @PersistenceContext
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getPeople(@QueryParam("page") Integer page) {
        TypedQuery<Person> personQuery = em.createNamedQuery("person.all", Person.class);
        personQuery.setMaxResults(perPage);
        personQuery.setFirstResult((page - 1) * perPage);
        return personQuery.getResultList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Person addPerson(Person person) {
        em.persist(person);
        return person;
    }

    @DELETE
    @Path("/{id}")
    public Response deletePerson(@PathParam("id") Integer id) {
        Person person;
        try {
            person = em.createNamedQuery("person.id", Person.class)
                    .setParameter("personId", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return Response.status(404).build();
        }
        em.remove(person);
        return Response.ok().build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePerson(@PathParam("id") Integer id, Person person) {
        try {
            em.createNamedQuery("person.id", Person.class)
                    .setParameter("personId", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return Response.status(404).build();
        }
        person.setId(id);
        em.merge(person);

        return Response.ok(person).build();
    }
}
