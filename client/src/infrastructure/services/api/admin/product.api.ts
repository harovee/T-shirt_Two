import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_PRODUCT} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyProductParams {
    keyword?: string | null;
    status?: string | null;

    [key: string]: any;
}

export interface FindProductRequest extends PropertyProductParams, PaginationParams {

}

export interface ProductRequest {
    ten: string | null
    moTa: string | null
    trangThai: Number | null
    idDanhMuc: string | null
}

export type ProductResponse = ResponseList & {
    maSanPham: string | null
    ten: string | null
    ngayTao: Number | null
    soLuong: number | 0
    trangThai: Number | null
    tenDanhMuc: string | null
};

export type ListProductResponse = {
    maSanPham: string | null
    ten: string | null
};

export const getProducts = async (params: Ref<FindProductRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PRODUCT}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ProductResponse>>>
    >;

    return res.data;
};

export const getListProducts = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PRODUCT}/get-list-product`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ListProductResponse>>>
    >;

    return res.data;
};

export const createProduct = async (data: ProductRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PRODUCT}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getProduct = async (id: string | null) => {
    const res = (await request({
      url: `${PREFIX_API_ADMIN_PRODUCT}/${id}`,
      method: "GET",
    })) as AxiosResponse<DefaultResponse<ProductResponse>>;
  
    return res.data;
  };

export const updateProduct = async (id: string, params: ProductRequest) => {
    // return await request({
    //     url: `${PREFIX_API_ADMIN_PRODUCT}/${id}`,
    //     method: "PUT",
    //     data: data
    // }) as AxiosResponse<
    //     DefaultResponse<DefaultResponse<null>>
    // >;
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PRODUCT}/${id}`,
        method: "PUT",
        data: params,
      })) as AxiosResponse<DefaultResponse<null>>;
    
      return res.data;
};

// export const deletedProduct = async (id: string) => {
//     return await request({
//         url: `${PREFIX_API_ADMIN_PRODUCT}/${id}`,
//         method: "DELETE",
//     }) as AxiosResponse<
//         DefaultResponse<DefaultResponse<null>>
//     >;
// };
