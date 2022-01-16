package models

const (
	DateLayout = "2006-Jan-02 Monday 03:04:05"
)

type Location struct {
	Place     string `json:"place"`
	Division  string `json:"division"`
	Longitude string `json:"longitude"`
	Latitude  string `json:"latitude"`
	LocTime   string `json:"loc_time"`
}

type DTOLocation struct {
	Mac       string `json:"mac"`
	PLace     string `json:"place"`
	Division  string `json:"division"`
	Longitude string `json:"longitude"`
	Latitude  string `json:"latitude"`
	LocTime   string `json:"loc_time"`
}

type Beacon struct {
	Id     int        `json:"id"`
	Name   string     `json:"name"`
	Mac    string     `json:"mac"`
	Rssi   int        `json:"rssi"`
	RecLoc Location   `json:"-"`
	HisLoc []Location `json:"-"`
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
