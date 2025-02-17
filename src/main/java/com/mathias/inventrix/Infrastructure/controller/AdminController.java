package com.mathias.inventrix.Infrastructure.controller;

import com.mathias.inventrix.payload.request.EditUserRequestDto;
import com.mathias.inventrix.payload.request.EmployeeRequest;
import com.mathias.inventrix.payload.request.LocationRequest;
import com.mathias.inventrix.service.PersonService;
import com.mathias.inventrix.service.StockService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PersonService personService;
    private final StockService stockService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-employee")
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeRequest employeeRequest) throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(personService.addEmployee(currentUsername,employeeRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-location")
    public ResponseEntity<?> createLocation(@RequestBody LocationRequest locationRequest) throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(stockService.addLocation(currentUsername,locationRequest));
    }

    @GetMapping("/view-user")
    public ResponseEntity<?> viewUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
       return ResponseEntity.ok(personService.viewUserDetails(currentUsername));
    }

    @PutMapping("/edit-user")
    public ResponseEntity<?> editUser(@RequestBody EditUserRequestDto employeeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(personService.editUser(currentUsername,employeeRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(personService.deleteUser(currentUsername,id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/make-admin")
    public ResponseEntity<?> makeAnAdmin(@RequestParam Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(personService.makeAdmin(currentUsername,id));
    }
}
