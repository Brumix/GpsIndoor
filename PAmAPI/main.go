package main

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
)

type Beacon struct {
	Id   int    `json:"id"`
	Name string `json:"name"`
	Mac  string `json:"mac"`
	Rssi int    `json:"rssi"`
}

var beacons = []Beacon{
	{0, "beacon-0", "54-AX-A2-D4-15-89", -50},
	{1, "beacon-1", "34-1C-AF-23-56-B7", 12},
	{2, "beacon-2", "D7-37-5B-87-49-64", -70},
	{3, "beacon-3", "F9-48-F4-E7-13-4B", -50}}

var MasterId = len(beacons)

func main() {

	router := gin.Default()

	router.GET("/beacon", func(context *gin.Context) {
		context.JSON(http.StatusOK, beacons)
	})

	router.GET("/beacon/:mac", func(context *gin.Context) {
		macBeacon, existValue := context.Params.Get("mac")
		if !existValue {
			context.JSON(http.StatusOK, gin.H{"msg": "Invalid id"})
			return
		}
		for _, beacon := range beacons {
			if beacon.Mac == macBeacon {
				context.JSON(http.StatusOK, beacon)
				return
			}
		}
		context.JSON(http.StatusOK, gin.H{"msg": "Invalid id"})
	})

	router.POST("/beacon", func(context *gin.Context) {
		var bodyBeacon Beacon
		errDTO := context.ShouldBind(&bodyBeacon)
		if errDTO != nil {
			fmt.Println(errDTO)
			context.JSON(http.StatusOK, gin.H{"msg": "Invalid id"})
			return
		}
		for _, beacon := range beacons {
			if beacon.Mac == bodyBeacon.Mac {
				context.JSON(http.StatusOK, gin.H{"msg": "Invalid id"})
				return
			}
		}

		bodyBeacon.Id = MasterId
		MasterId++
		beacons = append(beacons, bodyBeacon)
		context.JSON(http.StatusOK, gin.H{"msg": "Added with success!!"})
	})

	_ = router.Run()

}
