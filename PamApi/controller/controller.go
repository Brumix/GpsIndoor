package controller

import (
	"PAmAPI/models"
	"PAmAPI/utils"
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
	"time"
)

var master = []models.Master{
	{"RSR1.201013.001", utils.BeaconsGeneric, utils.EdgesGeneric},
}

var MasterId = len(utils.BeaconsGeneric)

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

func DELETELocByMac(context *gin.Context) {

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
		for i := range *result {
			(&(*result)[i]).RecLoc = models.Location{}
		}
		context.JSON(http.StatusOK, models.Location{})
		return
	} else {
		for i, beacon := range *result {
			if beacon.Mac == macBeacon {
				(&(*result)[i]).RecLoc = models.Location{}
				context.JSON(http.StatusOK, models.Location{})
				return
			}
		}
	}
	context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
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

func GETEdges(context *gin.Context) {
	var idUser, exist = checkUser(context)
	if !exist {
		return
	}

	for i, m := range master {
		if m.ID == idUser {
			context.JSON(http.StatusOK, master[i].Edges)
			return
		}
	}
	context.JSON(http.StatusOK, []models.Edges{})
}

func POSTEdges(context *gin.Context) {
	var idUser, exist = checkUser(context)
	if !exist {
		return
	}

	var edge models.Edges
	_ = context.ShouldBind(&edge)

	for i, m := range master {
		if m.ID == idUser {
			for iE := range master[i].Edges {
				var current = &(master)[i].Edges[iE]
				if current.NodeA == edge.NodeA && current.NodeB == edge.NodeB {
					current.Weight = edge.Weight
					context.JSON(http.StatusOK, gin.H{"msg": "edge updated with success"})
					return
				}
			}
			master[i].Edges = append(master[i].Edges, edge)
		}
	}
	context.JSON(http.StatusOK, gin.H{"msg": "edge added with success"})
}

func DELETEEdges(context *gin.Context) {
	var idUser, exist = checkUser(context)
	if !exist {
		return
	}
	macA, existmacA := context.Params.Get("macA")

	if !existmacA {
		for i, m := range master {
			if m.ID == idUser {
				var current = &master[i].Edges
				*current = []models.Edges{}
				context.JSON(http.StatusOK, gin.H{"msg": "edges deleted with success"})
				return
			}
		}
	}
	for i, m := range master {
		if m.ID == idUser {
			var current = &master[i].Edges
			*current = deleteEdge(*current, macA)
			context.JSON(http.StatusOK, gin.H{"msg": "edge Deleted with success"})
			return
		}
	}
	context.JSON(http.StatusBadRequest, gin.H{"msg": "Invalid id"})
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

func deleteEdge(edges []models.Edges, macA string) []models.Edges {
	index := 0
	for _, edge := range edges {
		if edge.NodeA != macA && edge.NodeB != macA {
			edges[index] = edge
			index++
		}
	}
	return edges[:index]
}
