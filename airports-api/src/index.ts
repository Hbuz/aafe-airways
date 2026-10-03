import express from "express";
import appRoot from 'app-root-path';
import fs from 'node:fs'
import {Airport, LatitudeDirection, LongitudeDirection} from "./model";

const app = express()
app.disable('etag')

const airportFile = fs.readFileSync(`${appRoot}/src/airports.txt`, 'utf8')
const airports: Airport[] =
    airportFile.split('\n')
        .map(line => {
            return line.split(':')
        })
        .map(line => {
            const airport : Airport = {
                icao: line[0],
                iata: line[1] !== "" ? line[1] : undefined,
                name: line[2],
                location: {
                    city: line[3],
                    country: line[4],
                    coordinates: {
                        latitude: {
                            degrees: Number(line[5]),
                            minutes: Number(line[6]),
                            seconds: Number(line[7]),
                            direction: line[8] as LatitudeDirection
                        },
                        longitude: {
                            degrees: Number(line[9]),
                            minutes: Number(line[10]),
                            seconds: Number(line[11]),
                            direction: line[12] as LongitudeDirection
                        }
                    },
                    altitude: Number(line[13])
                }
            }
            return airport
        })
        .filter(airport => {
            return airport.name != ""
        })

app.get('/airports/:code', (req, res) => {
    const airport = airports.find((element) => {
        return element.iata?.toUpperCase() == req.params.code.toUpperCase()
    })

    // wait for a couple of seconds to simulate a slow api
    setTimeout(() => {
        if (airport) {
            res.status(200)
                .header('Content-Type', 'application/json')
                .json(airport)
        } else {
            res.status(404)
                .json({message: `Airport with code ${req.params.code} was not found!`})
        }
    }, Math.random() * 1000 + 1000)
})

app.get('/airports', (req, res) => {
    const page = parseInt((req.query['page'] ?? '0') as string)
    const size = parseInt((req.query['size'] ?? '1') as string)
    const query = ((req.query['query'] ?? '') as string).toUpperCase()

    if (!query.match(/^[A-Z0-9]+$/) || page < 0 || size < 1 || size > 10) {
        return res.status(404)
            .json({message: `Invalid request`})
    }

    const filteredAirports = airports.filter((element: Airport) => {
            return element.iata?.toUpperCase().includes(query)
                || element.icao?.toUpperCase().includes(query)
                || element.name?.toUpperCase().includes(query)
                || element.location?.city?.toUpperCase().includes(query)
        }) as Airport[]

    setTimeout(() => {
        res.status(200)
            .header('Content-Type', 'application/json')
            .json({
                content: filteredAirports.slice(page * size, (page + 1) * size),
                page: {
                    number: page,
                    size,
                    totalElements: filteredAirports.length,
                    totalPages: Math.ceil(filteredAirports.length / size)

                }
            })
    }, Math.random() * 200 * size + 500)
})

/** Swagger ui */
app.get('/api', (req: express.Request, res: express.Response) => {
    res.sendFile(`${appRoot}/src/api/index.html`);
});
app.use('/swagger-ui', express.static(`${appRoot}/node_modules/swagger-ui-dist`));
app.use('/swagger-initializer.js', express.static(`${appRoot}/src/api/swagger-initializer.js`));
app.use('/airports-api.yaml', express.static(`${appRoot}/src/api/airports-api.yaml`));

app.listen(3000, () => {
    console.log(`Airports API listening on port ${3000}`)
})
