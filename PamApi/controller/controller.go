package controller

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
	"projects/PAmAPI/models"
)

var beaconsGeneric = []models.Beacon{
	{0, "beacon-0", "54-AX-A2-D4-15-89", -50, models.Location{}},
	{1, "beacon-1", "34-1C-AF-23-56-B7", 12, models.Location{}},
	{2, "beacon-2", "D7-37-5B-87-49-64", -70, models.Location{}},
	{3, "beacon-3", "F9-48-F4-E7-13-4B", -50, models.Location{}}}

var master = []models.Master{
	{"RSR1.201013.001", beaconsGeneric},
}

var MasterId = len(beaconsGeneric)

func GetAllBeacons(context *gin.Context) {
	var idUser, exist = checkUser(context)
	if !exist {
		return
	}
	var result = idUserBeacons(idUser)

	context.JSON(http.StatusOK, result)
}

func GetBeaconByMac(context *gin.Context) {

	var idUser, exist = checkUser(context)
	if !exist {
		return
	}
	var result = idUserBeacons(idUser)

	macBeacon, existValue := context.Params.Get("mac")
	if !existValue {
		context.JSON(http.StatusOK, gin.H{"msg": "Invalid id"})
		return
	}
	for _, beacon := range *result {
		if beacon.Mac == macBeacon {
			context.JSON(http.StatusOK, beacon)
			return
		}
	}
	context.JSON(http.StatusOK, gin.H{"msg": "Invalid id"})
}

func PostAddBeacon(context *gin.Context) {

	var idUser, exist = checkUser(context)
	if !exist {
		return
	}
	var result = idUserBeacons(idUser)
	if result != nil {
		master = append(master, models.Master{
			ID: idUser, Beacon: []models.Beacon{},
		})
	}
	result = idUserBeacons(idUser)

	var bodyBeacon models.Beacon
	errDTO := context.ShouldBind(&bodyBeacon)
	if errDTO != nil {
		context.JSON(http.StatusOK, gin.H{"msg": "Invalid id"})
		return
	}
	for _, beacon := range *result {
		if beacon.Mac == bodyBeacon.Mac {
			context.JSON(http.StatusOK, beacon.Loc)
			return
		}
	}
	bodyBeacon.Id = MasterId
	MasterId++
	*result = append(*result, bodyBeacon)
	context.JSON(http.StatusOK, gin.H{"msg": "Added with success!!"})
}
func PostAddLoc(context *gin.Context) {

	var idUser, exist = checkUser(context)
	if !exist {
		return
	}
	var result = idUserBeacons(idUser)

	var loc models.DTOLocation
	errDTO := context.ShouldBind(&loc)
	fmt.Println(loc)
	if errDTO != nil {
		fmt.Println(errDTO)
		context.JSON(http.StatusOK, gin.H{"msg": "Invalid id"})
		return
	}

	for i, beacon := range *result {
		if beacon.Mac == loc.Mac {
			var current = &(*result)[i]
			current.Loc = models.Location{
				Place:     loc.PLace,
				Division:  loc.Division,
				Longitude: loc.Longitude,
				Latitude:  loc.Latitude,
			}
			context.JSON(http.StatusOK, beacon)
			return
		}
	}
	context.JSON(http.StatusOK, gin.H{"msg": "Invalid id"})
}

func GetLocByMac(context *gin.Context) {

	var idUser, exist = checkUser(context)
	if !exist {
		return
	}
	var result = idUserBeacons(idUser)

	macBeacon, existValue := context.Params.Get("mac")
	if !existValue {
		context.JSON(http.StatusOK, gin.H{"msg": "Invalid id"})
		return
	}
	for _, beacon := range *result {
		if beacon.Mac == macBeacon {
			context.JSON(http.StatusOK, beacon.Loc)
			return
		}
	}
}

func idUserBeacons(id string) *[]models.Beacon {
	for i, m := range master {
		if m.ID == id {
			return &master[i].Beacon
		}
	}
	return nil
}

func checkUser(context *gin.Context) (string, bool) {
	idUser, existValueId := context.Params.Get("id")
	if !existValueId {
		context.JSON(http.StatusOK, gin.H{"msg": "Invalid id"})
		return "", false
	}
	return idUser, true
}
