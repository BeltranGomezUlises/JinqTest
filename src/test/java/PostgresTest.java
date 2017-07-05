
import daos.PermisoJpaController;
import daos.RolJpaController;
import daos.UsuarioJpaController;
import entities.postrgres.Permiso;
import entities.postrgres.Rol;
import entities.postrgres.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.jinq.jpa.JPAQueryLogger;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ulises Beltrán Gómez --- beltrangomezulises@gmail.com
 */
public class PostgresTest {

    private static EntityManagerFactory factory;
    private static EntityManager em;
    private static JinqJPAStreamProvider streams;

    @BeforeClass
    public static void setUpPU() {
        factory = Persistence.createEntityManagerFactory("postgres-unit");
        em = factory.createEntityManager();
        streams = new JinqJPAStreamProvider(factory);
        streams.setHint(
                "queryLogger", (JPAQueryLogger) (String query, Map<Integer, Object> positionParameters, Map<String, Object> namedParameters) -> {
                    System.out.println("queryLogr -> " + query);
                });
    }

    @Before
    public void setUp() {
        try {
            em.getTransaction().begin();

            Query q1 = em.createQuery("DELETE FROM Permiso");
            Query q2 = em.createQuery("DELETE FROM Rol");
            Query q3 = em.createQuery("DELETE FROM Usuario");

            q1.executeUpdate();
            q2.executeUpdate();
            q3.executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //PERMISOS        
            Permiso permiso1 = new Permiso();
            permiso1.setDescripcion("permiso general");
            permiso1.setNombre("alta contrato");

            Permiso permiso2 = new Permiso();
            permiso2.setDescripcion("permiso particular");
            permiso2.setNombre("baja contrato");

            Permiso permiso3 = new Permiso();
            permiso3.setDescripcion("permiso general");
            permiso3.setNombre("modificar contrato");

            //ROLES        
            Rol rolAdministrador = new Rol();
            rolAdministrador.setDescripcion("rol administrativos de contratos");
            rolAdministrador.setNombre("Administrador de contratos");

            Rol rolcapturista = new Rol();
            rolcapturista.setDescripcion("rol de capturista de contratos");
            rolcapturista.setNombre("Capturista de contratos");

            //relaciones roles con permisos - permisos con roles        
            List<Permiso> permisosRolCapturista = new ArrayList<>();
            permisosRolCapturista.add(permiso1);
            permisosRolCapturista.add(permiso2);

            List<Permiso> permisosRolAdministrador = new ArrayList<>();
            permisosRolAdministrador.add(permiso1);
            permisosRolAdministrador.add(permiso3);

            rolAdministrador.setPermisoCollection(permisosRolAdministrador);
            rolcapturista.setPermisoCollection(permisosRolCapturista);

            //USUARIOS        
            Usuario usuario1 = new Usuario();
            usuario1.setNombre("ulises");

            Usuario usuario2 = new Usuario();
            usuario2.setNombre("otro usuario");

            //relaciones usuarios con roles
            usuario1.setRol(rolAdministrador);
            usuario2.setRol(rolcapturista);

            PermisoJpaController permisoController = new PermisoJpaController(factory);
            RolJpaController rolController = new RolJpaController(factory);
            UsuarioJpaController usuarioController = new UsuarioJpaController(factory);

            permisoController.create(permiso1);
            permisoController.create(permiso2);
            permisoController.create(permiso3);

            rolController.create(rolcapturista);
            rolController.create(rolAdministrador);

            usuarioController.create(usuario1);
            usuarioController.create(usuario2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * IMPRIME TODOS LOS ROLES, SUS PERMISOS Y USUARIOS A LOS QUE ESTA ASIGNADO
     * (EAGER FETCH)
     */
    @Test
    public void simpleQuery() {
        streams.streamAll(em, Rol.class)
                .forEach(r -> System.out.println("rol: " + r.getId() + " permisos: " + r.getPermisoCollection() + " usuarios: " + r.getUsuarioCollection()));
    }

    /**
     *
     * IMPRIME LOS ROLES CON NOMBRE ADMINISTRADOR DE CONTRATOS
     */
    @Test
    public void simpleQueryFilter() {
        streams.streamAll(em, Rol.class)
                .filter(r -> r.getNombre().equals("Administrador de contratos"))
                .forEach(r -> System.out.println("rol administrador de contratos: " + r.getId()));
    }

    /**
     *
     * IMPRIME LOS NOMBRES DE LOS ROLES
     */
    @Test
    public void simpleQueryProjection() {
        streams.streamAll(em, Rol.class)
                .map(r -> r.getNombre())
                .forEach(nombre -> System.out.println(nombre));
    }

    @After
    public void tearDown() {
        em.getTransaction().commit();

    }

    @AfterClass
    public static void closeEntityManager() {
        if (em != null) {
            em.close();
        }
    }

}
