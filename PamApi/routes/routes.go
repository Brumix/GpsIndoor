package routes

import (
	"github.com/gin-gonic/gin"
	"projects/PAmAPI/controller"
)

func Routes(router *gin.Engine) {

	router.GET("/beacon/user/:id", controller.GETCreateUser)
	router.GET("/beacon/:id", controller.GetAllBeacons)
	router.GET("/beacon/:id/:mac", controller.GetBeaconByMac)
	router.POST("/beacon/:id", controller.PostAddBeacon)

	router.GET("/beacon/:id/loc", controller.GetAllLocByMac)
	router.GET("/beacon/:id/loc/:mac", controller.GetLocByMac)
	router.DELETE("/beacon/:id/loc/:mac", controller.DELETELocByMac)
	router.DELETE("/beacon/:id/loc", controller.DELETELocByMac)

	router.POST("/beacon/:id/loc", controller.PostAddLoc)
	router.GET("/beacon/:id/loc/hist/:mac", controller.GETHisLoc)

	router.GET("/beacon/:id/edge", controller.GETEdges)
	router.POST("/beacon/:id/edge", controller.POSTEdges)
}
