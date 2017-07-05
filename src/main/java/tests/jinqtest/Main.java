package tests.jinqtest;

import entities.Region;
import entities.Sucursal;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jinq.jpa.JPAQueryLogger;
import org.jinq.jpa.JinqJPAStreamProvider;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this tEMplate file, choose Tools | TEMplates
 * and open the tEMplate in the editor.
 */
/**
 *
 * @author Ulises Beltrán Gómez --- beltrangomezulises@gmail.com
 */
public class Main {

    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("postgres-unit");
    private static final EntityManager EM = FACTORY.createEntityManager();

    private static final JinqJPAStreamProvider STREAMS = new JinqJPAStreamProvider(FACTORY);
//        

    static {
        STREAMS.setHint(
                "queryLogger", (JPAQueryLogger) (String query, Map<Integer, Object> positionParameters, Map<String, Object> namedParameters) -> {
                    System.out.println("queryLogr -> " + query);
                });
    }

    public static void main(String[] args) {
                        
    }

    private static void initDB() {

        Region r1 = new Region();
        r1.setNombre("primera");

        Region r2 = new Region();
        r2.setNombre("segunda");

        Region r3 = new Region();
        r3.setNombre("tercera");

        Region r4 = new Region();
        r4.setNombre("cuarta");

        EM.getTransaction().begin();

        EM.persist(r1);
        EM.persist(r2);
        EM.persist(r3);
        EM.persist(r4);

        EM.flush();

        Sucursal s1 = new Sucursal();
        s1.setDireccion("dir 1");
        s1.setRegion(r1);

        Sucursal s2 = new Sucursal();
        s2.setDireccion("dir 2");
        s2.setRegion(r1);

        Sucursal s3 = new Sucursal();
        s3.setDireccion("dir 3");
        s3.setRegion(r2);

        Sucursal s4 = new Sucursal();
        s4.setDireccion("dir 4");
        s4.setRegion(r3);

        EM.persist(s1);
        EM.persist(s2);
        EM.persist(s3);
        EM.persist(s4);

        EM.flush();

        EM.getTransaction().commit();
        EM.close();
    }
    
    private static void consultas(){
        
    }
}
