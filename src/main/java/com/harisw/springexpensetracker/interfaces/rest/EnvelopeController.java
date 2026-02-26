package com.harisw.springexpensetracker.interfaces.rest;

import com.harisw.springexpensetracker.application.envelope.dto.command.UpdateEnvelopeCommand;
import com.harisw.springexpensetracker.application.envelope.dto.request.CreateEnvelopeRequest;
import com.harisw.springexpensetracker.application.envelope.dto.request.UpdateEnvelopeRequest;
import com.harisw.springexpensetracker.application.envelope.service.CreateEnvelopeService;
import com.harisw.springexpensetracker.application.envelope.service.DeleteEnvelopeService;
import com.harisw.springexpensetracker.application.envelope.service.GetEnvelopeService;
import com.harisw.springexpensetracker.application.envelope.service.UpdateEnvelopeService;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
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
@RequestMapping("/api/envelopes")
@SecurityRequirement(name = "bearerAuth")
public class EnvelopeController {
    private final CreateEnvelopeService create;
    private final GetEnvelopeService get;
    private final UpdateEnvelopeService update;
    private final DeleteEnvelopeService delete;

    public EnvelopeController(CreateEnvelopeService create, GetEnvelopeService get,
                               UpdateEnvelopeService update, DeleteEnvelopeService delete) {
        this.create = create;
        this.get = get;
        this.update = update;
        this.delete = delete;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Envelope create(@Valid @RequestBody CreateEnvelopeRequest req, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return create.create(req.toCommand(), user);
    }

    @GetMapping("/{publicId}")
    public Envelope get(@PathVariable UUID publicId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return get.get(publicId, user);
    }

    @GetMapping
    public List<Envelope> getAll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return get.getAll(user);
    }

    @PutMapping("/{publicId}")
    public Envelope update(@PathVariable UUID publicId, @Valid @RequestBody UpdateEnvelopeRequest req,
                           Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return update.update(new UpdateEnvelopeCommand(publicId, req.name(), req.budget(), req.canNotify()), user);
    }

    @DeleteMapping("/{publicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID publicId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        delete.delete(publicId, user);
    }
}
