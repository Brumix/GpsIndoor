FROM golang:1.17

WORKDIR /go/src

COPY . .

RUN ["go", "get", "github.com/githubnemo/CompileDaemon"]

EXPOSE 8080

ENTRYPOINT CompileDaemon -log-prefix=false -build="go build -o main" -command="./main"
