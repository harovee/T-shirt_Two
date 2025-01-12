import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_MATERIAL} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

// export interface PropertyProductParams {
//     keyword?: string | null;
//     status?: string | null;

//     [key: string]: any;
// }

// export interface FindProductRequest extends PropertyProductParams, PaginationParams {

// }

export interface materialRequest {
    ten: string | null
}

// export type materialResponse = ResponseList & {
//     maSanPham: string | null
//     ten: string | null
// };

export type ListMaterialResponse = {
    id: string | null,
    ten: string | null
};

// export const getProducts = async (params: Ref<FindProductRequest>) => {
//     const res = (await request({
//         url: `${PREFIX_API_ADMIN_PRODUCT}`,
//         method: "GET",
//         params: params.value,
//     })) as AxiosResponse<
//         DefaultResponse<PaginationResponse<Array<ProductResponse>>>
//     >;

//     return res.data;
// };

// export const getListMaterial = async () => {
//     const res = (await request({
//         url: `${PREFIX_API_ADMIN_MATERIAL}/get-list-material`,
//         method: "GET",
//     })) as AxiosResponse<
//         DefaultResponse<PaginationResponse<Array<ListMaterialResponse>>>
//     >;

//     return res.data;
// };

export const getListMaterial = async (): Promise<ListMaterialResponse[]> => {
    const res: AxiosResponse<DefaultResponse<ListMaterialResponse[]>> = await request({
      url: `${PREFIX_API_ADMIN_MATERIAL}/get-list-material`,
      method: "GET",
    });
  
    // Đảm bảo trả về dữ liệu từ "data"
    return res.data.data;
  };

export const createMaterial = async (data: materialRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_MATERIAL}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

// export const getEmployee = async (EmployeeId: Ref<string | null>) => {
//     return await request({
//         url: `${PREFIX_API_ADMIN_PRODUCT}/${EmployeeId}`,
//         method: "GET"
//     }) as AxiosResponse<
//         DefaultResponse<PaginationResponse<Array<ProductResponse>>>
//     >;
// };

// export const updateProduct = async (id: string, data: ProductRequest) => {
//     return await request({
//         url: `${PREFIX_API_ADMIN_PRODUCT}/${id}`,
//         method: "PUT",
//         data: data
//     }) as AxiosResponse<
//         DefaultResponse<DefaultResponse<null>>
//     >;
// };

// export const deletedProduct = async (id: string) => {
//     return await request({
//         url: `${PREFIX_API_ADMIN_PRODUCT}/${id}`,
//         method: "DELETE",
//     }) as AxiosResponse<
//         DefaultResponse<DefaultResponse<null>>
//     >;
// };
