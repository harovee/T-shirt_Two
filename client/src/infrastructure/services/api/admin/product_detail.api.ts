import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_PRODUCT_DETAIL} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyProductDetailParams {
    keyword?: string | null;
    idChatLieu?: string | null;
    idCoAo?: string | null;
    idHoaTiet?: string | null;
    idKichCo?: string | null;
    idKieuDang?: string | null;
    idMauSac?: string | null;
    idTayAo?: string | null;
    idThuongHieu?: string | null;
    idTinhNang?: string | null;
    trangThai?: string | null;
    [key: string]: any;
}

export interface FindProductDetailRequest extends PropertyProductDetailParams, PaginationParams {
    idSanPham?: string;
}

export interface FindAllProductDetailRequest extends PropertyProductDetailParams, PaginationParams {
}

export interface ProductDetailRequest {

    trangThai: number | null,

    gia: number | null,

    soLuong:number | null

    idChatLieu: string | null

    idCoAo: string | null

    idHoaTiet: string | null

    idMauSac: string | null

    idKichCo: string | null

    idKieuDang: string | null

    idTayAo: string | null

    idThuongHieu: string | null

    idTinhNang: string | null

    idSanPham: string | null

    listAnh: Array<anh> | null

    gioiTinh: string | null
}

export interface anh {
    url: string | null,

    name: string | null
}

export interface RenProductDetailResponse {

    id: string | null,

    trangThai: number | null,

    gia: number | null,

    soLuong:number | null

    idChatLieu: string | null

    idCoAo: string | null

    idHoaTiet: string | null

    idMauSac: string | null

    idKichCo: string | null

    idKieuDang: string | null

    idTayAo: string | null

    idThuongHieu: string | null

    idTinhNang: string | null

    idSanPham: string | null

    gioiTinh: string | null
}

export interface ProductDetailListResponse {

    id: string | null,

    maSanPhamChiTiet: string | null,

    trangThai: number | null,

    gia: number | null,

    soLuong:number | null

    chatLieu: string | null

    coAo: string | null

    hoaTiet: string | null

    mauSac: string | null

    kichCo: string | null

    kieuDang: string | null

    tayAo: string | null

    thuongHieu: string | null

    tinhNang: string | null

    sanPham: string | null

    gioiTinh: string | null
}



export interface ProductDetailUpdateRequest {
    gia: number | null,
    soLuong:number | null
    idChatLieu: string | null
    idCoAo: string | null
    idHoaTiet: string | null
    idMauSac: string | null
    idKichCo: string | null
    idKieuDang: string | null
    idTayAo: string | null
    idThuongHieu: string | null
    idTinhNang: string | null
    idSanPham: string | null
    trangThai: Number | null
    listAnh: Array<anh> | null
    gioiTinh: string | null
}

export type ProductDetailResponse = ResponseList & {
    maSanPhamChiTiet: string | null
    sanPham: string | null
    chatLieu: string | null
    thuongHieu: string | null
    coAo: string | null
    tayAo: string | null
    hoaTiet: string | null
    kieuDang: string | null
    tinhNang: string | null
    mauSac: string | null
    kichCo: string | null
    soLuong: number | 0
    gia: number | 0
    ngayTao: Number | null
    trangThai: Number | null
    gioiTinh: string | null
};

// export type ListProductResponse = {
//     maSanPham: string | null
//     ten: string | null
// };

export const getProductDetails = async (params: Ref<FindProductDetailRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PRODUCT_DETAIL}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ProductDetailResponse>>>
    >;

    return res.data;
};

export const getListProductDetail = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PRODUCT_DETAIL}/list-product-detail`,
        method: "GET"
    })) as AxiosResponse<
        DefaultResponse<Array<ProductDetailListResponse>>
    >;
    return res.data;
};

export const getAllProductDetails = async (paramsAll: Ref<FindAllProductDetailRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PRODUCT_DETAIL}/all-product-detail`,
        method: "GET",
        params: paramsAll.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ProductDetailResponse>>>
    >;

    return res.data;
};

export const getListProductDetails = async (paramsAll: Ref<FindAllProductDetailRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PRODUCT_DETAIL}/all-product-detail`,
        method: "GET",
        params: paramsAll.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ProductDetailResponse>>>
    >;

    return res.data;
};

// export const getListProducts = async () => {
//     const res = (await request({
//         url: `${PREFIX_API_ADMIN_PRODUCT}/get-list-product`,
//         method: "GET",
//     })) as AxiosResponse<
//         DefaultResponse<PaginationResponse<Array<ListProductResponse>>>
//     >;

//     return res.data;
// };

export const createProductDetail = async (data: ProductDetailRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PRODUCT_DETAIL}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getProductDetail = async (id: string | null) => {
    const res = (await request({
      url: `${PREFIX_API_ADMIN_PRODUCT_DETAIL}/${id}`,
      method: "GET",
    })) as AxiosResponse<DefaultResponse<ProductDetailResponse>>;
  
    return res.data;
  };

export const updateProductDetail = async (id: string, params: ProductDetailUpdateRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PRODUCT_DETAIL}/${id}`,
        method: "PUT",
        data: params,
      })) as AxiosResponse<DefaultResponse<null>>;
    
      return res.data;
};

// Lấy danh sách spct không phân trang
export const getListSanPhamChiTiet = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PRODUCT_DETAIL}/all-productdetail`,
        method: "GET"
    })) as AxiosResponse<
        DefaultResponse<Array<ProductDetailListResponse>>
    >;
    return res.data;
};

// Lấy danh sách spct theo id sanpham không phân trang
export const getListSanPhamChiTietByIdSanPham = async (id: string | null) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PRODUCT_DETAIL}/product-detail-by-id/${id}`,
        method: "GET"
    })) as AxiosResponse<
        DefaultResponse<Array<ProductDetailListResponse>>
    >;
    return res.data;
};