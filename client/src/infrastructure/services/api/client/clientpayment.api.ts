import { vnPayRequest } from './clientpayment.api';
import {
  useMutation,
  useQuery,
  useQueryClient,
  UseQueryReturnType,
} from "@tanstack/vue-query";
import { DefaultResponse } from "@/infrastructure/types/api.common";
import request from "@/infrastructure/services/request.ts";
import { API_CLIENT_PAYMENT } from "@/infrastructure/constants/url.ts";
import { AxiosResponse } from "axios";
import { Ref } from "vue";

export interface invoiceDetailRequest {
  idSanPhamChiTiet: string | null;
  soLuong: number | null;
  gia: number | null;
}

export interface clientPaymentRequest extends vnPayRequest{
  diaChiNguoiNhan: string | null;
  ghiChu: string | null;
  soDienThoai: string | null;
  tenNguoiNhan: string | null;
  tienGiam: number | null;
  tienShip: number | null;
  tongTien: number | null;
  idKhachHang: string | null;
  idNhanVien: string | null;
  idPhieuGiamGia: string | null;
  paymentMethod: string | null;
  listSanPhamChiTiets: invoiceDetailRequest[] | null;
  email:string | null;
}

export interface vnPayRequest  {
  amount: string | null;
  bankCode: string | null;
}

export type vnPayLinkResponse = {
  code: string | null;
  message: string | null;
  paymentUrl: string | null;
};

export const createInvoiceOnline = async (data: clientPaymentRequest) => {
  const res = (await request({
    url: `${API_CLIENT_PAYMENT}`,
    method: "POST",
    data: data,
  })) as AxiosResponse<DefaultResponse<DefaultResponse<null>>>;
  return res.data;
};

export const createInvoiceOnlineWithVnPay = async (data: clientPaymentRequest) => {
    const res = await request({
      url: `${API_CLIENT_PAYMENT}/vnpay`,
      method: "POST",
      params: {
        amount: data.amount,
        bankCode: data.bankCode,
      },
      data: {
        ...data,
      },
    }) as AxiosResponse<DefaultResponse<DefaultResponse<null>>>;
    
    return res.data;
};

export const createInvoiceOnlineWithMoMo = async (data: clientPaymentRequest) => {
  const res = await request({
    url: `${API_CLIENT_PAYMENT}/momo-invoice`,
    method: "POST",
    data: data,
  }) as AxiosResponse<DefaultResponse<DefaultResponse<null>>>;
  
  return res.data;
};
// export const createInvoiceOnlineWithVnPay = async (data: vnPayRequest) => {
//     const res = (await request({
//       url: `${API_CLIENT_PAYMENT}/vnpay`,
//       method: "POST",
//       params: {
//         {
//             amount: data.amount, // Truyền vào query params
//             bankCode: data.bankCode,
//         }
//       },
//       data: data,
//     })) as AxiosResponse<DefaultResponse<DefaultResponse<null>>>;
//     return res.data;
//   };

export const getVnPayLink = async (idHoaDon: string, params: Ref<vnPayRequest>) => {
  const res = (await request({
    url: `${API_CLIENT_PAYMENT}/vn-pay/${idHoaDon}`,
    method: "GET",
    params: params.value,
  })) as AxiosResponse<
    DefaultResponse<vnPayLinkResponse>
  >;
  return res.data;
};
