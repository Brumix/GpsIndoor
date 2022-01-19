package utils

import (
	"PAmAPI/models"
	"time"
)

var locBeacon0 = models.Location{Place: "Universidade", Division: "Sala 101", Longitude: "165.98145", Latitude: "990.90015", LocTime: time.Now().Format(models.DateLayout)}
var locBeacon1 = models.Location{Place: "Universidade", Division: "Sala 102", Longitude: "165.98145", Latitude: "710.9099", LocTime: time.Now().Format(models.DateLayout)}
var locBeacon2 = models.Location{Place: "Universidade", Division: "Sala 105", Longitude: "165.98145", Latitude: "431.8938", LocTime: time.Now().Format(models.DateLayout)}
var locBeacon3 = models.Location{Place: "Universidade", Division: "Sala 106", Longitude: "135.98877", Latitude: "114.95654", LocTime: time.Now().Format(models.DateLayout)}
var locBeacon4 = models.Location{Place: "Universidade", Division: "Sala 108", Longitude: "616.9592", Latitude: "114.95654", LocTime: time.Now().Format(models.DateLayout)}
var locBeacon5 = models.Location{Place: "Universidade", Division: "Sala 110", Longitude: "953.96484", Latitude: "114.95654", LocTime: time.Now().Format(models.DateLayout)}
var locBeacon6 = models.Location{Place: "Universidade", Division: "Sala 112", Longitude: "924.96094", Latitude: "413.94214", LocTime: time.Now().Format(models.DateLayout)}
var locBeacon7 = models.Location{Place: "Universidade", Division: "Sala 114", Longitude: "924.96094", Latitude: "717.9375", LocTime: time.Now().Format(models.DateLayout)}

var BeaconsGeneric = []models.Beacon{
	{Name: "beacon-0", Mac: "54-AX-A2-D4-15-89", Rssi: -50,
		RecLoc: locBeacon0, HisLoc: []models.Location{locBeacon0}},
	{Id: 1, Name: "beacon-1", Mac: "34-1C-AF-23-56-B7", Rssi: 12,
		RecLoc: locBeacon1, HisLoc: []models.Location{locBeacon1}},
	{2, "beacon-2", "D7-37-5B-87-49-64", -70,
		locBeacon2, []models.Location{locBeacon2}},
	{3, "beacon-3", "D2-23-C4-N7-15-4N", -35,
		locBeacon3, []models.Location{locBeacon3}},
	{4, "beacon-4", "Z9-15-A5-C2-09-9M", -10,
		locBeacon4, []models.Location{locBeacon4}},
	{5, "beacon-5", "X1-11-B1-Q2-99-5A", -90,
		locBeacon5, []models.Location{locBeacon5}},
	{6, "beacon-6", "Q2-90-T3-P1-10-5Y", -77,
		locBeacon6, []models.Location{locBeacon6}},
	{7, "beacon-7", "B2-10-J3-M1-10-5X", -77,
		locBeacon7, []models.Location{locBeacon7}}}

var EdgesGeneric = []models.Edges{
	{NodeA: "54-AX-A2-D4-15-89", NodeB: "34-1C-AF-23-56-B7", Weight: "10"},
	{NodeA: "34-1C-AF-23-56-B7", NodeB: "D7-37-5B-87-49-64", Weight: "20"},
	{NodeA: "D7-37-5B-87-49-64", NodeB: "D2-23-C4-N7-15-4N", Weight: "14"},
	{NodeA: "D2-23-C4-N7-15-4N", NodeB: "Z9-15-A5-C2-09-9M", Weight: "23"},
	{NodeA: "Z9-15-A5-C2-09-9M", NodeB: "X1-11-B1-Q2-99-5A", Weight: "67"},
	{NodeA: "X1-11-B1-Q2-99-5A", NodeB: "Q2-90-T3-P1-10-5Y", Weight: "8"},
	{NodeA: "Q2-90-T3-P1-10-5Y", NodeB: "B2-10-J3-M1-10-5X", Weight: "1"}}
