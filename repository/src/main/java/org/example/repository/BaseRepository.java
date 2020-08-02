package org.example.repository;

import org.example.model.BaseEntity;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.lang.InstantiationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRepository extends HibernateDaoSupport {

    private static final Logger log = LoggerFactory.getLogger(BaseRepository.class);

    private Class<? extends BaseEntity> entityClass;

    private static volatile int globalDbRequestsCount = 0;

    /**************************************************************************
     * fair-scheduling related set of PARAMETERS (can be cahanged at runtime)
     */
    public static volatile long UI_REQ_SLEEP_TIME = 30;
    public static volatile long JOB_SLEEP_TIME = 100;
    public static volatile long MAX_TIME_WITHOUT_SLEEP = 500;
    public static volatile long MIN_NUM_DB_REQUESTS_FROM_LAST_SLEEP = 3;

    private static List<Thread> globallySuspendedThreads = new ArrayList<Thread>();
    private static volatile boolean globalThreadsSuspensionRequsted = false;
    /**************************************************************************/

    private static ThreadLocal<Long> lastSleepTime = new ThreadLocal<Long>() {
        protected synchronized Long initialValue() {
            return System.currentTimeMillis();
        }
    };

    private static ThreadLocal<Boolean> isJobThread = new ThreadLocal<Boolean>() {
        protected synchronized Boolean initialValue() {
            return false;
        }
    };

    private static ThreadLocal<Integer> numDbRequestsFromLastSleep = new ThreadLocal<Integer>() {
        protected synchronized Integer initialValue() {
            return 0;
        }
    };

    public static interface JobControlBroker {
        public void onJobControlFromRepository();
    }


    private static JobControlBroker jobControlBroker = null;

    public static void setIsJobThread(boolean is) {
        isJobThread.set(is);
    }

    public static void setJobControlBorker(JobControlBroker borker) {
        jobControlBroker = borker;
    }

    public static void requestGlobalThreadSuspension() {
        globalThreadsSuspensionRequsted = true;
    }

    public static void resumeThreadsAfterGlobalSuspension() {

        globalThreadsSuspensionRequsted = false;

        synchronized (globallySuspendedThreads) {

            for(Thread th : globallySuspendedThreads)
                th.interrupt();

            globallySuspendedThreads.clear();
        }
    }

    public static int getGlobalNumberOfDbRequests() {
        return globalDbRequestsCount;
    }

    private static Map<Class<?>, BaseEntity> cachedBaseEntities = new HashMap<Class<?>, BaseEntity>();

    public static void resetLastSleepTime() {
        lastSleepTime.set(System.currentTimeMillis());
        numDbRequestsFromLastSleep.set(0);
    }

    /**
     * TODO TODO comments... (AA)
     */
    public static void ensureFairThreadScheduling(boolean isDbRequest) {

        long diff = System.currentTimeMillis() - lastSleepTime.get();

        // increase thread-local number of db-requests
        int numDbRequests = numDbRequestsFromLastSleep.get();
        if (isDbRequest) {
            ++numDbRequests;
            // incrament global number of db-reuests
            ++globalDbRequestsCount;
        }
        numDbRequestsFromLastSleep.set(numDbRequests);

        if (diff >= MAX_TIME_WITHOUT_SLEEP) {
            try {
                if (numDbRequests >= MIN_NUM_DB_REQUESTS_FROM_LAST_SLEEP || !isDbRequest) {
                    //log.info("\n\n\n!!!!!!!!!!!!!!! SLEEPING (" + diff + ") !!!!!!!!!!!!!!\n\n\n");
                    Thread.currentThread().sleep(isJobThread.get() ? JOB_SLEEP_TIME : UI_REQ_SLEEP_TIME);
                }
            }
            catch(Throwable e) {
                // ignore
            }

            resetLastSleepTime();
        }

        if (jobControlBroker != null)
            jobControlBroker.onJobControlFromRepository();

        // handle global-threads-suspension request (if any)

        if (globalThreadsSuspensionRequsted) {
            handleGLOBALSUSPENSION();
        }
    }

    private static void handleGLOBALSUSPENSION() {
        try {
            log.info("###### Entering GLOBAL-THREAD-SUSPENSION (respsitory: " + BaseRepository.class.getSimpleName() + ") ... " + Thread.currentThread().getName());
            synchronized (globallySuspendedThreads) {
                globallySuspendedThreads.add(Thread.currentThread());
            }

            Thread.sleep(1000L*1000L*1000L);
        } catch (InterruptedException e) {
            log.info("###### Waking from GLOBAL-THREAD-SUSPENSION (respsitory: " + BaseRepository.class.getSimpleName() + ") ... " +  Thread.currentThread().getName());
        }
    }

    /**
     * Default constructor.
     */
    public BaseRepository() {
        super();
    }

    public <T extends BaseEntity> T saveEntity(final T entity) {
        if (entity.getId() == null) {
            return this.createEntity(entity);
        } else {
            return this.updateEntity(entity);
        }
    }


    /**
     * @param entity
     * @return
     */
    public <T extends BaseEntity> T createEntity(final T entity) {
        return (T) this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.save(entity);
                ensureFairThreadScheduling(true);
                return entity;
            }
        });

    }

    /**
     * @param entity
     * @param transactionSession
     * @return
     */
    public <T extends BaseEntity> T createEntity(final T entity, Session transactionSession) {
        final Session tranSession = transactionSession;
        return (T) this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                tranSession.save(entity);
                ensureFairThreadScheduling(true);
                return entity;
            }
        });

    }

    public void flushAndClearSession() {
        this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                log.info("#### ENTERING flushAndClearSession()");
                session.flush();
                session.clear();
                return null;
            }
        });
    }

    public void startTransaction() {
        this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.beginTransaction();
                return null;
            }
        });
    }

    public void rollbackTransaction() {
        this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.getTransaction().rollback();
                return null;
            }
        });
    }

    /**
     * @param entity
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> T updateEntity(final T entity) {
        return (T) this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                // strange case... [LEMM-7412]
                // Update throws an error, when session doesn't contain this entity, tough error says opposite...
                // anyway, using merge in this case saves the day.
                // the only problem using merge is that it doesn't update object in session, so additional update must be used.
                // if some problems occur with not properly updated entities, you might want to debug merge method.
                if (!session.contains(entity)) {
                    T merged = (T) session.merge(entity);
                    session.update(merged);
                    ensureFairThreadScheduling(true);
                    return merged;
                }
                session.update(entity);
                ensureFairThreadScheduling(true);
                return (T)entity;
            }
        });
    }

    /**
     * @param entity
     * @param transactionSession
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> T updateEntity(final T entity, Session transactionSession) {
        final Session tranSession = transactionSession;
        return (T) this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                tranSession.update(entity);
                ensureFairThreadScheduling(true);
                return entity;
            }
        });
    }

    /**
     * @param entity
     * @return
     */
    public <T extends BaseEntity> T deleteEntity(final T entity) {
        return (T) this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.delete(entity);
                ensureFairThreadScheduling(true);
                return entity;
            }
        });
    }



    /**
     * @param entity
     * @param transactionSession
     * @return
     */
    public <T extends BaseEntity> T deleteEntity(final T entity, Session transactionSession) {
        final Session tranSession = transactionSession;
        return (T) this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                tranSession.delete(entity);
                ensureFairThreadScheduling(true);
                return entity;
            }
        });
    }


    public <T extends BaseEntity> T findByID(final Integer ID, final boolean nullOnNotFound) {
        T result = (T) this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {

                long startTm = System.currentTimeMillis();

                BaseEntity entity = (BaseEntity) session.get(
                        entityClass, ID, LockMode.READ);

                ensureFairThreadScheduling(true);
                if (log.isTraceEnabled())
                    log.trace("findByID() " + entityClass.getSimpleName() + " in " + (System.currentTimeMillis()-startTm) + " (num-sqls: " + (globalDbRequestsCount) + ")");

                return entity;
            }
        });

        if ((result == null) && (!nullOnNotFound)) {
            result = (T)this.newEntityInstance();
        }
        return result;
    }

    public <T extends BaseEntity> List<T> findAll() {
        return this.findAll(new Object[0], 0, 0);
    }

    public <T extends BaseEntity> List<T> findAll(final Object[] criterions) {
        return this.findAll(criterions, 0, 0);
    }

    /**
     * returns ids of all entities matching given criteria. Can be used to optimize
     * certain access cases where we don't need the list of whole entities, just
     * there ids, this mainly reduces network traffic
     */
    public List<Integer> findAllIds(final Object[] criterions, final int offset, final int limit) {
        HibernateCallback hc = new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                long startTm = System.currentTimeMillis();
                Criteria c = session.createCriteria(entityClass, "master");

                c.setProjection(Projections.id());

                for (Object o : criterions) {
                    if (o instanceof Criterion) {
                        c.add((Criterion) o);
                    }

                    if (o instanceof Order) {
                        c.addOrder((Order) o);
                    }

                }

                if (offset != 0) {
                    c.setFirstResult(offset);
                }

                if (limit != 0) {
                    c.setMaxResults(limit);
                }

                c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

                Object result = c.list();

                ensureFairThreadScheduling(true);
                if (log.isTraceEnabled())
                    log.trace("findAllIDS() " + entityClass.getSimpleName() + " in " + (System.currentTimeMillis()-startTm) + " (num-sqls: " + (globalDbRequestsCount) + ")");

                return result;
            }
        };

        return (List<Integer>)this.getHibernateTemplate().execute(hc);
    }


    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> List<T> findAll(final Object[] criterions, final int offset, final int limit) {
        HibernateCallback hc = new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {

                long startTm = System.currentTimeMillis();

                Criteria c = session.createCriteria(entityClass, "master");

                for (Object o : criterions) {
                    if (o instanceof ArrayList) {

                        for (Object x : ((ArrayList<Criterion>)o)) {
                            if (x instanceof Criterion) {
                                c.add((Criterion) x);
                            }
                        }

                    }

                    if (o instanceof Criterion) {
                        c.add((Criterion) o);
                    }

                    if (o instanceof Order) {
                        c.addOrder((Order) o);
                    }

                }

                if (offset != 0) {
                    c.setFirstResult(offset);
                }


                if (limit != 0) {
                    c.setMaxResults(limit);
                }

                c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

                Object result = (List<T>)c.list();

                ensureFairThreadScheduling(true);
                if (log.isTraceEnabled())
                    log.trace("findAll() " + entityClass.getSimpleName() + " in " + (System.currentTimeMillis()-startTm) + " (num-sqls: " + (globalDbRequestsCount) + ")");

                return result;
            }
        };

        return (List<T>) this.getHibernateTemplate().execute(hc);
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> List<T> findAllWithTrans(final Object[] criterions, final int offset, final int limit, final Session transSession) {
        HibernateCallback hc = new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {

                long startTm = System.currentTimeMillis();

                Criteria c = (transSession != null ? transSession : session).
                        createCriteria(entityClass, "master");

                for (Object o : criterions) {
                    if (o instanceof ArrayList) {

                        for (Object x : ((ArrayList<Criterion>)o)) {
                            if (x instanceof Criterion) {
                                c.add((Criterion) x);
                            }
                        }

                    }

                    if (o instanceof Criterion) {
                        c.add((Criterion) o);
                    }

                    if (o instanceof Order) {
                        c.addOrder((Order) o);
                    }

                }

                if (offset != 0) {
                    c.setFirstResult(offset);
                }


                if (limit != 0) {
                    c.setMaxResults(limit);
                }

                c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

                Object result = (List<T>)c.list();

                ensureFairThreadScheduling(true);
                if (log.isTraceEnabled())
                    log.trace("findAll() " + entityClass.getSimpleName() + " in " + (System.currentTimeMillis()-startTm) + " (num-sqls: " + (globalDbRequestsCount) + ")");

                return result;
            }
        };

        return (List<T>) this.getHibernateTemplate().execute(hc);
    }

    public <T extends BaseEntity> T findFirst(final Object[] criterions) {
        List<T> l = this.findAll(criterions, 0, 1);
        if (l.size() > 0) {
            return l.get(0);
        } else {
            return null;
        }
    }

    public int count(final Object[] criterions) {
        HibernateCallback hc = new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria c = session.createCriteria(entityClass, "master");

                for (Object o : criterions) {
                    if (o instanceof Criterion) {
                        c.add((Criterion) o);
                    }

                }

                c.setProjection(Projections.rowCount());
                return c.list();
            }
        };
        List<?> l = (List<?>) this.getHibernateTemplate().execute(hc);
        Long i = (Long) l.get(0);

        ensureFairThreadScheduling(true);
        return i.intValue();
    }

    public Object max(final String fieldName, final Object[] criterions) {
        HibernateCallback hc = new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria c = session.createCriteria(entityClass, "master");

                for (Object o : criterions) {
                    if (o instanceof Criterion) {
                        c.add((Criterion) o);
                    }

                }

                c.setProjection(Projections.max(fieldName));
                return c.list();
            }
        };
        List<?> l = (List<?>) this.getHibernateTemplate().execute(hc);

        ensureFairThreadScheduling(true);
        return l.get(0);
    }

    public void deleteByHql(final String hql) {
        HibernateCallback hc = new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.createQuery(hql).executeUpdate();
                return null;
            }
        };

        this.getHibernateTemplate().execute(hc);
    }


    public void setEntityClass(Class<? extends BaseEntity> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<? extends BaseEntity> getEntityClass() {
        return this.entityClass;
    }


    public <T extends BaseEntity> T getEntityInstance() {
        BaseEntity entity = cachedBaseEntities.get(entityClass);
        if (entity == null)
        {
            entity = newEntityInstance();
            cachedBaseEntities.put(entityClass, entity);
        }
        return (T)entity;
    }

    public <T extends BaseEntity> T newEntityInstance() {
        BaseEntity entity = null;
        if (entity == null)
        {
            try {
                entity = (BaseEntity) this.entityClass.newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return (T)entity;
    }

    /**
     * Autowires hibernate session factory for all repository implementations.
     *
     * @param sessionFactory
     */
    @Autowired
    public void setLemmingHibernateSessionFactory(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
    }

}
