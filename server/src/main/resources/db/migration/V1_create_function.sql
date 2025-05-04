DELIMITER $$

DROP FUNCTION IF EXISTS tinh_gia_hien_tai;

CREATE FUNCTION tinh_gia_hien_tai(spct_id CHAR(36))
    RETURNS DECIMAL(10, 2)
    DETERMINISTIC
    READS SQL DATA
BEGIN
    DECLARE gia_hien_tai DECIMAL(10, 2);

    -- Tính tổng giá sau giảm và số đợt giảm giá đang diễn ra
    SELECT IF(COUNT(dg.id) > 0, SUM(sd.gia_sau_giam) / COUNT(dg.id), spct.gia)
    INTO gia_hien_tai
    FROM
        san_pham_chi_tiet spct
            LEFT JOIN san_pham_giam_gia sd ON spct.id = sd.id_san_pham_chi_tiet
            LEFT JOIN dot_giam_gia dg ON sd.id_dot_giam_gia = dg.id
    WHERE
        spct.id = spct_id
      AND dg.trang_thai = 'ACTIVE'
      AND dg.ngay_bat_dau <= UNIX_TIMESTAMP()*1000  -- So sánh thời gian bắt đầu
      AND dg.ngay_ket_thuc >= UNIX_TIMESTAMP()*1000  -- So sánh thời gian kết thúc
    GROUP BY
        spct.id;  -- Nhóm theo spct.id để không gặp lỗi "only_full_group_by"

    RETURN gia_hien_tai;

END $$

DELIMITER ;

