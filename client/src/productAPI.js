// src/routes/productApi.js
import express from "express";
import connection from "./dbConfig.js";
import cors from 'cors';
const app = express();

// Sử dụng CORS cho tất cả các yêu cầu
app.use(cors());

// API lấy thông tin sản phẩm
app.get("/other-api/products", (req, res) => {
  const sanPham = req.query.sanPham || "";
  const maxPrice = req.query.maxPrice || 100000000; 
  const mauSacs = typeof req.query.mauSac === 'string' 
    ? req.query.mauSac.split(',')  // Tách chuỗi thành mảng nếu là chuỗi
    : req.query.mauSac || []; 
  const chatLieu = req.query.chatLieu || "";
  const coAo = req.query.coAo || "";
  const hoaTiet = req.query.hoaTiet || "";
  const kichCo = req.query.kichCo || "";
  const kieuDang = req.query.kieuDang || "";
  const tayAo = req.query.tayAo || "";
  const thuongHieu = req.query.thuongHieu || "";
  const tinhNang = req.query.tinhNang || "";

  // Khởi tạo câu truy vấn
  let query = `
      SELECT
          sp.id AS id, 
          spct.ma_san_pham_chi_tiet AS maSanPhamChitiet,
          cl.ten AS chatLieu,
          ca.ten AS coAo,
          ht.ten AS hoaTiet,
          kc.ten AS kichCo,
          kd.ten AS kieuDang,
          ms.ten AS mauSac,
          ta.ten AS tayAo,
          th.ten AS thuongHieu,
          tn.ten AS tinhNang,
          sp.ten AS sanPham,
          spct.gia AS gia
      FROM 
          san_pham_chi_tiet spct
          LEFT JOIN chat_lieu cl ON spct.id_chat_lieu = cl.id
          LEFT JOIN co_ao ca ON spct.id_co_ao = ca.id
          LEFT JOIN hoa_tiet ht ON spct.id_hoa_tiet = ht.id
          LEFT JOIN kich_co kc ON spct.id_kich_co = kc.id
          LEFT JOIN kieu_dang kd ON spct.id_kieu_dang = kd.id
          LEFT JOIN mau_sac ms ON spct.id_mau_sac = ms.id
          LEFT JOIN tay_ao ta ON spct.id_tay_ao = ta.id
          LEFT JOIN thuong_hieu th ON spct.id_thuong_hieu = th.id
          LEFT JOIN tinh_nang tn ON spct.id_tinh_nang = tn.id
          JOIN san_pham sp ON spct.id_san_pham = sp.id
      WHERE 
          (LOWER(sp.ten) LIKE LOWER(?)) AND
          (spct.gia IS NULL OR spct.gia <= ?) AND
          (LOWER(cl.ten) LIKE LOWER(?)) AND
          (LOWER(ca.ten) LIKE LOWER(?)) AND
          (LOWER(ht.ten) LIKE LOWER(?)) AND
          (LOWER(kc.ten) LIKE LOWER(?)) AND
          (LOWER(kd.ten) LIKE LOWER(?)) AND
          (LOWER(ta.ten) LIKE LOWER(?)) AND
          (LOWER(th.ten) LIKE LOWER(?)) AND
          (LOWER(tn.ten) LIKE LOWER(?)) AND
          spct.deleted = 0
    `;

    if (mauSacs.length > 0) {
        query += ` AND ms.ten IN (?)`;  // Điều kiện IN để so sánh với mảng màu sắc
      }

  // Truyền tham số vào câu truy vấn
  const params = [
    `%${sanPham}%`, // Tên sản phẩm
    maxPrice, // Giá tối đa
    `%${chatLieu}%`,
    `%${coAo}%`,
    `%${hoaTiet}%`,
    `%${kichCo}%`,
    `%${kieuDang}%`,
    `%${tayAo}%`,
    `%${thuongHieu}%`,
    `%${tinhNang}%`,
  ];
  if (mauSacs.length > 0) {
    params.push(mauSacs);  // Thêm mảng màu sắc vào params
  }
  
  connection.query(query, params, (err, results) => {
    if (err) {
      console.error("Lỗi khi truy vấn dữ liệu:", err);
      return res.status(500).json({ error: "Lỗi khi truy vấn dữ liệu" });
    }
    if (results.length === 0) {
        return res.json({ message: "Không tìm thấy sản phẩm nào." });
      }
    // Trả kết quả dưới dạng JSON
    res.json(results);
  });
});

// app.get("/other-api/all-products", () => {
//   // Khởi tạo câu truy vấn
//   let query = `
//       SELECT
//           sp.id AS id, 
//           spct.ma_san_pham_chi_tiet AS maSanPhamChitiet,
//           cl.ten AS chatLieu,
//           ca.ten AS coAo,
//           ht.ten AS hoaTiet,
//           kc.ten AS kichCo,
//           kd.ten AS kieuDang,
//           ms.ten AS mauSac,
//           ta.ten AS tayAo,
//           th.ten AS thuongHieu,
//           tn.ten AS tinhNang,
//           sp.ten AS sanPham,
//           spct.gia AS gia
//       FROM 
//           san_pham_chi_tiet spct
//           LEFT JOIN chat_lieu cl ON spct.id_chat_lieu = cl.id
//           LEFT JOIN co_ao ca ON spct.id_co_ao = ca.id
//           LEFT JOIN hoa_tiet ht ON spct.id_hoa_tiet = ht.id
//           LEFT JOIN kich_co kc ON spct.id_kich_co = kc.id
//           LEFT JOIN kieu_dang kd ON spct.id_kieu_dang = kd.id
//           LEFT JOIN mau_sac ms ON spct.id_mau_sac = ms.id
//           LEFT JOIN tay_ao ta ON spct.id_tay_ao = ta.id
//           LEFT JOIN thuong_hieu th ON spct.id_thuong_hieu = th.id
//           LEFT JOIN tinh_nang tn ON spct.id_tinh_nang = tn.id
//           JOIN san_pham sp ON spct.id_san_pham = sp.id
//     `;
  
//   connection.query(query, (err, results) => {
//     if (err) {
//       console.error("Lỗi khi truy vấn dữ liệu:", err);
//       return res.status(500).json({ error: "Lỗi khi truy vấn dữ liệu" });
//     }
//     if (results.length === 0) {
//         return res.json({ message: "Không tìm thấy sản phẩm nào." });
//       }
//     res.json(results);
//   });
// });

app.listen(3000, () => {
  console.log("Server đang chạy tại http://localhost:3000");
});
