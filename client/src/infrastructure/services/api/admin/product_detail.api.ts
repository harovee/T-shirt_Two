import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_PRODUCT_DETAIL} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

// export interface PropertyProductParams {
//     keyword?: string | null;
//     status?: string | null;

//     [key: string]: any;
// }

// export interface FindProductRequest extends PropertyProductParams, PaginationParams {

// }



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
}

// export type ProductResponse = ResponseList & {
//     maSanPham: string | null
//     ten: string | null
//     ngayTao: Number | null
//     soLuong: number | 0
//     trangThai: Number | null
//     tenDanhMuc: string | null
// };

// export type ListProductResponse = {
//     maSanPham: string | null
//     ten: string | null
// };

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

// // export const getEmployee = async (EmployeeId: Ref<string | null>) => {
// //     return await request({
// //         url: `${PREFIX_API_ADMIN_PRODUCT}/${EmployeeId}`,
// //         method: "GET"
// //     }) as AxiosResponse<
// //         DefaultResponse<PaginationResponse<Array<ProductResponse>>>
// //     >;
// // };

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
