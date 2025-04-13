import axios from 'axios';
import { SOCKET_TARGET, BASE_URL_SERVER } from '@env';

export const updateQuantity = async (idHoaDonChiTiet, soLuongBanTruoc, soLuongBanSau) => {
  try {
    const response = await axios.put( SOCKET_TARGET + '/api/v1/admin/point-of-sale/products-in-order', {
      idHoaDonChiTiet,
      soLuongBanTruoc,
      soLuongBanSau
    });
    console.log('Kết quả cập nhật:', response.data);
  } catch (error) {
    console.error('Lỗi cập nhật số lượng:', error);
  }
};



export const addToCart = async (idHoaDonCho, idSanPhamChiTiets, soLuong, userEmail) => {
  try {
    const response = await axios.post( SOCKET_TARGET + '/api/v1/admin/point-of-sale/products-in-order', {
      idHoaDonCho,
      idSanPhamChiTiets,
      soLuong,
      userEmail
    });
    console.log('Kết quả thêm vào giỏ:', response.data);
  } catch (error) {
    console.error('Lỗi thêm sản phẩm:', error);
  }
};


export const deleteProductFromCart = async (id) => {
    try {
      const response = await axios.delete( SOCKET_TARGET + `/api/v1/admin/point-of-sale/products-in-order/${id}`);
    } catch (error) {
      console.error('Lỗi xóa sản phẩm:', error);
    }
  };