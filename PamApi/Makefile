default:
	@echo "=============building Local API============="
	docker build -f Dockerfile -t api .


up: default
	@echo "=============starting api locally============="
	docker-compose up -d

logs: up
	docker-compose logs -f

down:
	docker-compose down

test:
	go test -v -cover ./...

clean: down
	@echo "=============cleaning up============="
	rm -f server
	docker system prune -f
	docker volume prune -f
