# ✈️ AAFE Airways – Fare Calculation Service
This project is a backend web application for **AAFE Airways**, a fictional airline that offers fixed pricing regardless of booking time. <br> The application calculates the total ticket fare for a given itinerary based on distance, stopovers, and airport-specific service charges.

---
## Table of Contents
- How to Run the Application
- Core Features Implemented
- Distance Calculation Strategy
- Example Error Response
- API Specification
- Running Tests

---
## How to Run the Application 🚀
### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js (for running the Airports API)
### Steps

1. **Clone the project**: <br>
Clone the project into a directory of your choice.


2. **Start the Airports API**:
``` bash 
cd airports-api 
npm install 
npm run server
```
3. **Build the project**:
``` bash
cd ../aafe-fare-engine 
mvn clean install
```
4. **Run the application**:
```bash 
mvn spring-boot:run
```
5. **Access the API**: <br>
http://localhost:8080/itinerary?airport=AMS&airport=LAX
---

## Core Features Implemented
- **Fare Calculation**:
  - Distance-based pricing (Haversine or Geodesic)
  - Intermediate stopover fees
  - Fixed base fare
  - VAT (21%)
  - Airport-specific service charges
- **Validation**:
  - Minimum of two airports
  - Forbidden start/end stations
  - Valid IATA codes only
- **Performance**:
  - Caching of airport data to reduce external API calls
- **Logging**:
  - Internal-only fare breakdown for auditing and support
  - Strategy used for distance calculation
- **Adaptability**:
  - Admin endpoint to refresh airport data dynamically
  - Configuration-driven pricing and rules
---
## Distance Calculation Strategy
By default, the application uses the **Haversine formula** for distance calculation. <br> 
To switch to the more accurate **Geodesic method**, run the application the command:
```bash 
mvn spring-boot:run -Dspring-boot.run.arguments="--fareengine.distance-method=accurate"
```

---
## Example Error Response
If an itinerary violates airline regulations (e.g., starts in a forbidden station), the API returns a clear error message:
``` json 
{ 
  "error": "Itineraries cannot start in BHD" 
}
```

---
## API Specifications
to be added...

---
## Running Tests
To run unit and integration tests:
``` bash 
mvn test
```
---
##  Example Requests
- Direct flight:
```
GET /itinerary?airport=AMS&airport=LAX
```
- With stopovers:
```
GET /itinerary?airport=AMS&airport=BCN&airport=LAX
```
- Admin refresh (Extra):
```
POST /admin/refresh-stations 
```
