package com.dianming.jd.tool;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Transactional
public abstract class GenericService<T> {
    public abstract GenericDao<T> getDao();

    public void save(T a) {
        getDao().save(a);
    }

    public void save(Collection<T> as) {
        if (!Fusion.isEmpty(as)) {
            for (T a : as) {
                getDao().save(a);
            }
        }
    }

    public void update(T a) {
        getDao().update(a);
    }

    public void saveOrUpdate(T a) {
        getDao().saveOrUpdate(a);
    }

    public T get(int id) {
        return (T) getDao().get(id);
    }

    public T get(long id) {
        return (T) getDao().get(id);
    }

    public void delete(T c) {
        getDao().delete(c);
    }

    public void delete(Collection<T> list) {
        getDao().deleteAll(list);
    }

    public void deleteByKey(int id) {
        getDao().deleteByKey(id);
    }

    public void deleteByKey(long id) {
        getDao().deleteByKey(id);
    }

    public T findFirst(List<Criterion> criterion, Order sorting) {
        return (T) getDao().findFirst(criterion, sorting);
    }

    public List<T> getListByPaged(Pagination page, Order op, List<Criterion> criterion) {
        return getDao().getPaginatedAndOrdered(page, op, criterion);
    }

    public List<T> getListByPaged(org.hibernate.criterion.ProjectionList pl, Pagination page, Order op, List<Criterion> criterion) {
        return getDao().getPaginatedAndOrdered(null, page, op, criterion);
    }

    public List<T> getListByPaged(Pagination page, Order op, List<Criterion> criterion, Map<String, String> aliasMap) {
        return getDao().getPaginatedAndOrdered(null, page, op, criterion, aliasMap);
    }

    public List<T> getAll(List<Criterion> criterion) {
        return getDao().findAll(criterion);
    }

    public long getCount(List<Criterion> criterion) {
        return getDao().count(criterion);
    }

    public void evict(T t) {
        getDao().getSession().evict(t);
    }

    public int execHQL(String hql) {
        return getDao().getSession().createQuery(hql).executeUpdate();
    }

    public int execSQL(String sql) {
        return getDao().getSession().createSQLQuery(sql).executeUpdate();
    }
}