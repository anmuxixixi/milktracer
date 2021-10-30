package com.amx.milk.sdk;


import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FabricClient {

    private static final Logger log = LoggerFactory.getLogger(FabricClient.class);

    private final HFClient hfClient;

    public FabricClient(UserContext userContext) throws IllegalAccessException, InvocationTargetException, InvalidArgumentException, InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException {
        hfClient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfClient.setCryptoSuite(cryptoSuite); // 配置具体的加密算法
        hfClient.setUserContext(userContext); // 设置用户的环境变量
    }


    public Channel getChannel(String channelName) throws InvalidArgumentException, TransactionException, ProposalException {
        Channel channel =  hfClient.newChannel(channelName);
        return channel;
    }

    public Orderer getOrderer(String name,String grpcUrl,String tlsFilePath) throws InvalidArgumentException {
        Properties properties = new Properties();
        properties.setProperty("pemFile",tlsFilePath);
        Orderer orderer = hfClient.newOrderer(name,grpcUrl,properties);
        return orderer;
    }


    public Peer getPeer(String name, String grpcUrl, String tlsFilePath) throws InvalidArgumentException {
        Properties properties = new Properties();
        properties.setProperty("pemFile",tlsFilePath);
        Peer peer = hfClient.newPeer(name,grpcUrl,properties);
        return peer;
    }


    public void invoke(String channelName, TransactionRequest.Type lang, String chaincodeName, Orderer order, List<Peer> peers, String funcName, String args[]) throws TransactionException, ProposalException, InvalidArgumentException {
        // 获取通道，将通道内的每个节点加入
        Channel channel = getChannel(channelName);
        channel.addOrderer(order);
        for(Peer p : peers) {
            channel.addPeer(p);
        }
        channel.initialize();
        TransactionProposalRequest transactionProposalRequest = hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeLanguage(lang);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setFcn(funcName);
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
        transactionProposalRequest.setChaincodeID(builder.build());
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(transactionProposalRequest,peers);
        for(ProposalResponse response:responses){
            if(response.getStatus().getStatus()==200){
                log.info("{} invoke proposal {} sucessful",response.getPeer().getName(),funcName);
            }else{
                String logArgs[] = {response.getMessage(),funcName,response.getPeer().getName()};
                log.error("{} invoke proposal {} failed on {}",logArgs);
            }
        }
        channel.sendTransaction(responses);
    }

}
