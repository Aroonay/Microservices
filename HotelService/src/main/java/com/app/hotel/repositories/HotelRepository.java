package com.app.hotel.repositories;
import com.app.hotel.entities.Hotel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, String> {

}
