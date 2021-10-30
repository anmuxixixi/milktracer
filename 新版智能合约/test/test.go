package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"time"
)


type DairyFarm struct {
	Id          string   `json:"id"`
	Name        string   `json:"name"`        //名称
	CreateTime  string   `json:"createTime"`  //创建时间
	MaxCowNo    uint     `json:"maxCowNo"`    //最大奶牛编号
	MaxBucketNo uint     `json:"maxBucketNo"` //最大牛奶桶编号
	CowIds      []string `json:"cowIds"`      //奶牛ids
	BucketIds   []string `json:"bucketIds"`   //桶ids
}

const (
	intPrefix = "%06d"
	dateFormat = "2006-01-02 15:04:05"  //日期格式
)

func main() {
	cowId := "10086" + fmt.Sprintf(intPrefix, 520)
	fmt.Println(cowId)


	dairyFarm := DairyFarm{
		Id: "119",
		Name: "卫岗",
		CreateTime: time.Now().Format(dateFormat),
		MaxCowNo: 0,
		MaxBucketNo: 0,
		CowIds: []string{},
		BucketIds: []string{},
	}

	fmt.Println(dairyFarm)

	// 将奶牛厂信息转化成json数据
	jsonString, _ := json.Marshal(dairyFarm)

	a := 8

	fmt.Println("json:" + string(jsonString))
	cowId2 := cowId+ strconv.Itoa(a)
	fmt.Println(cowId2)

}
