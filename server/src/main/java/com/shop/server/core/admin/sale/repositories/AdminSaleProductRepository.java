package com.shop.server.core.admin.sale.repositories;

import com.shop.server.entities.main.SanPhamGiamGia;
import com.shop.server.repositories.SanPhamGiamGiaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminSaleProductRepository extends SanPhamGiamGiaRepository {

    Optional<SanPhamGiamGia> findBySanPhamChiTietIdAndDotGiamGiaIdAndDeletedIsFalse(String spctId, String dggId);

    List<SanPhamGiamGia> findAllByDotGiamGiaIdAndDeletedIsFalse(String idDotGiamGia);
}
