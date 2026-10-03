package com.aafe.fareengine.controller;

import com.aafe.fareengine.service.StationRegistryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Administrative controller for managing system-level operations.
 * Currently supports refreshing the list of valid airport stations.
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StationRegistryService stationRegistryService;

    @PostMapping("/refresh-stations")
    public ResponseEntity<String> refreshStations() {
        log.info("Refreshing station list via admin endpoint");

        stationRegistryService.refreshStations();
        return ResponseEntity.ok("Station list refreshed.");
    }
}
