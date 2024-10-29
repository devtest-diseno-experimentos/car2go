package com.pe.platform.payment.interfaces.rest;

import com.pe.platform.payment.domain.model.aggregates.Subscription;
import com.pe.platform.payment.domain.model.commands.UpdateSubscriptionCommand;
import com.pe.platform.payment.domain.model.queries.GetSubscriptionByIdQuery;
import com.pe.platform.payment.domain.services.SubscriptionCommandService;
import com.pe.platform.payment.domain.services.SubscriptionQueryService;
import com.pe.platform.payment.interfaces.rest.resources.CreateSubscriptionResource;
import com.pe.platform.payment.interfaces.rest.resources.SubscriptionResource;
import com.pe.platform.payment.interfaces.rest.transform.CreateSubscriptionCommandFromResourceAssembler;
import com.pe.platform.payment.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(value="/api/v1/subscription", produces = MediaType.APPLICATION_JSON_VALUE)

public class SubscriptionController {
    private final SubscriptionQueryService subscriptionQueryService;
    private final SubscriptionCommandService subscriptionCommandService;

    public SubscriptionController(SubscriptionQueryService subscriptionQueryService,
                                  SubscriptionCommandService subscriptionCommandService){
        this.subscriptionQueryService = subscriptionQueryService;
        this.subscriptionCommandService = subscriptionCommandService;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResource> createSubscription(@RequestBody CreateSubscriptionResource resource){
        Optional<Subscription> subscription = subscriptionCommandService.handle(CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource));
        return subscription.map(resp ->
                        new ResponseEntity<>(SubscriptionResourceFromEntityAssembler
                                .toResourceFromEntity(resp), CREATED))
                .orElseGet(()-> ResponseEntity.badRequest().build());
    }

    /*
    @GetMapping("/all")
    public ResponseEntity<List<SubscriptionResource>> getAllSubscription() {
        var subscriptions = subscriptionQueryService.handle(new GetAllSubscriptionQuery());
        if(subscriptions.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        var subscriptionResources = subscriptions.stream().map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(subscriptionResources);
    }
    */


    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResource> getSubscriptionById(@PathVariable int id) {
        var getSubscriptionByIdQuery = new GetSubscriptionByIdQuery((long) id);
        var subscription = subscriptionQueryService.handle(getSubscriptionByIdQuery);
        if(subscription.isEmpty()) return ResponseEntity.notFound().build();
        var subscriptionResource = SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription.get());
        return ResponseEntity.ok(subscriptionResource);
    }


    @PutMapping("/pay/subscription/{profileId}")
    public ResponseEntity<SubscriptionResource>paySubscription(@PathVariable Long profileId){
        Optional<Subscription> subscription= subscriptionCommandService.handle(new UpdateSubscriptionCommand(profileId));
        var  subscriptionResource = SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription.get());
        return ResponseEntity.ok(subscriptionResource);

    }




}
