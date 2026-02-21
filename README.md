# InterbankPaymentOrchestratorMs
It's the interbank payment orchestrator for Softka

## How to Run the Project

Please follow these steps to start the service:

1. Start the containers:
   ```bash
   docker-compose up -d

2. Create the database
   docker exec -it sqlserver-payments /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "StrongPassword123!" -C \
  -Q "CREATE DATABASE paymentsdb;"

3. - Run the microservice
    ./gradlew bootRun


This way the instructions are clear, formatted, and ready for Markdown. Do you want me to also add a **Prerequisites** section (Java 21, Docker, Gradle)