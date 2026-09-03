.PHONY: help install build test backend-test frontend-build backend-run frontend-run clean

help:
	@printf "Available targets:\n"
	@printf "  install          Install frontend dependencies\n"
	@printf "  build            Build backend and frontend\n"
	@printf "  test             Run backend tests\n"
	@printf "  backend-test     Run backend tests only\n"
	@printf "  frontend-build   Build the frontend only\n"
	@printf "  backend-run      Start the Spring Boot API\n"
	@printf "  frontend-run     Start the Vite development server\n"
	@printf "  clean            Remove generated build output\n"

install:
	npm --prefix frontend install

build: backend-test frontend-build

test: backend-test

backend-test:
	mvn -f backend/pom.xml test

frontend-build:
	npm --prefix frontend run build

backend-run:
	mvn -f backend/pom.xml spring-boot:run

frontend-run:
	npm --prefix frontend run dev -- --host 0.0.0.0 --port 5173

clean:
	mvn -f backend/pom.xml clean
	rm -rf frontend/dist
