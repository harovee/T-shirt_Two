import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_COLOR} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

// export interface PropertyProductParams {
//     keyword?: string | null;
//     status?: string | null;

//     [key: string]: any;
// }

// export interface FindProductRequest extends PropertyProductParams, PaginationParams {

// }

export interface colorRequest {
    ten: string | null
}

// export type materialResponse = ResponseList & {
//     maSanPham: string | null
//     ten: string | null
// };

export type ListColorResponse = {
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

export const getListColor = async (): Promise<ListColorResponse[]> => {
    const res: AxiosResponse<DefaultResponse<ListColorResponse[]>> = await request({
      url: `${PREFIX_API_ADMIN_COLOR}/get-list-color`,
      method: "GET",
    });
  
    // Đảm bảo trả về dữ liệu từ "data"
    return res.data.data;
  };

export const createColor = async (data: colorRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_COLOR}`,
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
