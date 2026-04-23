package com.gasc.ams.service;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gasc.ams.dao.RegisterOptionsDao;

@Service
public class RegisterOptionService {
    final RegisterOptionsDao registerOptionsDao = new RegisterOptionsDao();

    public List<Map.Entry<String, String>> getOptions(String tableName) {
        return registerOptionsDao.fetchOptions(tableName);
    }

    public List<String> getOptionsCascade(String tablename, int columnIndex, String filter) {
        return registerOptionsDao.fetchOptionsCascade(tablename, columnIndex, filter);
    }

}
