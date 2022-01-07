package routes

import (
	"github.com/gin-gonic/gin"
	"projects/PAmAPI/controller"
)

func Routes(router *gin.Engine) {

	router.GET("/beacon/:id", controller.GetAllBeacons)
	router.GET("/beacon/:id/:mac", controller.GetBeaconByMac)
	router.POST("/beacon/:id", controller.PostAddBeacon)

	router.GET("/beacon/:id//loc/:mac", controller.GetLocByMac)
	router.POST("/beacon/:id/loc", controller.PostAddLoc)

}
