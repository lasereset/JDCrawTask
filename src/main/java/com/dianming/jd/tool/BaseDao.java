package com.dianming.jd.tool;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BaseDao<T> {
    T get(int paramInt);

    T get(long paramLong);

    List<T> loadAll();

    void update(T paramT);

    void save(T paramT);

    void saveOrUpdate(T paramT);

    void saveOrUpdateAll(Collection<T> paramCollection);

    void delete(T paramT);

    void deleteByKey(int paramInt);

    void deleteByKey(long paramLong);

    void deleteAll(Collection<T> paramCollection);

    int execHQL(String paramString);

    int execSQL(String paramString);

    T findOne(List<Criterion> paramList, Order paramOrder)
            throws Exception;

    T findFirst(List<Criterion> paramList, Order paramOrder);

    List<T> findFirstTwoItems(List<Criterion> paramList, Order paramOrder);

    List<T> findAll(List<Criterion> paramList);

    List<T> findAll(List<Criterion> paramList, Order paramOrder, Integer paramInteger1, Integer paramInteger2);

    List<T> findAll(List<Criterion> paramList, Order paramOrder, ProjectionList paramProjectionList);

    List<T> findAll(List<Criterion> paramList, Order paramOrder, Integer paramInteger1, Integer paramInteger2, ProjectionList paramProjectionList);

    List<T> findAll(List<Criterion> paramList, Order paramOrder, Integer paramInteger1, Integer paramInteger2, ProjectionList paramProjectionList, Map<String, String> paramMap);

    float sum(List<Criterion> paramList, ProjectionList paramProjectionList);

    long count(List<Criterion> paramList);

    List<T> findGrouped(List<Criterion> paramList, Order paramOrder, ProjectionList paramProjectionList);

    <W> List<W> findGrouped(List<Criterion> paramList, Order paramOrder, ProjectionList paramProjectionList, Class<W> paramClass);

    List<T> getPaginatedAndOrdered(Pagination paramPagination, Order paramOrder, List<Criterion> paramList);

    List<T> getPaginatedAndOrdered(ProjectionList paramProjectionList, Pagination paramPagination, Order paramOrder, List<Criterion> paramList);

    List<T> getPaginatedAndOrdered(ProjectionList paramProjectionList, Pagination paramPagination, Order paramOrder, List<Criterion> paramList, Map<String, String> paramMap);

    void executeHql(String paramString, Object... paramVarArgs);

    void executeSql(String paramString, Object... paramVarArgs);
}