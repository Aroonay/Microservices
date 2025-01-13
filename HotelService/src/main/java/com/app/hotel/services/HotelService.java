package com.app.hotel.services;
import com.app.hotel.entities.Hotel;

import java.util.List;
import java.util.Optional;


public interface HotelService {

    Hotel create(Hotel hotel);

    List<Hotel> getAll();

    Hotel get(String id);
}
