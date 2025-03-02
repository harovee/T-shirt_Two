

DELIMITER $$
DROP FUNCTION IF EXISTS get_time_display;

CREATE FUNCTION get_time_display(timeUnit VARCHAR(10), ngayTao BIGINT)
    RETURNS VARCHAR(20) DETERMINISTIC
BEGIN
    DECLARE result VARCHAR(20);

    SET result = CASE
                     WHEN timeUnit = 'DAY' THEN CONCAT(FROM_UNIXTIME(ngayTao / 1000, '%d-%m-%Y'))
                     WHEN timeUnit = 'WEEK' THEN CONCAT(YEAR(FROM_UNIXTIME(ngayTao / 1000)), '-W', WEEK(FROM_UNIXTIME(ngayTao / 1000)))
                     WHEN timeUnit = 'MONTH' THEN CONCAT(FROM_UNIXTIME(ngayTao / 1000, '%m-%Y'))
                     WHEN timeUnit = 'YEAR' THEN CONCAT(FROM_UNIXTIME(ngayTao / 1000, '%Y'))
        END;

    RETURN result;
END $$
DELIMITER ;