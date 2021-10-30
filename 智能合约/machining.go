package main

import (
	"encoding/json"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"time"
)

// 牛奶加工
type machining struct{}


func (t *machining) Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success([]byte("奶牛加工智能合约初始化成功"))
}


// 调用智能合约的逻辑函数
func (t *machining) Invoke(stub shim.ChaincodeStubInterface) peer.Response {

	_, args := stub.GetFunctionAndParameters()

	// optType：逻辑函数 【putValue,getLastValue,getHistory】
	// assetName：kv键值对的形式， assetName：奶牛场的名字
	// optContent：传入的参数内容
	optType := args[0]
	assetName := args[1]
	optContent := args[2]

	fmt.Printf("参数为 %s %s %s \n ", optType, assetName, optContent)

	if optType == "putValue" { //设值
		err := stub.PutState(assetName, []byte(optContent))
		if err != nil {
			return shim.Error("添加信息成功")
		}
		return shim.Success([]byte("成功添加信息" + optContent))
	} else if optType == "getLastValue" { //取值
		keyValue, err := stub.GetState(assetName)
		if err != nil {
			return shim.Error("未找到对应的数据")
		}
		return shim.Success(keyValue)
	} else if optType == "getHistory" {
		keysIter, err := stub.GetHistoryForKey(assetName)

		if err != nil {
			return shim.Error(fmt.Sprintf("获取历史数据失败.Error accessing state %s", err))
		}
		defer keysIter.Close()
		var keys []string
		for keysIter.HasNext() {
			response, iterErr := keysIter.Next()
			if iterErr != nil {
				return shim.Error(fmt.Sprintf("GetHistoryForKey operation failed.Error accessing state %s", err))
			}
			//交易编号
			txid := response.TxId
			//交易的值
			txvalue := response.Value
			//当前交易的状态
			txstatus := response.IsDelete
			//交易发生的时间戳
			txtimestamp := response.Timestamp

			tm := time.Unix(txtimestamp.Seconds, 0)
			datestr := tm.Format("2006-01-02 03:04:05 PM")

			fmt.Printf("Tx info - txid:%s value: %s if delete %t datetime:%s\n", txid, string(txvalue), txstatus, datestr)
			keys = append(keys, string(txvalue)+":"+datestr)
		}

		jsonKeys, err := json.Marshal(keys)
		if err != nil {
			return shim.Error(fmt.Sprintf("query operation failed.Error marshaling JSON :%s", err))
		}
		return shim.Success(jsonKeys)
	} else {
		return shim.Success([]byte("成功调用智能合约 !!!!!!!! "))
	}
}

func main() {
	err := shim.Start(new(machining))
	if err != nil {
		fmt.Printf("启动智能合约失败，错误为:%s", err)
	}
}


