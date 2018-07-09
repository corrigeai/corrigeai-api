package org.corrige.ai.services.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.corrige.ai.enums.ReviewStatus;
import org.corrige.ai.models.essay.EditEssayBean;
import org.corrige.ai.models.essay.Essay;
import org.corrige.ai.models.essay.EssayBean;
import org.corrige.ai.models.review.EssayToReviewResponse;
import org.corrige.ai.models.review.EssaysReviews;
import org.corrige.ai.models.review.Review;
import org.corrige.ai.models.user.User;
import org.corrige.ai.repositories.EssayRepository;
import org.corrige.ai.services.interfaces.EssayService;
import org.corrige.ai.services.interfaces.ReviewService;
import org.corrige.ai.services.interfaces.TopicService;
import org.corrige.ai.services.interfaces.UserService;
import org.corrige.ai.validations.exceptions.EmptyFieldsException;
import org.corrige.ai.validations.exceptions.EssayNotExistsException;
import org.corrige.ai.validations.exceptions.TopicNotExistsException;
import org.corrige.ai.validations.exceptions.TopicNotFoundException;
import org.corrige.ai.validations.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EssayServiceImpl implements EssayService{
	@Autowired
	private EssayRepository essayRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private TopicService topicService;

	@Override
	public Essay create(EssayBean bean) throws UserNotExistsException, TopicNotExistsException{
		Optional<User> user = userService.findByUsername(bean.getUserUsername());
		if(! user.isPresent()) {
			throw new UserNotExistsException();
		} 
		if(bean.getTopicId() != null && topicService.findById(bean.getTopicId()) == null) {
			throw new TopicNotExistsException();
		} 
		Essay essay = new Essay(user.get().getId(), bean.getTitle(), bean.getTheme(), bean.getContent(), bean.getType(), bean.getTopicId());
		return essayRepository.save(essay);
	}

	@Override
	public Essay update(String id, EditEssayBean bean) throws EssayNotExistsException, EmptyFieldsException, TopicNotExistsException{
		Optional<Essay> essay_opt = essayRepository.findById(id);
		if(essay_opt.isPresent()) {
			if(bean.getTopicId() != null && topicService.findById(bean.getTopicId()) == null) {
				throw new TopicNotExistsException();
			}
			Essay essay = essay_opt.get();
			if(bean.getTitle() != null && !bean.getTitle().isEmpty() && 
			   bean.getTheme() != null && !bean.getTheme().isEmpty() && 
			   bean.getContent() != null && !bean.getContent().isEmpty() &&
			   bean.getType() != null) {
				
				essay.setTitle(bean.getTitle());
				essay.setTheme(bean.getTheme());
				essay.setContent(bean.getContent());
				essay.setType(bean.getType());
				essay.setTopicId(bean.getTopicId());
				
				return essayRepository.save(essay);
			} else {
				throw new EmptyFieldsException();
			}
			
		} else {
			throw new EssayNotExistsException();
		}
	}

	@Override
	public Essay delete(String id) throws EssayNotExistsException {
		Optional<Essay> essay = essayRepository.findById(id);
		if(essay.isPresent()) {
			essayRepository.delete(essay.get());
			return essay.get();
		} else {
			throw new EssayNotExistsException();
		}
	}

	@Override
	public Collection<Essay> findAllByUserUsername(String username) throws UserNotExistsException{
		Optional<User> user = userService.findByUsername(username);
		if (user.isPresent()) 
			return essayRepository.findAllByUserId(user.get().getId());
		else 
			throw new UserNotExistsException();
	}

	@Override
	public Collection<Essay> findAllByUserId(String id) throws UserNotExistsException{
		if (this.userService.existsById(id)) 
			return essayRepository.findAllByUserId(id);
		throw new UserNotExistsException();
	}
	
	@Override
	public Collection<Essay> findAll() {
		return essayRepository.findAll();
	}

	@Override
	public Optional<Essay> findByTitleAndUserId(String id, String userId) {
		return essayRepository.findByTitleAndUserId(id, userId);
	}

	@Override
	public Essay findById(String id) throws EssayNotExistsException {
		Optional<Essay> essay = essayRepository.findById(id);
		if(essay.isPresent())
			return essay.get();
		else
			throw new EssayNotExistsException();
	}

	@Override
	public EssayToReviewResponse getEssayToReview(String id) throws EssayNotExistsException, UserNotExistsException, TopicNotExistsException, TopicNotFoundException {
		for (Review review : reviewService.findAllByUserId(id)) {
			if (review.getStatus().equals(ReviewStatus.PENDING) && essayRepository.findById(review.getEssayId()).isPresent()) {
				return new EssayToReviewResponse(review.getId(), essayRepository.findById(review.getEssayId()).get());
			}
		}
		
		Collection<Essay> essays = essayRepository.findAll();
		if (!essays.isEmpty()) {
			Collection<Essay> notReviewedEssays;
			
			if(this.userService.findById(id).getUsingWeekelyTopic()) {
				notReviewedEssays = this.getEssaysByTopic(this.topicService.getOpenTopic().getId())
										.stream().filter(essay -> essay.getStatus().equals(ReviewStatus.PENDING))
										.collect(Collectors.toList());
			} else {
				notReviewedEssays = essays.stream().filter(
						essay -> essay.getStatus().equals(ReviewStatus.PENDING))
						.collect(Collectors.toList());
			}
			
			if(!notReviewedEssays.isEmpty())
				essays = notReviewedEssays;
			
			if (essays.size() == 0)
				throw new EssayNotExistsException();
			
			for (Essay essay : essays) {
				if (!essay.getUserId().equals(id) && !userAlreadyReview(reviewService.findAllByUserId(id), essay.getId())) {
					Review review = reviewService.create(id, essay.getId());
					return new EssayToReviewResponse(review.getId(), essay);
				}
			}
			throw new EssayNotExistsException();
		} else {
			throw new EssayNotExistsException();
		}
	}
	
	private Boolean userAlreadyReview(Iterable<Review> essays, String essayId) {
		Collection<Review> listOfReviews = new ArrayList<>();
		essays.forEach(listOfReviews::add);
		for(Review review : listOfReviews) {
			if (review.getEssayId().equals(essayId))
				return true;
		}
		return false;
	}

	@Override
	public List<EssaysReviews> getEssaysReviews(String userId) throws EssayNotExistsException, UserNotExistsException {
		Collection<Essay> essays = findAllByUserId(userId);
		List<EssaysReviews> reviews_list = new ArrayList<>();
		for (Essay essay : essays) {
			Iterable<Review> reviews = reviewService.findAllByEssayId(essay.getId());
			for (Review review : reviews) {
				reviews_list.add(new EssaysReviews(review, essay.getTitle(), essay.getTheme()));
			}
		}
		return reviews_list;
	}

	@Override
	public Collection<Essay> getEssaysByTopic(String topicId) throws TopicNotExistsException {
		Collection<Essay> essays = new ArrayList<>();
		for (Essay essay : essayRepository.findAll()) {
			if (essay.getTopicId() != null && essay.getTopicId().equals(topicId)) {
				essays.add(essay);
			}
		}
		return essays;
	}
}