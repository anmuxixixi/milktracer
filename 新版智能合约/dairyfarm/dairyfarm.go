// @author:来瓶安慕嘻
// @time:2021-10-27 21:37:00
// @file:dairyfarm.go
// @开始美好的一天吧 ('μ')

package main

import (
	"encoding/json"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"strconv"
	"strings"
	"time"
)

// 奶牛场的智能合约
type DairyFarmChaincode struct {
}

//奶牛
type Cow struct {
	Id         string `json:"id"`
	FarmId     string `json:"farmId"`     //奶牛场id
	Healthy    bool   `json:"healthy"`    //健康状态
	Quarantine bool   `json:"quarantine"` //检疫状态
	FeedSource string `json:"feedSource"` //食物来源
	Status     int8   `json:"status"`     //状态
}

//奶牛场
type DairyFarm struct {
	Id          string   `json:"id"`
	Name        string   `json:"name"`        //名称
	CreateTime  string   `json:"createTime"`  //创建时间
	MaxCowNo    uint     `json:"maxCowNo"`    //最大奶牛编号
	MaxBucketNo uint     `json:"maxBucketNo"` //最大牛奶桶编号
	CowIds      []string `json:"cowIds"`      //奶牛ids
	BucketIds   []string `json:"bucketIds"`   //桶ids
}

//奶牛桶
type Bucket struct {
	Id              string `json:"id"`
	MachiningId     string `json:"machiningId"`     //加工场id
	Time            string `json:"time"`            //装桶时间
	InMachiningTime string `json:"inMachiningTime"` //进入加工场时间
	Status          int8   `json:"status"`          //状态 0待签收，1确认，2拒绝
}

//奶牛操作
type CowOperation struct {
	CowId               string `json:"cowId"`               //奶牛id
	Time                string `json:"time"`                //时间
	Operation           int8   `json:"operation"`           // 操作类型 0为挤奶 1为喂养 2为检疫
	ConsumptionOrOutput string `json:"consumptionOrOutput"` //消耗或产出
}

const (
	channelName            = "amxchannel" //通道名
	machiningChaincodeName = "machining"            // 加工厂chaincode名
	operationSuffix        = "-opr"                 //奶牛操作后缀
	intPrefix              = "%06d"                 //6位数值，不足前面补0
	dateFormat              = "2006-01-02 15:04:05"  //日期格式
	combinedConstruction   = "machiningId~buskerId" //组合键
)

func (t *DairyFarmChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

func (t *DairyFarmChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {

	fn, args := stub.GetFunctionAndParameters()

	fmt.Printf("方法: %s  参数 ： %s \n", fn, args)

	if fn == "addDairyFarm" {
		return t.addDairyFarm(stub, args)
	} else if fn == "addCow" {
		return t.addCow(stub, args)
	} else if fn == "delCow" {
		return t.delCow(stub, args)
	} else if fn == "addCowOperate" {
		return t.addCowOperate(stub, args)
	} else if fn == "addCowMilking" {
		return t.addCowMilking(stub, args)
	} else if fn == "sentProcess" {
		return t.sentProcess(stub, args)
	} else if fn == "checkBucketForMachining" {
		return t.checkBucketForMachining(stub, args)
	} else if fn == "confirmBucket" {
		return t.confirmBucket(stub, args)
	} else if fn == "getOperationHistory" {
		return t.getOperationHistory(stub, args)
	} else if fn == "get" {
		return t.get(stub, args)
	} else if fn == "set" {
		return t.set(stub, args)
	}
	return shim.Error("Machining No operation:" + fn)

}


//添加奶牛场
func (t *DairyFarmChaincode) addDairyFarm(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 2 {
		return shim.Error("参数出错")
	}
	id := args[0]
	name := args[1]

	// 先根据奶牛场ID判断奶牛场是否存在
	if isExisted(stub, id) {
		return shim.Error("已存在")
	}

	dairyFarm := DairyFarm{
		Id: id,
		Name: name,
		CreateTime: time.Now().Format(dateFormat),
		MaxCowNo: 0,
		MaxBucketNo: 0,
		CowIds: []string{},
		BucketIds: []string{},
	}

	// 将奶牛厂信息转化成json数据
	jsonString, err := json.Marshal(dairyFarm)

	fmt.Println("json:" + string(jsonString))

	if err != nil {
		return shim.Error("json序列化失败")
	}

	err = stub.PutState(id, []byte(jsonString))

	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(jsonString)

}

//根据id获取牛奶场
func (t *DairyFarmChaincode) getDairyFarmById(stub shim.ChaincodeStubInterface, id string) (DairyFarm, error) {
	dvalue, err := stub.GetState(id)
	var d DairyFarm
	err = json.Unmarshal([]byte(dvalue), &d)
	return d, err
}

//添加奶牛
func (t *DairyFarmChaincode) addCow(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		return shim.Error("参数出错")
	}

	cowStr := args[0]

	var cow Cow
	//这里就是实际的解码和相关的错误检查
	if err := json.Unmarshal([]byte(cowStr), &cow); err != nil {
		return shim.Error("json反序列化失败")
	}

	dairyFarm, err := t.getDairyFarmById(stub, cow.FarmId)
	if err != nil {
		return shim.Error("奶牛场不存在")
	}

	dairyFarm.MaxCowNo += 1

	cowId := cow.FarmId + fmt.Sprintf(intPrefix, dairyFarm.MaxCowNo)

	cowIds := dairyFarm.CowIds
	dairyFarm.CowIds = append(cowIds, cowId)

	//更新牛奶场
	jsonString, err := json.Marshal(dairyFarm)
	if err != nil {
		return shim.Error("json序列化失败")
	}

	err = stub.PutState(cow.FarmId, []byte(jsonString))
	if err != nil {
		return shim.Error(err.Error())
	}

	fmt.Println("json:" + string(jsonString))
	//添加奶牛
	cow.Id = cowId

	jsonString, err = json.Marshal(cow)
	if err != nil {
		return shim.Error("json序列化失败")
	}

	err = stub.PutState(cowId, []byte(jsonString))
	if err != nil {
		return shim.Error(err.Error())
	}

	fmt.Println("json:" + string(jsonString))
	return shim.Success(jsonString)

}

//删除奶牛
func (t *DairyFarmChaincode) delCow(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		return shim.Error("参数出错")
	}

	cowId := args[0]

	cowStr, err := stub.GetState(cowId)
	if err != nil {
		return shim.Error(err.Error())
	}

	var cow Cow
	//这里就是实际的解码和相关的错误检查
	if err := json.Unmarshal([]byte(cowStr), &cow); err != nil {
		return shim.Error("json反序列化失败")
	}
	//设置死亡
	cow.Status = 1

	cowStr, err = json.Marshal(cow)
	if err != nil {
		return shim.Error("json序列化失败")
	}

	err = stub.PutState(cowId, cowStr)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("json:", string(cowStr))
	return shim.Success(nil)

}

//添加奶牛操作
func (t *DairyFarmChaincode) addCowOperate(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		return shim.Error("参数出错")
	}

	operationStr := args[0]

	var operation CowOperation
	//这里就是实际的解码和相关的错误检查
	if err := json.Unmarshal([]byte(operationStr), &operation); err != nil {
		return shim.Error("json反序列化失败")
	}
	operation.Time = time.Now().Format(dateFormat)

	cowId := operation.CowId

	operationJson, err := json.Marshal(operation)
	if err != nil {
		return shim.Error("json序列化失败")
	}
	fmt.Println("json:" + string(operationJson))

	err = stub.PutState(cowId+"-"+strconv.Itoa(int(operation.Operation)), []byte(operationJson))

	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(operationJson)

}

//添加挤奶
func (t *DairyFarmChaincode) addCowMilking(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		return shim.Error("参数出错")
	}

	operationStr := args[0]

	var operation CowOperation
	//这里就是实际的解码和相关的错误检查
	if err := json.Unmarshal([]byte(operationStr), &operation); err != nil {
		return shim.Error("json反序列化失败")
	}
	operation.Time = time.Now().Format(dateFormat)
	operation.Operation = 0

	cowId := operation.CowId

	operationJson, err := json.Marshal(operation)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("json:" + string(operationJson))

	err = stub.PutState(cowId+operationSuffix, []byte(operationJson))
	if err != nil {
		return shim.Error(err.Error())
	}

	ids := cowId[0:4]
	dairyFarmId := ids

	dairyFarm, err := t.getDairyFarmById(stub, dairyFarmId)
	if err != nil {
		return shim.Error("奶牛场不存在")
	}

	dairyFarm.MaxBucketNo += 1

	bucketId := cowId + fmt.Sprintf(intPrefix, dairyFarm.MaxBucketNo)

	bucketIds := dairyFarm.BucketIds
	dairyFarm.BucketIds = append(bucketIds, bucketId)

	//跟新牛奶场
	jsonString, err := json.Marshal(dairyFarm)
	if err != nil {
		return shim.Error("json序列化失败")
	}
	err = stub.PutState(dairyFarmId, []byte(jsonString))
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("json:" + string(jsonString))

	//添加奶牛桶
	bucket := Bucket{}
	bucket.Id = bucketId
	bucket.Time = time.Now().Format(dateFormat)
	bucket.Status = 0

	jsonString, err = json.Marshal(bucket)
	if err != nil {
		return shim.Error("json序列化失败")
	}
	err = stub.PutState(bucketId, []byte(jsonString))
	if err != nil {
		return shim.Error(err.Error())
	}

	fmt.Println("json:" + string(jsonString))

	return shim.Success(jsonString)

}

//发送到加工厂
func (t *DairyFarmChaincode) sentProcess(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 2 {
		return shim.Error("参数出错")
	}

	bucketId := args[0]
	machiningId := args[1]

	bucketStr, err := stub.GetState(bucketId)
	if err != nil {
		return shim.Error(err.Error())
	}
	var bucket Bucket
	//这里就是实际的解码和相关的错误检查
	if err := json.Unmarshal([]byte(bucketStr), &bucket); err != nil {
		return shim.Error("json反序列化失败")
	}
	bucket.MachiningId = machiningId
	bucket.InMachiningTime = time.Now().Format(dateFormat)

	bucketStr, err = json.Marshal(bucket)
	if err != nil {
		return shim.Error("json序列化失败")
	}

	err = stub.PutState(bucketId, bucketStr)

	if err != nil {
		return shim.Error(err.Error())
	}

	//添加工厂查询奶桶临时组合建
	indexKey, err := stub.CreateCompositeKey(combinedConstruction, []string{machiningId, bucketId})
	err = stub.PutState(indexKey, bucketStr)
	if err != nil {
		return shim.Error(err.Error())
	}

	fmt.Println("json:", string(bucketStr))
	return shim.Success(nil)

}

//工厂查询奶桶状态
func (t *DairyFarmChaincode) checkBucketForMachining(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		return shim.Error("参数出错")
	}

	machiningId := args[0]

	var buckets = make([]Bucket, 0)

	resultIterator, err := stub.GetStateByPartialCompositeKey(combinedConstruction, []string{machiningId})
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultIterator.Close()
	for resultIterator.HasNext() {
		item, err := resultIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		var bucket Bucket
		//这里就是实际的解码和相关的错误检查
		if err := json.Unmarshal(item.Value, &bucket); err != nil {
			return shim.Error("json反序列化失败")
		}

		buckets = append(buckets, bucket)

	}

	jsonStr, err := json.Marshal(buckets)
	if err != nil {
		return shim.Error("json序列化失败")
	}
	fmt.Println("json:", string(jsonStr))
	return shim.Success(jsonStr)

}

//确认奶桶状态
func (t *DairyFarmChaincode) confirmBucket(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 3 {
		return shim.Error("参数出错")
	}

	buckerId := args[0]
	machiningId := args[1]
	isConfirm := args[2]

	resultIterator, err := stub.GetStateByPartialCompositeKey(combinedConstruction, []string{machiningId, buckerId})
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultIterator.Close()

	var bucket Bucket

	var indexKey string

	for resultIterator.HasNext() {
		item, err := resultIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}

		indexKey = item.Key
		//这里就是实际的解码和相关的错误检查
		if err := json.Unmarshal(item.Value, &bucket); err != nil {
			return shim.Error("json反序列化失败")
		}

	}

	status, err := strconv.Atoi(isConfirm)
	if err != nil {
		return shim.Error(err.Error())
	}

	bucket.Status = int8(status)

	jsonStr, err := json.Marshal(bucket)
	if err != nil {
		return shim.Error("json序列化失败")
	}
	fmt.Println("json:", string(jsonStr))

	//跟新bucket
	err = stub.PutState(buckerId, jsonStr)

	if err != nil {
		return shim.Error(err.Error())
	}

	//确认签收向工厂添加奶桶
	if isConfirm == "1" {
		response := stub.InvokeChaincode(machiningChaincodeName, [][]byte{[]byte("addBucket"), []byte(jsonStr)}, channelName)
		if response.Status != shim.OK {
			errStr := fmt.Sprintf("Failed to query chaincode. Got error: %s", response.Payload)
			fmt.Printf(errStr)
			return shim.Error(errStr)
		}
	}

	//删除组合键
	err = stub.DelState(indexKey)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(jsonStr)

}


func (t *DairyFarmChaincode) get(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("参数出错")
	}

	keys := strings.Split(args[0],",")

	var dmap = make(map[string]map[string]interface{})

	for i := 0; i < len(keys); i++ {
		key := keys[i]
		//读出
		value, err := stub.GetState(key)
		if err != nil {
			return shim.Error(err.Error())
		}

		if value == nil {
			dmap[key] = nil
		} else {
			var imap map[string]interface{}
			//这里就是实际的解码和相关的错误检查
			if err := json.Unmarshal([]byte(value), &imap); err != nil {
				return shim.Error("json反序列化失败")
			}
			dmap[key] = imap
		}
	}

	jsonStr, err := json.Marshal(dmap)
	if err != nil {
		return shim.Error("json序列化失败")
	}

	fmt.Println("json:", string(jsonStr))
	return shim.Success(jsonStr)
}


func (t *DairyFarmChaincode) set(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 2 {
		return shim.Error("要输入一个键")
	}

	err := stub.PutState(args[0], []byte(args[1]))
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

//获取操作记录
func (t *DairyFarmChaincode) getOperationHistory(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		return shim.Error("parm error")
	}
	id := args[0]
	cowId := id[0:10]
	keysIter, err := stub.GetHistoryForKey(cowId + operationSuffix)

	if err != nil {
		return shim.Error(fmt.Sprintf("GetHistoryForKey failed. Error accessing state: %s", err))
	}
	defer keysIter.Close()

	var historys = make([]CowOperation, 0)

	for keysIter.HasNext() {

		response, iterErr := keysIter.Next()
		if iterErr != nil {
			return shim.Error(fmt.Sprintf("GetHistoryForKey operation failed. Error accessing state: %s", err))
		}
		//交易的值
		txvalue := response.Value

		var operation CowOperation
		//这里就是实际的解码和相关的错误检查
		if err := json.Unmarshal(txvalue, &operation); err != nil {
			return shim.Error("json反序列化失败")
		}
		historys = append(historys, operation)
	}

	jsonKeys, err := json.Marshal(historys)
	if err != nil {
		return shim.Error(fmt.Sprintf("query operation failed. Error marshaling JSON: %s", err))
	}
	fmt.Println("json:", string(jsonKeys))
	return shim.Success(jsonKeys)
}

func isExisted(stub shim.ChaincodeStubInterface, key string) bool {
	val, err := stub.GetState(key)
	if err != nil {
		fmt.Printf("Error: %s\n", err)
	}

	if len(val) == 0 {
		return false
	}

	return true
}

func main() {
	err := shim.Start(new(DairyFarmChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}