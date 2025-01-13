package com.app.user.service.services.impl;

import com.app.user.service.entities.Hotel;
import com.app.user.service.entities.Rating;
import com.app.user.service.entities.User;
import com.app.user.service.exceptions.ResourceNotFoundException;
import com.app.user.service.external.services.HotelService;
import com.app.user.service.repositories.UserRepository;
import com.app.user.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

    private Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public User saveUser(User user) {
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
       User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User with given id is not found on server !! : "+userId));

       //fetch rating of the above user from RATING SERVICE
        Rating[] ratingsOfUser=restTemplate.getForObject("http://RATINGSERVICE/ratings/users/"+user.getUserId(), Rating[].class);
        logger.info("{}", ratingsOfUser);


        List<Rating>  ratings= Arrays.stream(ratingsOfUser).toList();

        List<Rating> ratingList=ratings.stream().map(rating->{
            //http://localhost:8082/hotels/61ed91e8-052a-4020-98a3-8ed638144563
            //ResponseEntity<Hotel> forEntity=restTemplate.getForEntity("http://HOTELSERVICE/hotels/"+rating.getHotelId(), Hotel.class);
            Hotel hotel=hotelService.getHotel(rating.getHotelId());

            //logger.info("response status code: {} ",forEntity.getStatusCode());
            rating.setHotel(hotel);
            return rating;
        }).collect(Collectors.toList());


        user.setRatings(ratingList);
        return user;
    }
}
