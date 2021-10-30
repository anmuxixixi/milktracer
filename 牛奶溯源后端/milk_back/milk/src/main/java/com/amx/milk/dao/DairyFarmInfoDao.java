package com.amx.milk.dao;

import com.amx.milk.entity.Bucket;
import com.amx.milk.entity.Cow;
import com.amx.milk.entity.CowOperation;
import com.amx.milk.entity.DairyFarmInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DairyFarmInfoDao {

    List<DairyFarmInfo> getAllDairyInfo();
    int createDairyFarm(DairyFarmInfo dairyFarmInfo);
    List<CowOperation> getAllCowOperation();
    List<CowOperation> getCowOperation(String cowId);

    // 获取奶桶信息
    List<Bucket> getAllBucket();

    // 查询是否能查到对应的奶牛
    Cow queryCowExist(String cowId);
    // 更新奶牛操作
    int addCowOperation(CowOperation cowOperation);
    // 获取奶牛场信息
    DairyFarmInfo getDairyFarmById(String farmId);
    // 增加奶桶
    int addBucket(Bucket bucket);
    // 更新奶牛场信息
    int updateDairyFarm(DairyFarmInfo dairyFarmInfo);

}
