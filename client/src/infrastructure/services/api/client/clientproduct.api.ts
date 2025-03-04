import { ClientProductDetailRequest } from './clientproduct.api';
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {API_CLIENT_ALLPRODUCT, PREFIX_API_ADMIN_CLIENT} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";
import { List } from "lodash";

export interface PropertyProductParams {
    tenSanPham?: string | null;
    tenDanhMuc?: string | null;
    tenChatLieu?: string | null;
    tenTayAo?: string | null;
    tenTinhNang?: string | null;
    tenKieuDang?: string | null;
    tenCoAo?: string | null;
    tenThuongHieu?: string | null;
    tenHoaTiet?: string | null;

}

export interface FindProductClientRequest extends PropertyProductParams, PaginationParams {

}

export interface ClientProductRequest {

      idHoaTiet: string;

      idTinhNang: string ;

      idCoAo: string;

      idTayAo: string;

      idThuongHieu: string;

     idKieuDang: string;

     idChatLieu: string;

     idDanhMuc: string;
}

export interface ClientProductDetailRequest extends ClientProductRequest{
    
    idKichCo: string;

    idMauSac: string;
}

export type  ImageResponse={
    id: string;
    url: string;
}

export type SizeResponse = {
    id: string;
    ten: string;
}

export type ColorResponse = {
    id: string;
    ten: string;
}

export type KieuDangResponse = {
    id: string;
    ten: string;
}
export type ChatLieuResponse = {
    id: string;
    ten: string;
}
export type DanhMucResponse = {
    id: string;
    ten: string;
}
export type CoAoResponse = {
    id: string;
    ten: string;
}
export type TayAoResponse = {
    id: string;
    ten: string;
}
export type ThuongHieuResponse = {
    id: string;
    ten: string;
}
export type HoaTietResponse = {
    id: string;
    ten: string;
}

export type TinhNangResponse = {
    id: string;
    ten: string;
}
export type ClientProductResponse = ResponseList & {

    anh: List<ImageResponse> ;

    maSanPham: string ;

    ten: string ;

    danhMuc: DanhMucResponse;

    chatLieu: ChatLieuResponse;

    tayAo: TayAoResponse;

    coAo: CoAoResponse;

    hoaTiet: HoaTietResponse;

    tinhNang: TinhNangResponse;

    kieuDang: KieuDangResponse;

    thuongHieu: ThuongHieuResponse;

    gia: List<number>;

    discount: List<number>;
    
    kichCo: List<SizeResponse>;

    color: List<ColorResponse>;
};


export type ClientProductDetailResponse = ResponseList & {

    anh: string ;

    maSPCT: string ;

    ten: string ;

    danhMuc: string;

    chatLieu: string;

    tayAo: string;

    coAo: string;

    hoaTiet: string;

    tinhNang: string;

    kieuDang: string;

    thuongHieu: string;

    gia: number;

    kichCo: string;

    color: string;
};


export const getAllProducts = async (params: Ref<FindProductClientRequest>) => {
    const res = (await request({
        url: `${API_CLIENT_ALLPRODUCT}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ClientProductResponse>>>
    >;

    return res.data;
};

export const getTop8ProductsMoiNhat = async (params: Ref<FindProductClientRequest>) => {
    const res = (await request({
        url: `${API_CLIENT_ALLPRODUCT}/moi-nhat`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ClientProductResponse>>>
    >;

    return res.data;
};

export const getProductDetailById = async (sanPhamId: Ref<string | null>, params: Ref<ClientProductDetailRequest>) => {
    return await request({
        url: `${API_CLIENT_ALLPRODUCT}/detail/${sanPhamId.value}`,
        params: params.value,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<Array<ClientProductResponse>>
    >;
};

export const getProductById = async (sanPhamId: Ref<string | null>, params: Ref<ClientProductRequest>) => {
    return await request({
        url: `${API_CLIENT_ALLPRODUCT}/${sanPhamId.value}`,
        params: params.value,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<Array<ClientProductResponse>>
    >;
};

