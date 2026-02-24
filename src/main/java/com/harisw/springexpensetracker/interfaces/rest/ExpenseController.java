package com.harisw.springexpensetracker.interfaces.rest;

import com.harisw.springexpensetracker.application.expense.dto.command.UpdateExpenseCommand;
import com.harisw.springexpensetracker.application.expense.dto.request.CreateExpenseRequest;
import com.harisw.springexpensetracker.application.expense.service.CreateExpenseService;
import com.harisw.springexpensetracker.application.expense.service.DeleteExpenseService;
import com.harisw.springexpensetracker.application.expense.service.GetExpenseService;
import com.harisw.springexpensetracker.application.expense.service.UpdateExpenseService;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.expense.Expense;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {
    private final CreateExpenseService create;
    private final GetExpenseService get;
    private final UpdateExpenseService update;
    private final DeleteExpenseService delete;

    public ExpenseController(CreateExpenseService create, GetExpenseService get, UpdateExpenseService update,
                             DeleteExpenseService delete) {
        this.create = create;
        this.get = get;
        this.update = update;
        this.delete = delete;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expense create(@Valid @RequestBody CreateExpenseRequest req, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return create.create(req.toCommand(), user);
    }

    @GetMapping("/{publicId}")
    public Expense get(@PathVariable UUID publicId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return get.get(publicId, user);
    }

    @GetMapping
    public List<Expense> getAll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return get.getAllByUserId(user);
    }

    @PutMapping("/{publicId}")
    public Expense update(@PathVariable UUID publicId, @Valid @RequestBody CreateExpenseRequest req,
                          Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return update.update(
                new UpdateExpenseCommand(publicId, req.category(), req.description(), req.amount(), req.date()), user);
    }

    @DeleteMapping("/{publicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID publicId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        delete.delete(publicId, user);
    }
}
