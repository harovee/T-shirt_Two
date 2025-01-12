import { Ref } from 'vue';
import { DefaultResponse, PaginationParams, PaginationResponse, ResponseList } from "@/infrastructure/types/api.common";
import request from "@/infrastructure/services/request.ts";
import { API_ADMIN_BILL_DETAIL } from '@/infrastructure/constants/url';
import {  AxiosResponse } from 'axios';

export interface BillDetailPropsParams {
    idHoaDon?: string | null;
    keyword?: string | null;

    [key: string]: any; 
}

export interface FindBillDetailRequest extends BillDetailPropsParams, PaginationParams {

}

export interface BillDetailRequest {

}

export type BillDetailResponse = ResponseList & {
    catalog: number | null;
    maHoaDon: string | null;
    tenSanPhamChiTiet: string | null;
    soLuong: number | null;
    gia: number | null;
    thanhTien: number | null;
    trangThaiHD: string | null;
    tongTienHD: number | null;
    tienGiamHD: number | null;
    loaiHoaDon: string | null;
    tenNguoiNhan: string | null;
    soDienThoai: string | null;
    diaChiNguoiNhan: string | null;
    tienShip: number | null;
    ngayShip: string | null;
    ghiChuHD: string | null;
    tenNhanVien: string | null;
    tenKhachHang: string | null;
}

export const getBillDetailsByIdHoaDon = async (params: Ref<FindBillDetailRequest>) => {
    const res = (await request ({
        url: `${API_ADMIN_BILL_DETAIL}`,
        method: 'GET',
        params: params.value
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<BillDetailResponse>>>
    >;

    return res.data;
}