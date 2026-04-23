package com.gasc.ams.service;

import org.springframework.stereotype.Service;

import com.gasc.ams.dao.RegisterDao;
import com.gasc.ams.dto.RegistrationForm;

@Service
public class RegisterService {
    private final RegisterDao registerDao = new RegisterDao();

    private String messages;

    public String saveRegistration(RegistrationForm registrationForm) {
        
        messages = registerDao.registerData(registrationForm);

        return  messages;
    }

    public RegistrationForm getAssetById(int assetId) {
        return registerDao.getAssetById(assetId);
    }

    public String updateRegister(RegistrationForm registrationForm) {
        
        messages = registerDao.updatePcAsset(registrationForm);
        
        return  messages;
    }

}
