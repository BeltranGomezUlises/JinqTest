
import com.mongodb.DB;
import entities.mongo.Mascota;
import entities.mongo.Person;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoConnection;
import org.jinq.jpa.JPAQueryLogger;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Test para jinq jpa eclipselink and MongoDB
 *
 * @author Ulises Beltrán Gómez --- beltrangomezulises@gmail.com
 */
public class MongoTest {

    /**
     * Proveedor de streams jpa
     */
    private static JinqJPAStreamProvider streams;

    /**
     * entity manager.
     */
    private static EntityManager em;

    /**
     * TX - with MongoDB ?!?
     */
    private EntityTransaction tx;

    private String id;

    @BeforeClass
    public static void setUpPU() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("mongo-unit");
        em = factory.createEntityManager();
        
        /**
         * No realiza el mapeo de los atributos y marca un error, intentar revisar con que anotacion o asignacion se puede arreglar
         * o probar con un proveedor distinto coom hibernate
         * 
         */
//        streams = new JinqJPAStreamProvider(factory);
//        streams.setHint(
//                "queryLogger", (JPAQueryLogger) (String query, Map<Integer, Object> positionParameters, Map<String, Object> namedParameters) -> {
//                    System.out.println("queryLogr -> " + query);
//                });
    }

    /**
     * Attention: EclipseLink requires an active transaction although MongoDB
     * itself
     * <b>DOES NOT</b> transaction at all!
     */
    @Before
    public void setUp() {
        // is needed by JPA/EntityManager
        tx = em.getTransaction();
        tx.begin();

        // given ...
        DB db = ((MongoConnection) em.unwrap(javax.resource.cci.Connection.class)).getDB();

        db.dropDatabase();

        Person person = new Person();
        person.setEdad(10);
        person.setFechaRegistro(new Date());
        person.setNombre("ulises");

        List<Mascota> mascotas = Arrays.asList(
                new Mascota(3, "kiki", "ave"),
                new Mascota(1, "lobito", "perro"),
                new Mascota(0, "mika", "gato")
        );

        person.setMascotas(mascotas);

        em.persist(person);
        em.flush();
        id = person.getId();
    }

    /**
     * Uses entity manager primary key lookup.
     */
    @Test
    public void should_find_by_primary_key() {
        // when
        Person person = em.find(Person.class, id);
        //then
        assertPerson(person);
    }

    /**
     * Uses JPQL query (that gets translated to native MongoDB query.
     */
    @Test
    public void should_find_by_items_quantity() {
        // when
        Person order = em
                .createQuery("SELECT p FROM Person p JOIN p.mascotas i WHERE i.edad = 1", Person.class)
                .getSingleResult();

        // then
        assertPerson(order);
    }

    /**
     * Uses native MongoDB query (which consists of the full find command like
     * used in the Mongo shell, not only the query string itself.
     * <p>
     * Note that EclipseLink converts the names of collections field to upper
     * case.
     */
    @Test
    public void should_find_by_primary_with_native_query() {
        // when
        Person person = (Person) em
                .createNativeQuery("db.persons.findOne({_id: \"" + id + "\"})", Person.class)
                .getSingleResult();
        // then
        assertPerson(person);
    }

    /**
     * Native queries with nested paths (in this example "ITEMS.QUANTITY") do
     * not seem to work properly, they raise an error.
     */
    @Test
    public void should_find_by_items_quantity_with_native_query() {
        // when
        Person order = (Person) em
                .createNativeQuery("db.persons.findOne({\"mascotas.edad\": 1})", Person.class)
                .getSingleResult();
        // then
        assertPerson(order);
    }

    /**
     * IMPRIME TODAS LAS PERSONAS   
     */
//    @Test
//    public void jinqFindAll() {
//        streams.streamAll(em, Person.class)
//                .forEach( p -> System.out.println(p));
//    }
//    
//    @Test
//    public void jinqFilter() {
//        streams.streamAll(em, Person.class)
//                .filter(p -> p.getNombre().equals("ulises"))
//                .forEach( p -> System.out.println(p));
//    }
//    
//    @Test
//    public void jinqMap() {
//        streams.streamAll(em, Person.class)
//                .map( p -> p.getMascotas())
//                .forEach( mascotas -> mascotas
//                        .forEach(mascota -> System.out.println(mascota.getNombre()))
//                );
//    }

    @After
    public void tearDown() {
        tx.commit();
    }

    @AfterClass
    public static void closeEntityManager() {
        if (em != null) {
            em.close();
        }
    }

    private static void assertPerson(Person person) {
        assertNotNull(person);
        assertEquals(3, person.getMascotas().size());
    }

}
