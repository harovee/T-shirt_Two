import { API_ADMIN_DELIVERY_PAYMENT } from "@/infrastructure/constants/url"
import request from "../../request"
import { AxiosResponse } from "axios"
import { DefaultResponse } from "@/infrastructure/types/api.common";


export interface CreateDeliveryPaymentRequest {
    idHoaDon: string | null,
    idPhuongThucThanhToan: string | null,
    tongTien: number | null,
    maGiaoDich: string | null,
    tienKhachDua: number | null,
    soTienDu: number | null,
    ghiChu: string | null,
}

export const createDeliveryPayment = async (data: CreateDeliveryPaymentRequest) => {
    const res = (await request({
        url: `${API_ADMIN_DELIVERY_PAYMENT}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
    return res.data
}