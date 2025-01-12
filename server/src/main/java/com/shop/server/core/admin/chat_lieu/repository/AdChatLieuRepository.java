package com.shop.server.core.admin.chat_lieu.repository;

import com.shop.server.core.admin.chat_lieu.model.request.AdFindChatLieuRequest;
import com.shop.server.core.admin.chat_lieu.model.response.AdChatLieuResponse;
import com.shop.server.core.admin.chat_lieu.model.response.AdGetChatLieuResponse;
import com.shop.server.core.admin.product.models.responses.AdProductListResponse;
import com.shop.server.core.admin.product.models.responses.AdminProductResponse;
import com.shop.server.repositories.ChatLieuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdChatLieuRepository extends ChatLieuRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY cl.id DESC) AS catalog,
            cl.id as id,
            cl.ma_chat_lieu as maChatLieu,
            cl.ten AS ten,
            cl.ngay_tao AS ngayTao
        FROM chat_lieu cl
        WHERE
            (:#{#req.keyword} IS NULL OR cl.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR cl.ma_chat_lieu LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND cl.deleted = 0
    """, countQuery = """
        SELECT COUNT(cl.id)
        FROM chat_lieu cl
        WHERE
            (:#{#req.keyword} IS NULL OR cl.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR cl.ma_chat_lieu LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND cl.deleted = 0
    """, nativeQuery = true)
    Page<AdChatLieuResponse> getAllChatLieus (Pageable pageable, AdFindChatLieuRequest req);

    @Query(value = """
    SELECT cl.id AS id,
    cl.ten AS ten
    FROM ChatLieu cl
""")
    List<AdGetChatLieuResponse> getListChatLieu ();

    Boolean existsChatLieuByMaChatLieu(String maChatLieu);

    Boolean existsChatLieuByTen(String ten);

    @Query("""
        SELECT cl
        FROM ChatLieu cl
        WHERE cl.ten = :ten AND cl.id != :id
""")
    AdChatLieuResponse existsChatLieuByTenNotId(@Param("ten") String name, @Param("id") String id);
}
