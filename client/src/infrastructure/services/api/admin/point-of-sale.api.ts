import { PREFIX_API_ADMIN_POINT_OF_SALE } from "@/infrastructure/constants/url";
import { AxiosResponse } from "axios";
import { DefaultResponse, PaginationParams, PaginationResponse, ResponseList } from "@/infrastructure/types/api.common";
import { Ref } from "vue";
import request from "@/infrastructure/services/request.ts";

export interface PropertyPOSProductDetailParams {
    keyword?: string | null;
    gioiTinh?: boolean | null;
    idThuongHieu?: string | null;
    idKichCo?: string | null;
    idCoAo?: string | null;
    idTayAo?: string | null;
    idHoaTiet?: string | null;
    idChatLieu?: string | null;
    idKieuDang?: string | null;
    idTinhNang?: string | null;
    bienGiaBe?: number | null;
    bienGiaLon?: number | null;

    [key: string]: any;
}
export interface FindPOSProductDetailRequest extends PropertyPOSProductDetailParams, PaginationParams {
}
export type POSProductDetailResponse = ResponseList & {
    maSanPhamChiTiet: string | null;
    ten: string | null;
    soLuong: number | null;
    gia: number | null;
    giaHienTai: number | null;
    tenSanPham: string | null;
    tenThuongHieu: string | null;
    gioiTinh: boolean | null;
    kichCo: string | null;
    phongCach: string | null;
    maMauSac: string | null;
    tenMauSac: string | null;
    linkAnh: string | null;
}

export const getProductDetailsInPOS = async (params: Ref<FindPOSProductDetailRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_POINT_OF_SALE}/products`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<POSProductDetailResponse>>>
    >;
    return res.data;
};

export const getPriceRank =  async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_POINT_OF_SALE}/sub/price-rank`,
        method: "GET",
    })) as AxiosResponse<Object>;
    return res.data;
};

export const getOrderDetails =  async (idOrder: string) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_POINT_OF_SALE}/products-in-order/${idOrder}`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<Array<POSProductDetailResponse>>
    >;
    return res.data;
};


export interface POSAddProductsToCartRequest {
    idSanPhamChiTiets: string[] | null;
    idHoaDonCho: string | null;
    userEmail: string | null;
    soLuong: number | null
}

export const createOrderDetails = async (data: POSAddProductsToCartRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_POINT_OF_SALE}/products-in-order`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
    return res.data;
}

export interface POSUpdateCartRequest {
    idHoaDonChiTiet: string | null;
    soLuongBanTruoc: number | null;
    soLuongBanSau: number | null;
}

export const updateQuantityOrderDetails = async (data: POSUpdateCartRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_POINT_OF_SALE}/products-in-order`,
        method: "PUT",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
    return res.data;
}

export const deleteCartById = async (idOrderDetail: string) => {
    return await request({
        url: `${PREFIX_API_ADMIN_POINT_OF_SALE}/products-in-order/${idOrderDetail}`,
        method: "DELETE",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};