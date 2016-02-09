package org.oruko.dictionary.auth.rest;

import org.oruko.dictionary.auth.ApiUser;
import org.oruko.dictionary.auth.ApiUserRepository;
import org.oruko.dictionary.auth.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;

/**
 * Controller for Authentication related endpoints
 * Created by Dadepo Aderemi.
 */
@RestController
@RequestMapping("/v1/auth")
public class AuthApi {

    private ApiUserRepository userRepository;


    /**
     * Endpoint for retrieving metadata information for suggested users
     *
     * @return a {@link ResponseEntity} with the response message
     */
    @RequestMapping(value = "/meta", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getAuthMetaData() {
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("totalUsers", userRepository.count());
        return new ResponseEntity<>(metaData, HttpStatus.OK);
    }



    /**
     * Public constructor for {@link AuthApi}
     * @param userRepository repository for persisting {@link ApiUser}
     */
    @Autowired
    public AuthApi(ApiUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Login endpoint serves as the endpoint to validate a user details is valid and to get any associated
     * needed properties of the user
     * @param principal the {@link java.security.Principal}
     * @return a map of principal properties
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String, Object> login(Principal principal) {
        Map<String, Object> userDetails = new HashMap<>();
        Collection<GrantedAuthority> authorities = ((UsernamePasswordAuthenticationToken) principal).getAuthorities();

        ArrayList<String> roles = authorities.stream().map(auth -> auth.getAuthority())
                                             .collect(Collectors.toCollection(ArrayList::new));

        userDetails.put("roles", roles);
        userDetails.put("username", principal.getName());
        return userDetails;
    }


    /**
     * Endpoint to create user
     */
    @RequestMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> create(@Valid @RequestBody CreateUserRequest createUserRequest,
                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            ArrayList<String> errorMessages = fieldErrors.stream().map(fieldError -> fieldError.getDefaultMessage())
                                                   .collect(Collectors.toCollection(ArrayList::new));

            String messages = String.join(",", errorMessages);

            return new ResponseEntity<>(response(messages), HttpStatus.BAD_REQUEST);
        }

        String username = createUserRequest.getUsername();
        String email = createUserRequest.getEmail();
        String password = createUserRequest.getPassword();
        ArrayList<String> roles = createUserRequest.getRoles();
        roles = roles.stream().map(role -> role.toUpperCase()).collect(Collectors.toCollection(ArrayList::new));

        if (userRepository.findByEmail(email) != null) {
            final String messages = "A user with email " + email + " already exists";
            return new ResponseEntity<>(response(messages), HttpStatus.BAD_REQUEST);
        }

        ApiUser apiUser = new ApiUser();
        apiUser.setEmail(email);
        apiUser.setPassword(password);
        apiUser.setUsername(username);
        apiUser.setRoles(roles.toArray(new String[roles.size()]));
        ApiUser savedUser = userRepository.save(apiUser);

        if (savedUser == null) {
            return new ResponseEntity<>(response("failed"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response("success"), HttpStatus.OK);
    }

    /**
     * End point listing all users
     * @return list of all {@link org.oruko.dictionary.auth.ApiUser}
     */
    @RequestMapping(value = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    public List<ApiUser> getUsers() {
        List<ApiUser> all = userRepository.findAll();
        all.forEach(user -> user.setPassword("xxx"));
        return all;
    }


    private HashMap<String, String> response(String value) {
        HashMap<String, String> response = new HashMap<>();
        response.put("message", value);
        return response;
    }
}