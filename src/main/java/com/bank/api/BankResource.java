package com.bank.api;

import com.bank.domain.Customer;
import com.bank.domain.Account;
import com.bank.service.BankTransactionService;
import com.bank.repository.AccountRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/bank")
@Produces(MediaType.APPLICATION_JSON)
public class BankResource {

    @Inject BankTransactionService transactionService;
    @Inject AccountRepository accountRepository;
    @Inject EntityManager em;

    @GET
    @Path("/customers")
    public List<Customer> getAllCustomers() {
        return em.createQuery("SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.accounts", Customer.class).getResultList();
    }

    @GET
    @Path("/accounts/high-value")
    public List<Account> getHighValueAccounts(@QueryParam("min") Double minBalance) {
        if (minBalance == null) minBalance = 1000.0;
        return accountRepository.findHighValueAccountsJPQL(minBalance);
    }

    // Endpoint 1: CMT Transfer
    @POST
    @Path("/transfer/cmt")
    public Response performCmtTransfer(@QueryParam("from") Long fromId, @QueryParam("to") Long toId, @QueryParam("amount") Double amount) {
        try {
            transactionService.transferCMT(fromId, toId, amount);
            return Response.ok("CMT Transfer successful! (@Transactional)").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // Endpoint 2: BMT Transfer
    @POST
    @Path("/transfer/bmt")
    public Response performBmtTransfer(@QueryParam("from") Long fromId, @QueryParam("to") Long toId, @QueryParam("amount") Double amount) {
        try {
            transactionService.transferBMT(fromId, toId, amount);
            return Response.ok("BMT Transfer successful! (UserTransaction)").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
