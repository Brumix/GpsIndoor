package controller

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
	"projects/PAmAPI/models"
	"time"
)

var locBeacon0 = models.Location{Place: "Universidade ", Division: "Sala 101", Longitude: "540", Latitude: "540", LocTime: time.Now().Format(models.DateLayout)}
var locBeacon1 = models.Location{Place: "Universidade", Division: "Sala 102", Longitude: "360", Latitude: "360", LocTime: time.Now().Format(models.DateLayout)}

var beaconsGeneric = []models.Beacon{
	{Name: "beacon-0", Mac: "54-AX-A2-D4-15-89", Rssi: -50,
		RecLoc: locBeacon0, HisLoc: []models.Location{locBeacon0}},
	{Id: 1, Name: "beacon-1", Mac: "34-1C-AF-23-56-B7", Rssi: 12,
		RecLoc: locBeacon1, HisLoc: []models.Location{locBeacon1}},
	{2, "beacon-2", "D7-37-5B-87-49-64", -70, models.Location{}, []models.Location{}},
	{3, "beacon-3", "F9-48-F4-E7-13-4B", -50, models.Location{}, []models.Location{}}}

var master = []models.Master{
	{"RSR1.201013.001", beaconsGeneric},
}

var MasterId = len(beaconsGeneric)

func GETCreateUser(context *gin.Context) {
	var idUser, exist = checkUser(context)
	if !exist {
		return
	}
	var _, havaSomething = idUserBeacons(idUser)
	if havaSomething {
		return
	}
	master = append(master, models.Master{
		ID: idUser, Beacon: []models.Beacon{},
	})
}

func GetAllBeacons(context *gin.Context) {
	var idUser, exist = checkUser(context)
	if !exist {
		return
	}
	var result, havaSomething = idUserBeacons(idUser)
	if !havaSomething {
		context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
		return
	}

	context.JSON(http.StatusOK, result)
}

func GetBeaconByMac(context *gin.Context) {

	var idUser, exist = checkUser(context)
	if !exist {
		return
	}

	var result, havaSomething = idUserBeacons(idUser)
	if !havaSomething {
		context.JSON(http.StatusOK, gin.H{"msg": "Invalid id"})
		return
	}

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
	var result, havaSomething = idUserBeacons(idUser)
	if !havaSomething {
		context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
		return
	}

	if result != nil {
		master = append(master, models.Master{
			ID: idUser, Beacon: []models.Beacon{},
		})
	}
	result, _ = idUserBeacons(idUser)

	var bodyBeacon models.Beacon
	errDTO := context.ShouldBind(&bodyBeacon)
	if errDTO != nil {
		context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
		return
	}
	for _, beacon := range *result {
		if beacon.Mac == bodyBeacon.Mac {
			context.JSON(http.StatusOK, beacon.RecLoc)
			return
		}
	}
	bodyBeacon.Id = MasterId
	MasterId++
	*result = append(*result, bodyBeacon)
	context.JSON(http.StatusBadRequest, gin.H{"msg": "Added with success!!"})
}

func PostAddLoc(context *gin.Context) {

	var idUser, exist = checkUser(context)
	if !exist {
		return
	}
	var result, havaSomething = idUserBeacons(idUser)
	if !havaSomething {
		context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
		return
	}

	var loc models.DTOLocation
	errDTO := context.ShouldBind(&loc)
	fmt.Println(loc)
	if errDTO != nil {
		fmt.Println(errDTO)
		context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
		return
	}

	for i, beacon := range *result {
		if beacon.Mac == loc.Mac {
			var current = &(*result)[i]
			loc := models.Location{
				Place:     loc.PLace,
				Division:  loc.Division,
				Longitude: loc.Longitude,
				Latitude:  loc.Latitude,
				LocTime:   time.Now().Format(models.DateLayout),
			}
			current.RecLoc = loc
			current.HisLoc = append(current.HisLoc, loc)
			context.JSON(http.StatusOK, beacon)
			return
		}
	}
	context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
}

func GetAllLocByMac(context *gin.Context) {
	var idUser, exist = checkUser(context)
	if !exist {
		return
	}
	var result, havaSomething = idUserBeacons(idUser)
	if !havaSomething {
		context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
		return
	}

	var dto []models.DTOLocation
	var currentDto models.DTOLocation
	for _, beacon := range *result {
		if beacon.RecLoc.Place != "" && beacon.RecLoc.Division != "" {
			currentDto.Mac = beacon.Mac
			currentDto.PLace = beacon.RecLoc.Place
			currentDto.Division = beacon.RecLoc.Division
			currentDto.Longitude = beacon.RecLoc.Longitude
			currentDto.Latitude = beacon.RecLoc.Latitude
			currentDto.LocTime = beacon.RecLoc.LocTime

			dto = append(dto, currentDto)
		}
	}
	if dto == nil {
		dto = []models.DTOLocation{}
	}

	context.JSON(http.StatusOK, dto)

}

func GetLocByMac(context *gin.Context) {

	var idUser, exist = checkUser(context)
	if !exist {
		return
	}
	var result, havaSomething = idUserBeacons(idUser)
	if !havaSomething {
		context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
		return
	}

	macBeacon, existValue := context.Params.Get("mac")
	if !existValue {
		context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
		return
	}
	for _, beacon := range *result {
		if beacon.Mac == macBeacon {
			context.JSON(http.StatusOK, beacon.RecLoc)
			return
		}
	}
}

func GETHisLoc(context *gin.Context) {

	var idUser, exist = checkUser(context)
	if !exist {
		return
	}
	var result, havaSomething = idUserBeacons(idUser)
	if !havaSomething {
		context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
		return
	}

	macBeacon, existValue := context.Params.Get("mac")
	if !existValue {
		context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
		return
	}

	for _, beacon := range *result {
		if beacon.Mac == macBeacon {
			if beacon.HisLoc == nil {
				context.JSON(http.StatusOK, []models.DTOLocation{})
				return
			}
			context.JSON(http.StatusOK, reverseDto(beacon.HisLoc))
			return
		}
	}
	context.JSON(http.StatusOK, []models.DTOLocation{})
	return
}

func idUserBeacons(id string) (*[]models.Beacon, bool) {
	for i, m := range master {
		if m.ID == id {
			return &master[i].Beacon, true
		}
	}
	return nil, false
}

func checkUser(context *gin.Context) (string, bool) {
	idUser, existValueId := context.Params.Get("id")
	if !existValueId {
		context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
		return "", false
	}
	return idUser, true
}

func reverseDto(input []models.Location) []models.Location {
	if len(input) == 0 {
		return input
	}
	return append(reverseDto(input[1:]), input[0])
}
