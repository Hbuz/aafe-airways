export interface Airport {
    icao: string,
    iata?: string,
    name: string,
    location?: Location
}

export interface Location {
    city: string,
    country: string,
    coordinates: Coordinates,
    altitude?: number
}

export interface Coordinates {
    latitude: Latitude,
    longitude: Longitude
}

export interface Latitude {
    degrees: number,
    minutes: number,
    seconds: number,
    direction: LatitudeDirection
}

export type LatitudeDirection = 'N' | 'S'
export interface Longitude {
    degrees: number,
    minutes: number,
    seconds: number,
    direction: LongitudeDirection
}

export type LongitudeDirection = 'E' | 'W'