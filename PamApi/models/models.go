package models

type Location struct {
	Place     string `json:"place"`
	Division  string `json:"division"`
	Longitude string `json:"longitude"`
	Latitude  string `json:"latitude"`
}

type DTOLocation struct {
	Mac       string `json:"mac"`
	PLace     string `json:"Place"`
	Division  string `json:"division"`
	Longitude string `json:"longitude"`
	Latitude  string `json:"latitude"`
}

type Beacon struct {
	Id   int      `json:"id"`
	Name string   `json:"name"`
	Mac  string   `json:"mac"`
	Rssi int      `json:"rssi"`
	Loc  Location `json:"-"`
}

type DTOBeacon struct {
	IdDevice string `json:"idDevice"`
	Id       int    `json:"id"`
	Name     string `json:"name"`
	Mac      string `json:"mac"`
	Rssi     int    `json:"rssi"`
}

type Master struct {
	ID     string
	Beacon []Beacon
}
