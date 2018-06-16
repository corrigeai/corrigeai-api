package org.tuiter.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tuiter.errors.exceptions.NotificationNotExistsException;
import org.tuiter.errors.exceptions.TuiterApiException;
import org.tuiter.services.implementations.NotificationServiceImpl;
import org.tuiter.services.interfaces.NotificationService;
import org.tuiter.util.ServerConstants;

@Controller
@CrossOrigin
@RequestMapping(ServerConstants.SERVER_REQUEST 
				+ ServerConstants.NOTIFICATION_REQUEST)
public class NotificationController {
//	@Autowired
//	private SimpMessagingTemplate messagingTemplate;
	private NotificationService notificationService;
	
	@Autowired
	public void setNotificationService(NotificationServiceImpl notificationService) {
		this.notificationService = notificationService;
	}
	
/*	@MessageMapping("/send/message/{essayId}")
	public void notifyOnReview(@PathVariable String essayId, @RequestBody NotificationBean bean) {
		try {
			Notification notification = notificationService.createOnReviewDone(essayId, bean);
			messagingTemplate.convertAndSend("/notification_ch/" + notification.getUserId(), notification);
		} catch(ReviewNotExistsException e) {
			throw new TuiterApiException("Review not exists!");
		} catch(UserNotExistsException e) {
			throw new TuiterApiException("User not exists!");
		} catch(UserNotFoundException e) {
			throw new TuiterApiException("User not exists!");
		} catch(EssayNotExistsException e) {
			throw new TuiterApiException("Essay not exists!");
		}
	}*/
	
	@RequestMapping(value = "/{notificationId}", method = RequestMethod.DELETE)
	public ResponseEntity<HttpStatus> delete(@PathVariable String notificationId) {
		try {
			notificationService.delete(notificationId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch(NotificationNotExistsException e) {
			throw new TuiterApiException("Notification not exists!");
		}
	}
}
