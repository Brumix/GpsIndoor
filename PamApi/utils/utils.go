package utils

import (
	"PAmAPI/models"
	"time"
)

var locBeacon0 = models.Location{Place: "Universidade ", Division: "Sala 101", Longitude: "165.98145", Latitude: "847.9131", LocTime: time.Now().Format(models.DateLayout)}
var locBeacon1 = models.Location{Place: "Universidade", Division: "Sala 102", Longitude: "161.99341", Latitude: "990.90015", LocTime: time.Now().Format(models.DateLayout)}

var edge = models.Edges{
	NodeA:  "54-AX-A2-D4-15-89",
	NodeB:  "34-1C-AF-23-56-B7",
	Weight: "10",
}

var EdgesGeneric = []models.Edges{edge}
var BeaconsGeneric = []models.Beacon{
	{Name: "beacon-0", Mac: "54-AX-A2-D4-15-89", Rssi: -50,
		RecLoc: locBeacon0, HisLoc: []models.Location{locBeacon0}},
	{Id: 1, Name: "beacon-1", Mac: "34-1C-AF-23-56-B7", Rssi: 12,
		RecLoc: locBeacon1, HisLoc: []models.Location{locBeacon1}},
	{2, "beacon-2", "D7-37-5B-87-49-64", -70, models.Location{}, []models.Location{}},
	{3, "beacon-3", "F9-48-F4-E7-13-4B", -50, models.Location{}, []models.Location{}}}
