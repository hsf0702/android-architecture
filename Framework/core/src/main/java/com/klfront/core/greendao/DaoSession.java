package com.klfront.core.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.klfront.core.App;
import com.klfront.core.User;

import com.klfront.core.greendao.AppDao;
import com.klfront.core.greendao.UserDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig appDaoConfig;
    private final DaoConfig userDaoConfig;

    private final AppDao appDao;
    private final UserDao userDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        appDaoConfig = daoConfigMap.get(AppDao.class).clone();
        appDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        appDao = new AppDao(appDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);

        registerDao(App.class, appDao);
        registerDao(User.class, userDao);
    }
    
    public void clear() {
        appDaoConfig.clearIdentityScope();
        userDaoConfig.clearIdentityScope();
    }

    public AppDao getAppDao() {
        return appDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

}
