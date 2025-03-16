package com.shop.server.repositories;

import com.shop.server.entities.main.HoaDon;
import com.shop.server.entities.main.LichSuHoaDon;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuHoaDonRepository  extends CrudRepository<LichSuHoaDon, String> {
    List<LichSuHoaDon> findByIdHoaDon(HoaDon hoaDon);
}
