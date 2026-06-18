package com.onlydevs.bookstore.service;

import com.onlydevs.bookstore.endpoint.rest.mapper.CustomerMapper;
import com.onlydevs.bookstore.model.Customer;
import com.onlydevs.bookstore.model.dto.request.CreateCustomerRequest;
import com.onlydevs.bookstore.model.dto.request.UpdateCustomerRequest;
import com.onlydevs.bookstore.model.dto.response.CustomerResponse;
import com.onlydevs.bookstore.model.dto.response.SaleSummaryResponse;
import com.onlydevs.bookstore.model.exception.NotFoundException;
import com.onlydevs.bookstore.repository.CustomerRepository;
import com.onlydevs.bookstore.repository.SaleRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {
  private final CustomerRepository customerRepository;
  private final SaleRepository saleRepository;
  private final CustomerMapper customerMapper;

  @Transactional(readOnly = true)
  public List<CustomerResponse> getAllCustomers() {
    return customerMapper.toRest(customerRepository.findAll());
  }

  @Transactional(readOnly = true)
  public CustomerResponse getCustomerById(UUID id) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
    return customerMapper.toRest(customer);
  }

  @Transactional
  public CustomerResponse createCustomer(CreateCustomerRequest request) {
    Customer customer = customerMapper.toDomain(request);
    Customer saved = customerRepository.save(customer);
    return customerMapper.toRest(saved);
  }

  @Transactional
  public CustomerResponse updateCustomer(UUID id, UpdateCustomerRequest request) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
    customerMapper.updateDomain(customer, request);
    Customer saved = customerRepository.save(customer);
    return customerMapper.toRest(saved);
  }

  @Transactional(readOnly = true)
  public List<SaleSummaryResponse> getCustomerSales(UUID customerId) {
    if (!customerRepository.existsById(customerId)) {
      throw new NotFoundException("Customer not found with id: " + customerId);
    }
    return customerMapper.toSaleSummary(
        saleRepository.findByCustomerIdOrderByCreatedAtDesc(customerId));
  }
}
