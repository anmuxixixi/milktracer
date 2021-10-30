package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
)


// 奶牛场 结构体
type dairyFarm struct {
	Address string `json:"address"`  // 奶牛场的地址
	CowName string `json:"cow_name"` // 奶牛的名称
	CowType string `json:"cow_type"` // 奶牛的种类
}

// 智能合约初始化
func (t *dairyFarm) Init(stub shim.ChaincodeStubInterface) peer.Response {
	fmt.Println("================初始化奶牛场智能合约================")
	return shim.Success(nil)
}

// 调用智能合约的逻辑函数
func (t *dairyFarm) Invoke(stub shim.ChaincodeStubInterface) peer.Response {

	funcName, args := stub.GetFunctionAndParameters()

	if funcName == "putValue" { //设值
		return t.putValue(stub,args)
	} else if funcName == "getLastValue" { //取值
		return t.getValue(stub,args)
	} else {
		return shim.Error("no such function")
	}
}

// 存储奶牛场的信息
func (t *dairyFarm)putValue(stub shim.ChaincodeStubInterface,args []string)peer.Response  {
	err := stub.PutState(args[0], []byte(args[1]))
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}


// 获取奶牛场的信息
func (t *dairyFarm)getValue(stub shim.ChaincodeStubInterface,args []string)peer.Response  {
	keyValue, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(keyValue)
}


func main() {
	err := shim.Start(new(dairyFarm))
	if err != nil {
		fmt.Printf("启动智能合约失败，错误为:%s", err)
	}
}


