import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {API_ADMIN_PAYMENT, API_ADMIN_POINT_OF_SALE} from "@/infrastructure/constants/url.ts";
import {API_ADMIN_PAYMENT, GHN_API_URL, GHN_TOKEN, GHN_API_SERVICES} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";
import { invoices, currentInvoice, sendCartInfo } from "@/infrastructure/mobile.connect/InvoiceConnect2";

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
    fromWardCode: string;
    serviceId: number | 53320;
    toDistrictId: string;
    toWardCode: string;
    weight: number;
    length: number;
    width: number;
    height: number;
}

export type ServiceIdRequest ={
    formDistrict : number,
    toDistrict: number,
    shopId : number
}

export interface FindCustomerAddressRequest extends PaginationParams {
    keyword: string | null;
    idKhachHang: string | null;
}

export interface nextVoucherRequest {
    idKhachHang: string | null;

    tongTien: number | null;
}

export interface voucherRequest {
    keyword: string | null

    idKhachHang: string | null;
}

export interface paymentMethodDetailRequest {
    idHoaDon: string | null;

    idPhuongThucThanhToan: string | null;

    tienKhachDua: number | null;

    soTienDu: number | null;

    maGiaoDich: string | null;

    tienChuyenKhoan: number | null
}

export interface invoicePdfRequest {
    idKhachHang: string | null;

    idNhanVien: string | null;

    idHoaDon: string | null;

    products: Array<any> | [];

    tongTien: number;

    phiVanChuyen: number;

    giamGia: number;
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
    giaTri: string
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

export type PaymentMethodDetailResponse = ResponseList & {
    tenPhuongThuc: string;
    maGiaoDich : string;
    soTien: number;
};

export interface ServiceIdResponse{
    service_id: number,
    short_name: string,
    service_type_id: number,
}

export interface ShippingFeeResponse {
    total: number;         // Tổng phí vận chuyển
    service_fee: number;   // Phí ship
    insurance_fee: number; // Tổng phí vận chuyển (VNĐ)
}

export const getListPaymentMethodDetail = async (params: Ref<paymentMethodDetailRequest>) => {
    const res = (await request({
        url: `${API_ADMIN_PAYMENT}/payment-method-detail`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<Array<PaymentMethodDetailResponse>>
    >;
    return res.data;
};

export const createPaymentMethodDetail = async (data: paymentMethodDetailRequest) => {
    const res = (await request({
        url: `${API_ADMIN_PAYMENT}/payment-method-detail`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const createInvoicePdf = async (data: invoicePdfRequest) => {
    const res = (await request({
        url: `${API_ADMIN_POINT_OF_SALE}/save`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
    return res.data;
};

export const createInvoicePdfWithId = async (id: string, data: invoicePdfRequest) => {
    const res = (await request({
        url: `${API_ADMIN_POINT_OF_SALE}/pdf/${id}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
    return res.data;
};

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

export const getVoucherByCode = async (params: Ref<voucherRequest>) => {
    const res = (await request({
        url: `${API_ADMIN_PAYMENT}/voucher-code`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<VoucherResponse>
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

export const getWardByCode = async (code: string) => {
    return await request({
        url: `${API_ADMIN_PAYMENT}/ward/${code}`,
        method: "GET",
    }) as AxiosResponse<
        DefaultResponse<String>
    >;
};

export const getDistrictById = async (id: string) => {
    return await request({
        url: `${API_ADMIN_PAYMENT}/district/${id}`,
        method: "GET",
    }) as AxiosResponse<
        DefaultResponse<String>
    >;
};


export const getProvinceById = async (id: string) => {
    return await request({
        url: `${API_ADMIN_PAYMENT}/province/${id}`,
        method: "GET",
    }) as AxiosResponse<
        DefaultResponse<String>
    >;
};

export const getCustomerByPhoneNumber = async (phoneNumber: Ref<string | null>) => {
    return await request({
        url: `${API_ADMIN_PAYMENT}/customer/${phoneNumber.value}`,
        method: "GET",
    }) as AxiosResponse<
        DefaultResponse<CustomerResponse>
    >;
};

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
    try {
        const res = (await request({
            url: `${GHN_API_URL}`,
            method: "GET",
            params: {
                from_district_id: params.value.fromDistrictId,
                from_ward_code: params.value.fromWardCode,
                service_id: params.value.serviceId,
                service_type_id: 1,
                to_district_id: params.value.toDistrictId,
                to_ward_code: params.value.toWardCode,
                weight: params.value.weight,
                length: params.value.length,
                width: params.value.width,
                height: params.value.height
            },
            headers: {
                token: GHN_TOKEN,
                ShopId: "S22560282"
            },
        })) as AxiosResponse<DefaultResponse<ShippingFeeResponse>>;

        // console.log("Response API GHN:", res.data); // Kiểm tra response
        
        invoices.value.forEach((item) => {
            if (item.id === currentInvoice.value.id) {
            item.shipping = {
                method: 'GHN',
                cost: res.data.data.total,
                estimatedDelivery: '' ,
            }
            currentInvoice.value = item
            sendCartInfo(item);
            }
        });

        return res.data;
    } catch (error) {
        console.error("Lỗi khi gọi API GHN:", error);
    }
};

export const getServiceId = async (params: Ref<ServiceIdRequest>) => {
    try {
        const res = (await request({
            url: `${GHN_API_SERVICES}`,
            method: "GET",
            params: {
                from_district: params.value.formDistrict,
	            to_district: params.value.toDistrict,
	            shop_id: params.value.shopId
            },
            headers: {
                token: GHN_TOKEN
            },
        })) as AxiosResponse<DefaultResponse<ServiceIdResponse>>;
        return res.data;
    } catch (error) {
        console.error("Lỗi khi gọi API GHN Services:", error);
    }
};







