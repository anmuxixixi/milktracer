package com.amx.milk.controller;

import com.amx.milk.entity.*;
import com.amx.milk.sdk.FabricClient;
import com.amx.milk.sdk.UserContext;
import com.amx.milk.sdk.UserUtils;
import com.amx.milk.service.DairyFarmServiceImpl;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@RestController
public class DairyFarmController {



    private static final String keyFolderPath = "C:\\Users\\84262\\Desktop\\牛奶溯源\\牛奶溯源后端\\milk_back\\milk\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.cow.com\\users\\Admin@org1.cow.com\\msp\\keystore";
    private static final String keyFileName = "6b0d5e0392ff2078a16dc632521e7a442469d20f94191679f70186da6eaaa10d_sk";
    private static final String certFolderPath = "C:\\Users\\84262\\Desktop\\牛奶溯源\\牛奶溯源后端\\milk_back\\milk\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.cow.com\\users\\Admin@org1.cow.com\\msp\\signcerts";
    private static final String certFileName = "Admin@org1.cow.com-cert.pem";

    private static final String tlsPeerFilePath = "C:\\Users\\84262\\Desktop\\牛奶溯源\\牛奶溯源后端\\milk_back\\milk\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.cow.com\\tlsca\\tlsca.org1.cow.com-cert.pem";
    private static final String tlsOrderFilePath = "C:\\Users\\84262\\Desktop\\牛奶溯源\\牛奶溯源后端\\milk_back\\milk\\src\\main\\resources\\crypto-config\\ordererOrganizations\\cow.com\\tlsca\\tlsca.cow.com-cert.pem";


    @Autowired
    private DairyFarmServiceImpl dairyFarmService;

    @GetMapping("/dairyfarm/list")
    public Response getAllDairyFarmInfo(@RequestParam("page") int pageNo,@RequestParam("limit")int pageSize){

        Response response = new Response();

        List<DairyFarmInfo> dairyFarmInfoList = dairyFarmService.getAllDairyFarmInfo();
        List<DairyFarmInfo> dairyFarmInfos = dairyFarmService.getDairyFarmByPage(pageNo,pageSize);

        // 返回的数据总条目和具体的信息
        Map<String,Object> map = new HashMap<>();

        map.put("total",dairyFarmInfoList.size());
        map.put("items",dairyFarmInfos);

        response.setCode(200);
        response.setMessage("获取奶牛场信息成功");
        response.setData(map);
        return response;
    }

    @PostMapping("/dairyfarm/create")
    public Response createDairyFarm(@RequestBody DairyFarmInfo dairyFarmInfo) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, ProposalException, TransactionException {
        Response response = new Response();

        int count = dairyFarmService.createDairyFarm(dairyFarmInfo);
        if (count > 0){
            response.setCode(200);
            response.setMessage("成功添加用户");
        }else {
            response.setCode(500);
            response.setMessage("添加用户失败");
        }

        // 将数据更新到区块链系统中
        UserContext userContext = new UserContext();
        userContext.setAffiliation("Org1");
        userContext.setMspId("Org1MSP");
        userContext.setAccount("安慕嘻");
        userContext.setName("admin");
        Enrollment enrollment =  UserUtils.getEnrollment(keyFolderPath,keyFileName,certFolderPath,certFileName);
        userContext.setEnrollment(enrollment);
        FabricClient fabricClient = new FabricClient(userContext);
        Peer peer0 = fabricClient.getPeer("peer0.org1.cow.com","grpcs://peer0.org1.cow.com:7051",tlsPeerFilePath);
        List<Peer> peers = new ArrayList<>();
        peers.add(peer0);
        Orderer order = fabricClient.getOrderer("orderer.cow.com","grpcs://orderer.cow.com:7050",tlsOrderFilePath);
        String initArgs[] = {dairyFarmInfo.getId(),dairyFarmInfo.getName()};
        fabricClient.invoke("amxchannel", TransactionRequest.Type.GO_LANG,"dairyfarm",order,peers,"addDairyFarm",initArgs);

        return response;
    }

    @GetMapping("/dairyfarm/historylist")
    public Response getOperationList(@RequestParam("page") int pageNo,@RequestParam("limit")int pageSize){

        Response response = new Response();

        List<CowOperation> cowOperationList = dairyFarmService.getAllCowOperation();
        List<CowOperation> cowOperations = dairyFarmService.getCowOpearationByPage(pageNo,pageSize);

        // 返回的数据总条目和具体的信息
        Map<String,Object> map = new HashMap<>();

        map.put("total",cowOperationList.size());
        map.put("items",cowOperations);

        response.setCode(200);
        response.setMessage("获取操作历史记录成功");
        response.setData(map);

        return response;
    }


    @GetMapping("/dairyfarm/queryhistory")
    public Response getCowOperation(@RequestParam("cowId") String cowid){
        Response response = new Response();

        List<CowOperation> cowOperations = dairyFarmService.getCowOperation(cowid);

        if (cowOperations.size() != 0){
            // 返回的数据总条目和具体的信息
            Map<String,Object> map = new HashMap<>();

            map.put("total",cowOperations.size());
            map.put("items",cowOperations);

            response.setCode(200);
            response.setMessage("获取奶牛场信息成功");
            response.setData(map);
        }else {
            response.setCode(500);
            response.setMessage("查不到相应的奶牛");
        }

        return response;
    }


    @GetMapping("/dairyfarm/bucket")
    public Response getBucket(@RequestParam("page") int pageNo,@RequestParam("limit")int pageSize){

        Response response = new Response();

        List<Bucket> bucketList = dairyFarmService.getAllBucket();
        List<Bucket> buckets = dairyFarmService.getBucketByPage(pageNo,pageSize);

        Map<String,Object> map = new HashMap<>();
        map.put("total",bucketList.size());
        map.put("items",buckets);

        response.setCode(200);
        response.setMessage("成功获取奶桶信息");
        response.setData(map);

        return response;

    }


    @PostMapping("/dairyfarm/operate")
    public Response operateCow(@RequestBody CowOperation cowOperation) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, ProposalException, TransactionException {
        Response response = new Response();
        Map<String,String> opeMap = new HashMap<>();
        opeMap.put("0","喂养");
        opeMap.put("1","挤奶");
        opeMap.put("2","检疫");

        //System.out.println(cowOperation);

        // 获取传进来的奶牛ID
        String cowId = cowOperation.getCowId();

        Cow cow = dairyFarmService.queryCowExist(cowId);
        if (cow == null){
            response.setCode(500);
            response.setMessage("奶牛ID不存在");
        }else {
            // 将数据更新到区块链系统中
            UserContext userContext = new UserContext();
            userContext.setAffiliation("Org1");
            userContext.setMspId("Org1MSP");
            userContext.setAccount("安慕嘻");
            userContext.setName("admin");
            Enrollment enrollment =  UserUtils.getEnrollment(keyFolderPath,keyFileName,certFolderPath,certFileName);
            userContext.setEnrollment(enrollment);
            FabricClient fabricClient = new FabricClient(userContext);
            Peer peer0 = fabricClient.getPeer("peer0.org1.cow.com","grpcs://peer0.org1.cow.com:7051",tlsPeerFilePath);
            List<Peer> peers = new ArrayList<>();
            peers.add(peer0);
            Orderer order = fabricClient.getOrderer("orderer.cow.com","grpcs://orderer.cow.com:7050",tlsOrderFilePath);


            String operate = cowOperation.getOperation();
            String farmId = cow.getFarmId();
            CowOperation newCowOperation = new CowOperation();
            newCowOperation.setCowId(cowId);
            newCowOperation.setOperation(opeMap.get(cowOperation.getOperation()));
            newCowOperation.setOperateTime(new Date());
            newCowOperation.setConsumptionOrOutput(cowOperation.getConsumptionOrOutput());
            String[] initArgs = new String[1] ;

            // 更新奶牛操作
            int count = dairyFarmService.addCowOperation(newCowOperation);
            // 获取奶牛场信息
            DairyFarmInfo dairyFarmInfo = dairyFarmService.getDairyFarmById(farmId);

            // 如果是挤奶操作，我们要更新奶桶，并且更新奶场中奶桶的数量
            if (operate.equals("1")) {
                Bucket bucket = new Bucket();
                int BucketNum = dairyFarmInfo.getMax_Bucket_No();
                bucket.setId(cowId+String.format("%0"+6+"d",BucketNum+1));
                bucket.setStatus("代签收");
                bucket.setTime(new Date());
                bucket.setInMachiningTime(new Date());

                // 插入奶桶信息到后台库表中
                count = dairyFarmService.addBucket(bucket);
                // 更新奶牛场信息
                dairyFarmInfo.setMax_Bucket_No(BucketNum+1);
                count = dairyFarmService.updateDairyFarm(dairyFarmInfo);

                initArgs[0] = "{\"cowId\":\""+cowId+"\",\"operation\":"+cowOperation.getOperation()+",\"consumptionOrOutput\":\""+cowOperation.getConsumptionOrOutput()+"\"}";
                fabricClient.invoke("amxchannel", TransactionRequest.Type.GO_LANG,"dairyfarm",order,peers,"addCowMilking",initArgs);

            }else {
                initArgs[0] = "{\"cowId\":\""+cowId+"\",\"consumptionOrOutput\":\""+cowOperation.getConsumptionOrOutput()+"\"}";
                fabricClient.invoke("amxchannel", TransactionRequest.Type.GO_LANG,"dairyfarm",order,peers,"addCowOperate",initArgs);
            }
            response.setCode(200);
        }


        return response;

    }

}
