package org.educama.application.lifecycle;

import org.educama.customer.boundary.CustomerBoundaryService;
import org.educama.customer.model.Address;
import org.educama.customer.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * This lifecycle bean creates demo customers when the application is started..
 */
@Component
public class DemoCustomerLifecycleBean implements SmartLifecycle {
    private final CustomerBoundaryService customerBoundaryService;

    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private boolean running;

    @Autowired
    public DemoCustomerLifecycleBean(CustomerBoundaryService customerBoundaryService) {
        this.customerBoundaryService = customerBoundaryService;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        running = false;
        runnable.run();
    }

    @Override
    public void start() {
        running = true;
        this.createCustomers();
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return 1;
    }

    private void createCustomers() {
        Page<Customer> customers = customerBoundaryService.findAllCustomers(new PageRequest(0, 1));

        if (!customers.hasContent()) {
            logger.info("Creating Demo Customers");

            customerBoundaryService.createCustomer(
                    "Apple",
                    new Address("Apple Street", "1", "12345", "Silicon Valley"));
            customerBoundaryService.createCustomer(
                    "Bayer AG",
                    new Address("Kaiser-Wilhelm-Allee", "1", "51373", "Leverkusen"));
            customerBoundaryService.createCustomer(
                    "Tesa Hamburg",
                    new Address("Heykenaukamp", "10", "21147", "Hamburg"));
            customerBoundaryService.createCustomer(
                    "NovaTec Consulting GmbH",
                    new Address("Dieselstrasse", "18/1", "70771", "Leinfelden-Echterdingen"));
            customerBoundaryService.createCustomer(
                    "Daimler AG (Standort Möhringen)",
                    new Address("Epplestraße", "225", "70567", "Stuttgart"));
            customerBoundaryService.createCustomer(
                    "Continental AG",
                    new Address("Vahrenwalder Str.", "9", "30165", "Hannover"));
        }
    }
}
