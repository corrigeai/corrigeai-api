package org.corrige.ai.controllers;

import java.util.Map;

import org.corrige.ai.enums.ReviewStatus;
import org.corrige.ai.services.interfaces.MetricsService;
import org.corrige.ai.util.ServerConstants;
import org.corrige.ai.validations.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(ServerConstants.SERVER_REQUEST 
				+ ServerConstants.METRICS_REQUESTS)
public class MetricsController {
	@Autowired
	private MetricsService metricsService;
	
	@GetMapping("/evolution/{userId}")
	public void getHistoryEvolution(@PathVariable String userId) throws UserNotExistsException {
		ResponseEntity.ok(this.metricsService.getEvolution(userId));
	}
	
	@GetMapping("/essay-stats/{userId}")
	public ResponseEntity<Map<ReviewStatus, Long>> getEssaysStatusSummarised(@PathVariable String userId) throws UserNotExistsException {
		return ResponseEntity.ok(this.metricsService.getEssaysStatusSummarised(userId));
	}
	
	@GetMapping("/req-rating/{userId}")
	public void getMeanRatingPerRequirement(@PathVariable String userId) {
		this.metricsService.getMeanRatingPerRequirement(userId);
	}
	
	@GetMapping("/eval-rating/{userId}")
	public void getReviewEvaluation(@PathVariable String userId) {
		this.metricsService.getReviewEvaluation(userId);
	}
	
	@GetMapping("/eval-req-rating/{userId}")
	public void getReviewEvaluationPerRating(@PathVariable String userId) {
		this.metricsService.getReviewEvaluationPerRating(userId);
	}
}
