package main

import (
	"github.com/gin-gonic/gin"
	"projects/PAmAPI/routes"
)

func main() {

	router := gin.Default()

	routes.Routes(router)

	_ = router.Run()
}
