package com.dianming.jd.tool;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.loader.criteria.CriteriaJoinWalker;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.transform.Transformers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class GenericDao<T> implements BaseDao<T> {
    private Class<T> type;

    public GenericDao() {
        this.type = null;
        Class<?> c = getClass();
        Type t = c.getGenericSuperclass();
        if ((t instanceof ParameterizedType)) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            this.type = ((Class) p[0]);
        }
    }

    protected abstract SessionFactory getSessionFactory();

    public Session getSession() {
        return getSessionFactory().getCurrentSession();
    }


    public T get(int id) {
        return (T) getSession().get(this.type, Integer.valueOf(id));
    }


    public T get(long id) {
        return (T) getSession().get(this.type, Long.valueOf(id));
    }


    public List<T> loadAll() {
        return getSession().createCriteria(this.type).list();
    }

    public void update(T entity) {
        getSession().update(entity);
        getSession().flush();
    }

    public void save(T entity) {
        getSession().save(entity);
        getSession().flush();
    }


    public void saveOrUpdate(T entity) {
        getSession().saveOrUpdate(entity);
        getSession().flush();
    }


    public void saveOrUpdateAll(Collection<T> entities) {
        for (T t : entities) {
            getSession().saveOrUpdate(t);
        }
        getSession().flush();
    }

    public void delete(T entity) {
        getSession().delete(entity);
        getSession().flush();
    }

    public void deleteByKey(int id) {
        T t = get(id);
        if (t != null) {
            getSession().delete(t);
            getSession().flush();
        }
    }

    public void deleteByKey(long id) {
        T t = get(id);
        if (t != null) {
            getSession().delete(t);
            getSession().flush();
        }
    }

    public void deleteAll(Collection<T> entities) {
        for (T t : entities) {
            getSession().delete(t);
            getSession().flush();
        }
    }

    public T findOne(List<Criterion> criterion, Order sorting)
            throws Exception {
        Criteria criteria = getSession().createCriteria(this.type);

        for (Criterion c : criterion) {
            criteria.add(c);
        }
        criteria.setFirstResult(0);
        criteria.setMaxResults(1);

        if (sorting != null) {
            criteria.addOrder(sorting);
        }

        List<T> results = criteria.list();

        if ((results == null) || (results.isEmpty()))
            throw new Exception("No results returned");
        if (results.size() > 1) {
            throw new Exception("More than one result returned.");
        }

        return (T) results.get(0);
    }


    public List<T> findFirstTwoItems(List<Criterion> criterion, Order sorting) {
        Criteria criteria = getSession().createCriteria(this.type);

        for (Criterion c : criterion) {
            criteria.add(c);
        }
        criteria.setFirstResult(0);
        criteria.setMaxResults(2);

        if (sorting != null) {
            criteria.addOrder(sorting);
        }

        List<T> results = criteria.list();

        return results;
    }


    public T findFirst(List<Criterion> criterion, Order sorting) {
        Criteria criteria = getSession().createCriteria(this.type);

        for (Criterion c : criterion) {
            criteria.add(c);
        }
        criteria.setFirstResult(0);
        criteria.setMaxResults(1);

        if (sorting != null) {
            criteria.addOrder(sorting);
        }

        List<T> results = criteria.list();

        if ((results == null) || (results.isEmpty())) {
            return null;
        }

        return (T) results.get(0);
    }

    public List<T> findAll(List<Criterion> criterion) {
        return findAll(criterion, null, null, null, null);
    }

    public List<T> findAll(List<Criterion> criterion, Order sorting, Integer offset, Integer maxResults) {
        return findAll(criterion, sorting, offset, maxResults, null);
    }

    public List<T> findAll(List<Criterion> criterion, Order sorting, ProjectionList projectionList) {
        return findAll(criterion, sorting, null, null, projectionList);
    }


    public List<T> findAll(List<Criterion> criterion, Order sorting, Integer offset, Integer maxResults, ProjectionList projectionList) {
        return findAll(criterion, sorting, offset, maxResults, projectionList, null);
    }


    public List<T> findAll(List<Criterion> criterion, Order sorting, Integer offset, Integer maxResults, ProjectionList projectionList, Map<String, String> aliasMap) {
        Criteria criteria = getSession().createCriteria(this.type);
        if (!Fusion.isEmpty(aliasMap)) {
            for (Entry<String, String> alias : aliasMap.entrySet()) {
                criteria.createAlias((String) alias.getKey(), (String) alias.getValue());
            }
        }
        if (projectionList != null) {
            criteria.setProjection(projectionList);
        }
        if (criterion != null) {
            for (Criterion c : criterion) {
                criteria.add(c);
            }
        }
        if (offset != null) {
            criteria.setFirstResult(offset.intValue());
        }
        if (maxResults != null) {
            criteria.setMaxResults(maxResults.intValue());
        }

        if (sorting != null) {
            criteria.addOrder(sorting);
        }

        List<T> results = criteria.list();

        return results;
    }

    public float sum(List<Criterion> criterion, ProjectionList projectionList) {
        Criteria criteria = getSession().createCriteria(this.type);
        if (projectionList != null) {
            criteria.setProjection(projectionList);
        }
        if (criterion != null) {
            for (Criterion c : criterion) {
                criteria.add(c);
            }
        }

        Object result = criteria.uniqueResult();
        if ((result != null) && ((result instanceof Float))) {
            return ((Float) result).floatValue();
        }

        return 0.0F;
    }

    public long count(List<Criterion> criterion) {
        Criteria criteria = getSession().createCriteria(this.type);
        criteria.setProjection(Projections.rowCount());
        if (criterion != null) {
            for (Criterion c : criterion) {
                criteria.add(c);
            }
        }
        Long value = (Long) criteria.uniqueResult();
        if (value != null) {
            return value.longValue();
        }
        return 0L;
    }

    public List<T> findGrouped(List<Criterion> criterion, Order sort, ProjectionList projectionList) {
        return findGrouped(criterion, sort, projectionList, this.type);
    }


    public <W> List<W> findGrouped(List<Criterion> criterion, Order sort, ProjectionList projectionList, Class<W> transType) {
        Criteria criteria = getSession().createCriteria(this.type);
        if (projectionList != null) {
            criteria.setProjection(projectionList);
        }

        if (criterion != null) {
            for (Criterion c : criterion) {
                criteria.add(c);
            }
        }

        if (sort != null) {
            criteria.addOrder(sort);
        }

        criteria.setResultTransformer(Transformers.aliasToBean(transType));
        try {
            return criteria.list();
        } catch (Exception e) {
        }


        return null;
    }

    private String criteriaTranformSql(Criteria criteria) {
        CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
        SessionImplementor session = criteriaImpl.getSession();
        SessionFactoryImplementor factory = session.getFactory();
        CriteriaQueryTranslator translator = new CriteriaQueryTranslator(factory, criteriaImpl, criteriaImpl.getEntityOrClassName(), "this_");
        String[] implementors = factory.getImplementors(criteriaImpl.getEntityOrClassName());
        CriteriaJoinWalker walker = new CriteriaJoinWalker((OuterJoinLoadable) factory.getEntityPersister(implementors[0]), translator, factory, criteriaImpl, criteriaImpl.getEntityOrClassName(), session.getLoadQueryInfluencers());
        String sql = walker.getSQLString();


        Object[] parameters = translator.getQueryParameters().getPositionalParameterValues();
        if ((sql != null) &&
                (parameters != null) && (parameters.length > 0)) {
            for (Object val : parameters) {
                String value = "%";
                if ((val instanceof Boolean)) {
                    value = ((Boolean) val).booleanValue() ? "1" : "0";
                } else if ((val instanceof String)) {
                    value = "'" + val + "'";
                } else if ((val instanceof Number)) {
                    value = val.toString();
                } else if ((val instanceof Class)) {
                    value = "'" + ((Class) val).getCanonicalName() + "'";
                } else if ((val instanceof Date)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    value = "'" + sdf.format((Date) val) + "'";
                } else if ((val instanceof Enum)) {
                    value = "" + ((Enum) val).ordinal();
                } else {
                    value = val.toString();
                }
                sql = sql.replaceFirst("\\?", value);
            }
        }

        return sql == null ? "" : sql.replaceAll("left outer join", "\nleft outer join").replaceAll(" and ", "\nand ").replaceAll(" on ", "\non ").replaceAll("<>", "!=").replaceAll("<", " < ").replaceAll(">", " > ");
    }

    public int execHQL(String hql) {
        return getSession().createQuery(hql).executeUpdate();
    }

    public int execSQL(String sql) {
        return getSession().createSQLQuery(sql).executeUpdate();
    }

    public List<T> getPaginatedAndOrdered(Pagination pagination, Order sort, List<Criterion> criterion) {
        return getPaginatedAndOrdered(null, pagination, sort, criterion);
    }


    public List<T> getPaginatedAndOrdered(ProjectionList pl, Pagination pagination, Order sort, List<Criterion> criterion) {
        return getPaginatedAndOrdered(pl, pagination, sort, criterion, null);
    }


    public List<T> getPaginatedAndOrdered(ProjectionList pl, Pagination pagination, Order sort, List<Criterion> criterion, Map<String, String> aliasMap) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(this.type);
        if (!Fusion.isEmpty(aliasMap)) {
            for (Entry<String, String> alias : aliasMap.entrySet()) {
                criteria.createAlias((String) alias.getKey(), (String) alias.getValue());
            }
        }
        if (criterion != null) {
            for (Criterion c : criterion) {
                criteria.add(c);
            }
        }
        if (sort != null) {
            criteria.addOrder(sort);
        }

        if (pl != null) {
            criteria.setProjection(pl);
        }
        if ((pagination != null) && (pagination.getPage() > 0)) {
            criteria.setFirstResult((pagination.getPage() - 1) * pagination.getPageSize());
            criteria.setMaxResults(pagination.getPageSize() + 1);
            List<T> list = criteria.list();

            if (list.size() == pagination.getPageSize() + 1) {
                list.remove(list.size() - 1);
                pagination.setHasNext(true);
            }
            return list;
        }


        return criteria.list();
    }

    public void executeHql(String hqlString, Object... values) {
        Query query = getSession().createQuery(hqlString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i, values[i]);
            }
        }
        query.executeUpdate();
    }

    public void executeSql(String sqlString, Object... values) {
        Query query = getSession().createSQLQuery(sqlString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i, values[i]);
            }
        }
        query.executeUpdate();
    }
}