package com.umcsuser.carrent.web;

import com.umcsuser.carrent.dto.RentalRequest;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.services.RentalService;
import com.umcsuser.carrent.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalsController {
    private final RentalService rentalService;
    private final UserService userService;

    public RentalsController(RentalService rentalService, UserService userService) {
        this.rentalService = rentalService;
        this.userService = userService;
    }

    @GetMapping
    public List<Rental> list() {
        return rentalService.findAllRentals();
    }

    @GetMapping("/my")
    public List<Rental> userRentals(@AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        User user = userService.findByLogin(login);
        return rentalService.findUserRentals(user.getId());
    }

    @PostMapping("/rent")
    public ResponseEntity<Rental> rent(
            @RequestBody RentalRequest rentalRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String login = userDetails.getUsername();
        User user = userService.findByLogin(login);
        Rental rental = rentalService.rentVehicle(user.getId(), rentalRequest.vehicleId());
        return ResponseEntity.ok(rental);
    }

    @PostMapping("/return")
    public ResponseEntity<Rental> returnVehicle(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String login = userDetails.getUsername();
        User user = userService.findByLogin(login);
        Rental rental = rentalService.returnVehicle(user.getId());
        return ResponseEntity.ok(rental);
    }
}