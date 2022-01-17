package main

import (
	"PAmAPI/routes"
	"github.com/gin-gonic/gin"
)

func main() {

	router := gin.Default()

	routes.Routes(router)

	_ = router.Run()
}
