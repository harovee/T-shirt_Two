import { DefaultResponse, PaginationParams, PaginationResponse, ResponseList } from "@/infrastructure/types/api.common";
import { Ref } from "vue";
import request from "@/infrastructure/services/request.ts";
import { PREFIX_API_ADMIN_SALE } from "@/infrastructure/constants/url.ts";
import { AxiosResponse } from "axios";

export interface PropertySaleParams {
    keyword?: string | null;
    loai?: string | null;
    trangThai?: string | null;
    ngayBatDau?: number | null;
    ngayKetThuc?: number | null;

    [key: string]: any;
}

export interface FindSaleRequest extends PropertySaleParams, PaginationParams {

}

export interface SaleRequest {
    ma: string | null;
    ten: string | null;
    loai: string | null;
    giaTri: number | null;
    giaTriGiamToiDa: number | null;
    ngayBatDau: number | null;
    ngayKetThuc: number | null;
    nguoiSua: string | null;
    trangThai: string | null;
}

export type SaleResponse = ResponseList & {
    maDotGiamGia: string | null;
    ten: string | null;
    giaTri: number | null;
    giaTriGiamToiDa: number | null;
    ngayBatDau: number | null;
    ngayKetThuc: number | null;
    trangThai: string | null;
};

export type DetailSaleResponse = {
    id: string | null;
    maDotGiamGia: string | null;
    ten: string | null;
    loai: string | null;
    giaTri: number | null;
    giaTriGiamToiDa: number | null;
    ngayBatDau: number | null;
    ngayKetThuc: number | null;
    trangThai: string | null;
    createdDate: bigint | null;
    lastModifiedDate: bigint | null;
};

export const getSales = async (params: Ref<FindSaleRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SALE}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<SaleResponse>>>
    >;

    return res.data;
};

export const createSale = async (data: SaleRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SALE}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getSaleById = async (saleId: Ref<string | null>) => {
    if (saleId.value != null ) {
        return await request({
            url: `${PREFIX_API_ADMIN_SALE}/${saleId.value}`,
            method: "GET"
        }) as AxiosResponse<
            DefaultResponse<DetailSaleResponse>
        >;
    } else { return null}
};

export const updateSale = async (saleId: string, data: SaleRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_SALE}/${saleId}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const changeStatusSale = async (saleId: string, trangThai: string) => {
    return await request({
        url: `${PREFIX_API_ADMIN_SALE}/change-status/${saleId}/${trangThai}`,
        method: "PUT",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const deleteSaleById = async (saleId: string) => {
    return await request({
        url: `${PREFIX_API_ADMIN_SALE}/${saleId}`,
        method: "DELETE",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};




export type AttributeResponse = {
    id: string | null;
    ten: string | null;
}
export type AttributesResponse = {
    categories: Array<AttributeResponse>;
    brands: Array<AttributeResponse>;
    sizes: Array<AttributeResponse>;
    collars: Array<AttributeResponse>;
    sleeves: Array<AttributeResponse>;
    vignettes: Array<AttributeResponse>;
    materials: Array<AttributeResponse>;
    features: Array<AttributeResponse>;
    styles: Array<AttributeResponse>;
}
export const getAttributesOfProductDetail = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SALE}/product-detail-attributes`,
        method: "GET",
    })) as AxiosResponse<DefaultResponse<AttributesResponse>>;
    return res.data;
};




export interface FindProductRequest extends PaginationParams {
    key: string | null;
    idDanhMuc: string | null;
}
export type ProductSaleModuleResponse = {
    id: string | null;
    ten: string | null;
    tenDanhMuc: string | null;
}
export const getProductSaleModule = async (params: Ref<FindProductRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SALE}/products`,
        method: "GET",
        params: params.value
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ProductSaleModuleResponse>>>>;
    return res.data;
};




export interface PropertyProductDetailParams {
    idSanPhams: string | null;
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

    [key: string]: any;
}
export interface FindProductDetailRequest extends PropertyProductDetailParams, PaginationParams {
}
export type ProductDetailResponse = ResponseList & {
    maSanPhamChiTiet: string | null;
    ten: string | null;
    tenSanPham: string | null;
    tenThuongHieu: string | null;
    gioiTinh: boolean | null;
    kichCo: string | null;
    phongCach: string | null;
}
export const getProductDetailSaleModule = async (params: Ref<FindProductDetailRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SALE}/product-details`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ProductDetailResponse>>>
    >;
    return res.data;
};



export interface SaleProductRequest {
    idSanPhamChiTiets: string | null;
    idDotGiamGia?: string | null,
    loaiGiamGia?: string | null;
    giaTriGiamGia?: number | null;
    nhanVien?: string | null;
}
export const saveSaleProduct = async (data: SaleProductRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SALE}/sale-product-details`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
    return res.data;
};




export interface SaleAndSaleProductRequest {
    saleRequest: SaleRequest | null;
    saleProductRequest: SaleProductRequest | null;
}
export const saveSaleAndSaleProduct = async (data: SaleAndSaleProductRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SALE}/save-sale-and-sale-product-details`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
    return res.data;
};
