package com.amx.milk.service;

import com.amx.milk.dao.DairyFarmInfoDao;
import com.amx.milk.entity.Bucket;
import com.amx.milk.entity.Cow;
import com.amx.milk.entity.CowOperation;
import com.amx.milk.entity.DairyFarmInfo;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DairyFarmServiceImpl implements DairyFarmService{

    @Resource
    DairyFarmInfoDao dairyFarmInfoDao;

    @Override
    public List<DairyFarmInfo> getAllDairyFarmInfo() {
        return dairyFarmInfoDao.getAllDairyInfo();
    }

    @Override
    public List<DairyFarmInfo> getDairyFarmByPage(int pageNo, int pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<DairyFarmInfo> dairyFarmInfoList = dairyFarmInfoDao.getAllDairyInfo();
        return dairyFarmInfoList;
    }

    @Override
    public int createDairyFarm(DairyFarmInfo dairyFarmInfo) {
        return dairyFarmInfoDao.createDairyFarm(dairyFarmInfo);
    }

    @Override
    public List<CowOperation> getAllCowOperation() {
        return dairyFarmInfoDao.getAllCowOperation();
    }

    @Override
    public List<CowOperation> getCowOpearationByPage(int pageNo, int pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<CowOperation> cowOperationList = dairyFarmInfoDao.getAllCowOperation();
        return cowOperationList;
    }

    @Override
    public List<CowOperation> getCowOperation(String cowId) {
        return dairyFarmInfoDao.getCowOperation(cowId);
    }

    @Override
    public List<Bucket> getAllBucket() {
        return dairyFarmInfoDao.getAllBucket();
    }

    @Override
    public List<Bucket> getBucketByPage(int pageNo, int pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<Bucket> bucketList = dairyFarmInfoDao.getAllBucket();
        return bucketList;
    }

    @Override
    public Cow queryCowExist(String cowId) {
        return dairyFarmInfoDao.queryCowExist(cowId);
    }

    @Override
    public int addCowOperation(CowOperation cowOperation) {
        return dairyFarmInfoDao.addCowOperation(cowOperation);
    }

    @Override
    public int addBucket(Bucket bucket) {
        return dairyFarmInfoDao.addBucket(bucket);
    }

    @Override
    public DairyFarmInfo getDairyFarmById(String farmId) {
        return dairyFarmInfoDao.getDairyFarmById(farmId);
    }

    @Override
    public int updateDairyFarm(DairyFarmInfo dairyFarmInfo) {
        return dairyFarmInfoDao.updateDairyFarm(dairyFarmInfo);
    }
}
