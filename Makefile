test-jdct:
	docker-compose up -d
	gradle build
	docker-compose down

kafka-stop:
	docker-compose down

kafka-start:
	docker-compose up -d