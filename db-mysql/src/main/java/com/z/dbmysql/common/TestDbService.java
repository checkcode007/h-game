package com.z.dbmysql.common;

import com.z.dbmysql.dao.user.GUserDao;
import com.z.model.mysql.GUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestDbService {
    @Autowired
    private GUserDao dao;

    public GUser save(GUser GUser) {
        return dao.save(GUser);
    }

    public List<GUser> getAll() {
        return dao.getAll();
    }

}
