package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @PostMapping("/subscribers")
    @ApiMessage("Create subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) throws IdInvalidException {

        Subscriber sub = this.subscriberService.fetchSubscriberByEmail(subscriber.getEmail());
        if (sub != null) {
            throw new IdInvalidException("Email đã tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.subscriberService.handleCreateSubscriber(subscriber));
    }

    @PutMapping("/subscribers")
    public ResponseEntity<Subscriber> updateSubscribers(@PathVariable String id, @RequestBody Subscriber subsRequest)
            throws IdInvalidException {

        Subscriber subDB = this.subscriberService.fetchSubscriberByEmail(subsRequest.getEmail());
        if (subDB == null) {
            throw new IdInvalidException("Id không tồn tại");
        }

        return ResponseEntity.ok()
                .body(this.subscriberService.handleUpdateSubscriber(subDB, subsRequest));
    }

}
