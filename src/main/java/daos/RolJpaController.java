/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daos;

import daos.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.postrgres.Permiso;
import entities.postrgres.Rol;
import java.util.ArrayList;
import java.util.Collection;
import entities.postrgres.Usuario;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Ulises Beltrán Gómez --- beltrangomezulises@gmail.com
 */
public class RolJpaController implements Serializable {

    public RolJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Rol rol) {
        if (rol.getPermisoCollection() == null) {
            rol.setPermisoCollection(new ArrayList<Permiso>());
        }
        if (rol.getUsuarioCollection() == null) {
            rol.setUsuarioCollection(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Permiso> attachedPermisoCollection = new ArrayList<Permiso>();
            for (Permiso permisoCollectionPermisoToAttach : rol.getPermisoCollection()) {
                permisoCollectionPermisoToAttach = em.getReference(permisoCollectionPermisoToAttach.getClass(), permisoCollectionPermisoToAttach.getId());
                attachedPermisoCollection.add(permisoCollectionPermisoToAttach);
            }
            rol.setPermisoCollection(attachedPermisoCollection);
            Collection<Usuario> attachedUsuarioCollection = new ArrayList<Usuario>();
            for (Usuario usuarioCollectionUsuarioToAttach : rol.getUsuarioCollection()) {
                usuarioCollectionUsuarioToAttach = em.getReference(usuarioCollectionUsuarioToAttach.getClass(), usuarioCollectionUsuarioToAttach.getId());
                attachedUsuarioCollection.add(usuarioCollectionUsuarioToAttach);
            }
            rol.setUsuarioCollection(attachedUsuarioCollection);
            em.persist(rol);
            for (Permiso permisoCollectionPermiso : rol.getPermisoCollection()) {
                permisoCollectionPermiso.getRolCollection().add(rol);
                permisoCollectionPermiso = em.merge(permisoCollectionPermiso);
            }
            for (Usuario usuarioCollectionUsuario : rol.getUsuarioCollection()) {
                Rol oldRolOfUsuarioCollectionUsuario = usuarioCollectionUsuario.getRol();
                usuarioCollectionUsuario.setRol(rol);
                usuarioCollectionUsuario = em.merge(usuarioCollectionUsuario);
                if (oldRolOfUsuarioCollectionUsuario != null) {
                    oldRolOfUsuarioCollectionUsuario.getUsuarioCollection().remove(usuarioCollectionUsuario);
                    oldRolOfUsuarioCollectionUsuario = em.merge(oldRolOfUsuarioCollectionUsuario);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Rol rol) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol persistentRol = em.find(Rol.class, rol.getId());
            Collection<Permiso> permisoCollectionOld = persistentRol.getPermisoCollection();
            Collection<Permiso> permisoCollectionNew = rol.getPermisoCollection();
            Collection<Usuario> usuarioCollectionOld = persistentRol.getUsuarioCollection();
            Collection<Usuario> usuarioCollectionNew = rol.getUsuarioCollection();
            Collection<Permiso> attachedPermisoCollectionNew = new ArrayList<Permiso>();
            for (Permiso permisoCollectionNewPermisoToAttach : permisoCollectionNew) {
                permisoCollectionNewPermisoToAttach = em.getReference(permisoCollectionNewPermisoToAttach.getClass(), permisoCollectionNewPermisoToAttach.getId());
                attachedPermisoCollectionNew.add(permisoCollectionNewPermisoToAttach);
            }
            permisoCollectionNew = attachedPermisoCollectionNew;
            rol.setPermisoCollection(permisoCollectionNew);
            Collection<Usuario> attachedUsuarioCollectionNew = new ArrayList<Usuario>();
            for (Usuario usuarioCollectionNewUsuarioToAttach : usuarioCollectionNew) {
                usuarioCollectionNewUsuarioToAttach = em.getReference(usuarioCollectionNewUsuarioToAttach.getClass(), usuarioCollectionNewUsuarioToAttach.getId());
                attachedUsuarioCollectionNew.add(usuarioCollectionNewUsuarioToAttach);
            }
            usuarioCollectionNew = attachedUsuarioCollectionNew;
            rol.setUsuarioCollection(usuarioCollectionNew);
            rol = em.merge(rol);
            for (Permiso permisoCollectionOldPermiso : permisoCollectionOld) {
                if (!permisoCollectionNew.contains(permisoCollectionOldPermiso)) {
                    permisoCollectionOldPermiso.getRolCollection().remove(rol);
                    permisoCollectionOldPermiso = em.merge(permisoCollectionOldPermiso);
                }
            }
            for (Permiso permisoCollectionNewPermiso : permisoCollectionNew) {
                if (!permisoCollectionOld.contains(permisoCollectionNewPermiso)) {
                    permisoCollectionNewPermiso.getRolCollection().add(rol);
                    permisoCollectionNewPermiso = em.merge(permisoCollectionNewPermiso);
                }
            }
            for (Usuario usuarioCollectionOldUsuario : usuarioCollectionOld) {
                if (!usuarioCollectionNew.contains(usuarioCollectionOldUsuario)) {
                    usuarioCollectionOldUsuario.setRol(null);
                    usuarioCollectionOldUsuario = em.merge(usuarioCollectionOldUsuario);
                }
            }
            for (Usuario usuarioCollectionNewUsuario : usuarioCollectionNew) {
                if (!usuarioCollectionOld.contains(usuarioCollectionNewUsuario)) {
                    Rol oldRolOfUsuarioCollectionNewUsuario = usuarioCollectionNewUsuario.getRol();
                    usuarioCollectionNewUsuario.setRol(rol);
                    usuarioCollectionNewUsuario = em.merge(usuarioCollectionNewUsuario);
                    if (oldRolOfUsuarioCollectionNewUsuario != null && !oldRolOfUsuarioCollectionNewUsuario.equals(rol)) {
                        oldRolOfUsuarioCollectionNewUsuario.getUsuarioCollection().remove(usuarioCollectionNewUsuario);
                        oldRolOfUsuarioCollectionNewUsuario = em.merge(oldRolOfUsuarioCollectionNewUsuario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = rol.getId();
                if (findRol(id) == null) {
                    throw new NonexistentEntityException("The rol with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol rol;
            try {
                rol = em.getReference(Rol.class, id);
                rol.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rol with id " + id + " no longer exists.", enfe);
            }
            Collection<Permiso> permisoCollection = rol.getPermisoCollection();
            for (Permiso permisoCollectionPermiso : permisoCollection) {
                permisoCollectionPermiso.getRolCollection().remove(rol);
                permisoCollectionPermiso = em.merge(permisoCollectionPermiso);
            }
            Collection<Usuario> usuarioCollection = rol.getUsuarioCollection();
            for (Usuario usuarioCollectionUsuario : usuarioCollection) {
                usuarioCollectionUsuario.setRol(null);
                usuarioCollectionUsuario = em.merge(usuarioCollectionUsuario);
            }
            em.remove(rol);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Rol> findRolEntities() {
        return findRolEntities(true, -1, -1);
    }

    public List<Rol> findRolEntities(int maxResults, int firstResult) {
        return findRolEntities(false, maxResults, firstResult);
    }

    private List<Rol> findRolEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Rol.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Rol findRol(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Rol.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Rol> rt = cq.from(Rol.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
