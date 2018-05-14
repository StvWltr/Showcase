package org.educama.customer.api;

import org.educama.customer.api.resource.CustomerListResource;
import org.educama.customer.api.resource.CustomerResource;
import org.educama.customer.api.resource.SaveCustomerResource;
import org.educama.customer.api.resource.assembler.CustomerListResourceAssembler;
import org.educama.customer.api.resource.assembler.CustomerResourceAssembler;
import org.educama.customer.boundary.CustomerBoundaryService;
import org.educama.customer.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.UUID;

/**
 * REST-Service to access Customer resources.
 */
@RestController
@RequestMapping(value = CustomerController.CUSTOMER_RESOURCE_PATH, produces = {MediaType.APPLICATION_JSON_VALUE})
@Transactional
public class CustomerController {
    public static final String CUSTOMER_RESOURCE_PATH = "/educama/v1/customers";

    private final CustomerResourceAssembler customerResourceAssembler;
    private final CustomerListResourceAssembler customerListResourceAssembler;
    private final CustomerBoundaryService customerService;

    @Autowired
    public CustomerController(CustomerResourceAssembler customerResourceAssembler,
                              CustomerListResourceAssembler customerListResourceAssembler,
                              CustomerBoundaryService customerService) {
        this.customerResourceAssembler = customerResourceAssembler;
        this.customerListResourceAssembler = customerListResourceAssembler;
        this.customerService = customerService;
    }

    /**
     * Creates the customer if it does not exist.
     *
     * @param saveCustomerResource Name and address of the customer to create
     * @return The newly created customer
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CustomerResource> createCustomer(
            @Valid @RequestBody SaveCustomerResource saveCustomerResource) {

        if (saveCustomerResource != null) {
            Customer newCustomer = customerService.createCustomer(saveCustomerResource.name, saveCustomerResource.address.toAddress());
            CustomerResource customerResource = customerResourceAssembler.toResource(newCustomer);
            return new ResponseEntity<>(customerResource, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    /**
     * Updates the specified customer.
     *
     * @param saveCustomerResource customer to update
     * @return HttpStatus.OK if updated, otherwise HttpStatus.BAD_REQUEST
     */
    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public ResponseEntity<CustomerResource> updateCustomer(@PathVariable("uuid") UUID uuid,
                                                           @Valid @RequestBody SaveCustomerResource saveCustomerResource) {
        if (saveCustomerResource != null) {
            Customer updatedCustomer = customerService.updateCustomer(uuid, saveCustomerResource.name, saveCustomerResource.address.toAddress());
            CustomerResource customerResource = customerResourceAssembler.toResource(updatedCustomer);
            return new ResponseEntity<>(customerResource, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    /**
     * Deletes the specified customer (by uuid).
     *
     * @param uuid uuid of customer to delete
     */
    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteCustomer(@PathVariable("uuid") UUID uuid) {
        customerService.deleteCustomer(uuid);
    }

    /**
     * Retrieves Customers in a pageable fashion.
     *
     * @param pageable parameter for creating pages
     * @return a collection of all customers
     */
    @RequestMapping(method = RequestMethod.GET)
    public CustomerListResource findAll(Pageable pageable) {
        Page<Customer> page = customerService.findAllCustomers(pageable);
        return customerListResourceAssembler.build(page);
    }

    /**
     * Retrieves a single customer.
     *
     * @param uuid  identified (UUID) of the customer
     * @return the found customer
     */
    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<CustomerResource> findOneByUuid(@PathVariable("uuid") UUID uuid) {
        if (uuid != null) {
            Customer customer = customerService.findCustomerByUuid(uuid);

            CustomerResource customerResource = customerResourceAssembler.toResource(customer);

            return new ResponseEntity<>(customerResource, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Finds all customers with the suggested name.
     *
     * @param term     criteria for suggested names
     * @param pageable parameter for creating pages
     * @return a collection of all customers having the suggested name
     */
    @RequestMapping(value = "/suggestions", method = RequestMethod.GET)
    public CustomerListResource findSuggestions(@RequestParam("term") String term, Pageable pageable) {
        Page<Customer> page = customerService.findSuggestionsForCustomer(term, pageable);
        return customerListResourceAssembler.build(page);
    }
}
