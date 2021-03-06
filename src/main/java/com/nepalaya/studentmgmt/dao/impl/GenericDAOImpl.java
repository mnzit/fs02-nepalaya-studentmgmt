package com.nepalaya.studentmgmt.dao.impl;

import com.nepalaya.studentmgmt.dao.GenericDAO;
import com.nepalaya.studentmgmt.util.LogUtil;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/*
Generic Hibernate Implementation
 */
public abstract class GenericDAOImpl<T, ID> implements GenericDAO<T, ID> {
    private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jdbcDemo");

    public Class<T> persistenceClass(){
        final ParameterizedType type = (ParameterizedType) this.getClass()
                .getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }

    @Override
    public List<T> getAll() throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(persistenceClass());
            Root<T> root = criteriaQuery.from(persistenceClass());
            criteriaQuery = criteriaQuery.select(root);
            TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
            List<T> objects = typedQuery.getResultList();

            if (objects == null || objects.isEmpty()) {
                throw new Exception(String.format("%s's not found", persistenceClass().getSimpleName()));
            }

            return objects;
        } catch (Exception ex) {
            LogUtil.exception(ex);
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    public List<T> getAll(Conditional<T> conditional) throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(persistenceClass());
            Root<T> root = criteriaQuery.from(persistenceClass());
            criteriaQuery = criteriaQuery.select(root);
            conditional.add(criteriaBuilder, criteriaQuery, root);
            TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
            List<T> objects = typedQuery.getResultList();

            if (objects == null || objects.isEmpty()) {
                throw new Exception(String.format("%s's not found", persistenceClass().getSimpleName()));
            }

            return objects;
        } catch (Exception ex) {
            LogUtil.exception(ex);
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public T getOneById(ID id) throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            T object = entityManager.find(persistenceClass(), id);
            if (object == null) {
                throw new Exception(String.format("%s not found", persistenceClass().getSimpleName()));
            }
            return object;
        } catch (Exception ex) {
            LogUtil.exception(ex);
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    public T getOneById(Conditional<T> conditional) throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(persistenceClass());
            Root<T> root = criteriaQuery.from(persistenceClass());
            criteriaQuery = criteriaQuery.select(root);
            conditional.add(criteriaBuilder, criteriaQuery, root);
            TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
            T object = typedQuery.getSingleResult();

            if (object == null) {
                throw new Exception(String.format("%s not found", persistenceClass().getSimpleName()));
            }

            return object;
        } catch (Exception ex) {
            LogUtil.exception(ex);
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void save(T object) throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(object);
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
            LogUtil.exception(ex);
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void update(T object) throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            transaction = entityManager.getTransaction();
            entityManager.merge(object);
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
            LogUtil.exception(ex);
            throw ex;
        } finally {
            entityManager.close();
        }
    }
    
    
}
