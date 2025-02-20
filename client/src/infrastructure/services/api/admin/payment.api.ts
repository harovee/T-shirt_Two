import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {API_ADMIN_PAYMENT} from "@/infrastructure/constants/url.ts";
import {API_ADMIN_PAYMENT, GHN_API_URL, GHN_TOKEN} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyVoucherParams {
    keyword: string | null;

    idKhachHang: string | null;

    tongTien: number | null;

    [key: string]: any;
}

export interface FindVoucherRequest extends PropertyVoucherParams, PaginationParams {

}

export type ShippingFeeRequest = {
    fromDistrictId: number;
    serviceId: number | 53320;
    toDistrictId: number;
    toWardCode: number;
    weight: number;
    length: number;
    width: number;
    height: number;

}

export interface FindCustomerAddressRequest extends PaginationParams {
    keyword: string | null;
    idKhachHang: string | null;
}

export interface nextVoucherRequest {
    idKhachHang: string | null;

    tongTien: number | null;
}



export type CustomerAddressResponse = ResponseList & {

    id: string;

    name: string;

    phoneNumber: string;

    line: string;

    ward: string;

    district: string;

    province: string;

    clientId: string;

    isDefault: boolean;

}

export type VoucherResponse = ResponseList & {
    ma: string;
    ten : string;
    soLuong: number;
    dieuKienGiam: string;
    giamToiDa: string;
    loaiGiam: boolean;
    kieu: boolean;
    giaTriGiam: string;
    ngayBatDau: number;
    ngayKetThuc: number;
    trangThai : string;
};

export interface ShippingFeeResponse {
      total: number; // Tổng phí vận chuyển (VNĐ)
  }
export const getListVoucher = async (params: Ref<FindVoucherRequest>) => {
    const res = (await request({
        url: `${API_ADMIN_PAYMENT}/voucher`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<VoucherResponse>>>
    >;

    return res.data;
};

export const getPriceNextVoucher = async (params: Ref<nextVoucherRequest>) => {
    const res = (await request({
        url: `${API_ADMIN_PAYMENT}/voucher/next-voucher`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<Object>
    >;

    return res.data;
};

export const getListCustomerAddress = async (params: Ref<FindCustomerAddressRequest>) => {
    const res = (await request({
        url: `${API_ADMIN_PAYMENT}/customer-address`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<VoucherResponse>>>
    >;

    return res.data;
};

export const getVoucherById = async (VoucherId: Ref<string | null>) => {
    return await request({
        url: `${API_ADMIN_PAYMENT}/voucher/${VoucherId.value}`,
        method: "GET",
    }) as AxiosResponse<
        DefaultResponse<VoucherResponse>
    >;
};

export type CustomerResponse = ResponseList & {
    profilePicture: string;
    name: string;
    phoneNumber: string;
    email: string;
    tinh: string;
    huyen: string;
    xa: string;
    soNha: string;
}

export interface FindCustomerRequest extends PaginationParams{
    keyword : string | null;
}

export const getListCustomer = async (params: Ref<FindCustomerRequest>) => {
    const res = (await request({
        url: `${API_ADMIN_PAYMENT}/khach-hang`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<CustomerResponse>>>
    >;

    return res.data;
};

export const getCustomerById = async (id: Ref<string | null>) => {
    return await request({
        url: `${API_ADMIN_PAYMENT}/khach-hang/${id.value}`,
        method: "GET",
    }) as AxiosResponse<
        DefaultResponse<CustomerResponse>
    >;

};


export const calculateShippingFee = async (params: Ref<ShippingFeeRequest>) => {
    const res = (await request({
        url: `${GHN_API_URL}/v2/shipping/fee`,
        method: "GET",
        params: {
            from_district_id: params.value.fromDistrictId,
            service_id: params.value.serviceId,
            to_district_id: params.value.toDistrictId,
            to_ward_code: params.value.toWardCode,
            weight: params.value.weight,
            length: params.value.length,
            width: params.value.width,
            height: params.value.height,
            insurance_value: 0,
            coupon: null
          },
        headers: {
            Token: GHN_TOKEN,
            ShopId: "S22560282" // Thêm Token xác thực GHN
        },
    })) as AxiosResponse<DefaultResponse<ShippingFeeResponse>>;

    return res.data;
};





