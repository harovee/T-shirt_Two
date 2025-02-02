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
    loai: string | null;
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
    if (saleId.value && saleId.value.trim() !== '') {
        const url = `${PREFIX_API_ADMIN_SALE}/${saleId.value}`;
        return request({
            url: url,
            method: "GET",
        }).then((response) => {
            return response as AxiosResponse<DefaultResponse<DetailSaleResponse>>;
        }).catch((error) => {
            console.error("Error fetching sale:", error);
            return null; // Hoặc xử lý lỗi theo ý bạn
        });
    } else {
        return Promise.resolve(null);
    }
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
    tongSoLuong: number | null;
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
    gia: number | null;
    giaHienTai: number | null;
    tenSanPham: string | null;
    tenThuongHieu: string | null;
    gioiTinh: boolean | null;
    kichCo: string | null;
    phongCach: string | null;
    soLuong: number | null;
    linkAnh: string | null;
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
    idSanPhamChiTiets: string[] | null;
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

export const updateSaleAndSaleProduct = async (data: SaleAndSaleProductRequest, saleId: string) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SALE}/save-sale-and-sale-product-details/${saleId}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
    return res.data;
};


export interface PropertySaleProductDetailParams {
    keyword?: string | null;
    idDotGiamGia: string;

    [key: string]: any;
}
export interface FindSaleProductDetailRequest extends PropertySaleProductDetailParams, PaginationParams {
}
export type SaleProductDetailResponse = ResponseList & {
    idSanPhamGiamGia: string | null;
    maSanPhamChiTiet: string | null;
    ten: string | null;
    gia: number | null;
    soLuong: number | null;
    giaSauGiam: number | null;
    giaHienTai: number | null;
    linkAnh: string | null;
};
export const getSaleProductDetails = async (params: Ref<FindSaleProductDetailRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SALE}/sale-product-details`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<SaleProductDetailResponse>>>
    >;
    return res.data;
};

export const deleteSaleProductById = async (saleProductId: string) => {
    const res = await request({
        url: `${PREFIX_API_ADMIN_SALE}/sale-product-details/${saleProductId}`,
        method: "DELETE",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
    return res.data;
};