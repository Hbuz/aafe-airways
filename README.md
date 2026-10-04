# ✈️ AAFE Airways – Fare Calculation Service
This project is a backend web application for **AAFE Airways**, a fictional airline that offers fixed pricing regardless of booking time. 
It consists of two server applications:
- **Airports API**
- **AAFE Fare Engine**

---
## Table of Contents
- [Applications Details](#applications-details)
- [How to Run the Applications](#how-to-run-the-applications)
- [Core Features Implemented](#core-features-implemented)
- [Distance Calculation Strategy](#distance-calculation-strategy)
- [Example Error Response](#example-error-response)
- [API Specifications & Example Requests](#api-specifications--example-requests)
- [Running Tests](#running-tests)

---
## Applications Details

### 1. Airports API
A Node.js microservice exposing data for airports worldwide, including detailed feature metadata used by the Fare Engine Service for calculations.

### 2. AAFE Fare Engine
A Spring Boot service that calculates the total ticket fare for a given itinerary based on distance, stopovers, 
and airport-specific service charges. Required airport data is retrieved directly from the Airports API.

---

## How to Run the Applications

### Prerequisites
- **Java 17+**
- **Maven 3.8+**
- **Node.js 16+** (for running the Airports API)

### Steps

1. **Clone the project:**
``` bash
git clone <repository-url>
cd <project-directory>
```

2. **Start the Airports API:**
``` bash 
cd airports-api 
npm install 
npm run server
```
_Note: Ensure the Airports API is running before starting the Fare Engine._

3. **Build and Run the AAFE Fare Engine:**  
In a new terminal window:
``` bash
cd ../aafe-fare-engine 
mvn clean install
mvn spring-boot:run
```

4. **Access the API:**  
The Fare Engine runs on http://localhost:8080

---

## Core Features Implemented
- **Fare Calculation**:
  - Distance-based pricing (Haversine or Geodesic)
  - Intermediate stopover fees
  - Fixed base fare
  - VAT (21%)
  - Airport-specific service charges
- **Validation**:
  - Minimum of two airports required per itinerary
  - Restrictions on forbidden start/end stations
  - Valid IATA code verification
- **Performance**:
  - In-memory caching of airport data to minimize external API calls
- **Logging**:
  - Detailed internal fare breakdown for auditing and support
  - Explicit logging of distance calculation strategy used
- **Adaptability**:
  - Admin endpoint to refresh airport data dynamically
  - Configuration-driven pricing and rules
---

## Distance Calculation Strategy
By default, the application uses the **Haversine formula** for distance calculation.  

To switch to the high-accuracy **Geodesic method**, pass the argument when launching the Spring Boot application:
```bash 
mvn spring-boot:run -Dspring-boot.run.arguments="--fareengine.distance-method=accurate"
```

---
## Example Error Response
If an itinerary violates airline regulations (e.g., starts at a forbidden station), the API returns a structured error response:
``` json 
{ 
  "error": "Itineraries cannot start in BHD" 
}
```

---
## API Specifications & Example Requests
### `GET /itinerary`
Calculates total fare for a multi-leg or direct itinerary. 
#### Query parameters:
- `airport` _(required, multiple)_: Standard IATA codes representing origin, intermediate stopovers, and destination.

#### Direct flight example:
```http request
GET /itinerary?airport=AMS&airport=LAX
```
#### With stopovers example:
```http request
GET /itinerary?airport=AMS&airport=BCN&airport=LAX
```

### `POST /admin/refresh-stations`
Triggers an immediate cache refresh for all station data from the Airports API.
```http request
POST /admin/refresh-stations
```

---
## Running Tests
To execute unit and integration test suites:
``` bash 
cd aafe-fare-engine
mvn test
```