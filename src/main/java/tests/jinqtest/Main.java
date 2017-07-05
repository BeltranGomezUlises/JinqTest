package tests.jinqtest;

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
import org.jinq.jpa.JPAQueryLogger;
import org.jinq.jpa.JinqJPAStreamProvider;

/**
 *
 * @author Ulises Beltrán Gómez --- beltrangomezulises@gmail.com
 */
public class Main {

    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("postgres-unit");
    private static final EntityManager EM = FACTORY.createEntityManager();

    private static final JinqJPAStreamProvider STREAMS = new JinqJPAStreamProvider(FACTORY);

    static {
        STREAMS.setHint(
                "queryLogger", (JPAQueryLogger) (String query, Map<Integer, Object> positionParameters, Map<String, Object> namedParameters) -> {
                    System.out.println("queryLogr -> " + query);
                });
    }

    public static void main(String[] args) {
        //initDBExamen();
        consultasDBExamen();
    }

    private static void initDBExamen() {

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
        
        
        PermisoJpaController permisoController = new PermisoJpaController(FACTORY);
        RolJpaController rolController = new RolJpaController(FACTORY);
        UsuarioJpaController usuarioController = new UsuarioJpaController(FACTORY);
        
        permisoController.create(permiso1);
        permisoController.create(permiso2);
        permisoController.create(permiso3);
        
        rolController.create(rolcapturista);
        rolController.create(rolAdministrador);
        
        usuarioController.create(usuario1);
        usuarioController.create(usuario2);
        
    }

    private static void consultasDBExamen() {

        
        System.out.println("Consulta de los roles con sus permisos y lo usuarios asignados (eager fecth)");
        long time = System.currentTimeMillis();        
        STREAMS.streamAll(EM, Rol.class)
                .forEach( r -> System.out.println("rol: " + r.getId() + " permisos: " + r.getPermisoCollection() + " usuarios: " + r.getUsuarioCollection()));
        long finalTime = System.currentTimeMillis();
        
        STREAMS.streamAll(EM, Usuario.class).forEach( u -> System.out.println("usuario: " + u.getId() + " permisos: " + u.getRol().getPermisoCollection()));
        
        
        System.out.println("total tiempo : " + (finalTime - time));
        
        
    }
}
