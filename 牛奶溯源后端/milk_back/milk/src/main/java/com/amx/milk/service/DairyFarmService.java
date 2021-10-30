package com.amx.milk.service;

import com.amx.milk.entity.Bucket;
import com.amx.milk.entity.Cow;
import com.amx.milk.entity.CowOperation;
import com.amx.milk.entity.DairyFarmInfo;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DairyFarmService {

    List<DairyFarmInfo> getAllDairyFarmInfo();
    List<DairyFarmInfo> getDairyFarmByPage(int pageNo,int pageSize);
    int createDairyFarm(DairyFarmInfo dairyFarmInfo);

    List<CowOperation> getAllCowOperation();
    List<CowOperation> getCowOpearationByPage(int pageNo,int pageSize);

    List<CowOperation> getCowOperation(String cowId);

    List<Bucket> getAllBucket();
    List<Bucket> getBucketByPage(int pageNo,int pageSize);

    Cow queryCowExist(String cowId);

    int addCowOperation(CowOperation cowOperation);
    int addBucket(Bucket bucket);
    DairyFarmInfo getDairyFarmById(String farmId);
    int updateDairyFarm(DairyFarmInfo dairyFarmInfo);
}
