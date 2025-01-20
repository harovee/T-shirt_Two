
import { VoucherRequest } from '@/infrastructure/services/api/admin/voucher/voucher.api';
import dayjs, { Dayjs } from 'dayjs';

export interface FormState {
    ten: string;
    loaiGiam: boolean;
    giaTriGiam: string;
    giamToiDa: string ;
    soLuong: number | 0;
    dieuKienGiam: string ;
    ngayBatDauVaKetThuc: [Dayjs, Dayjs] | [];
}

export const defaultVoucherRequest: VoucherRequest = {
    ten: "",
    loaiGiam: false,
    giaTriGiam: "",
    giamToiDa: "",
    dieuKienGiam: "",
    soLuong: 0,
    ngayBatDau: null,
    ngayKetThuc: null,
}
export const defaultFormState: FormState = {
    ten: '',
    loaiGiam: false,
    giaTriGiam: "",
    giamToiDa: "",
    dieuKienGiam: "",
    soLuong: 0,
    ngayBatDauVaKetThuc: [],
}

export const defaultVoucherDatePickerRules = [
    { label: 'Ngày mai', value: [dayjs().startOf('d').add(1, 'd'), dayjs().endOf('d').add(1, 'd')] },
    { label: '7 ngày tiếp theo', value: [dayjs(), dayjs().add(7, 'd')] },
    { label: '15 ngày tiếp theo', value: [dayjs(), dayjs().add(15, 'd')] },
    { label: '30 ngày tiếp theo', value: [dayjs(), dayjs().add(30, 'd')] },
    {
        label: 'Tuần sau',
        value: [
            dayjs().startOf('week').add(1, 'week').add(1, 'd'),
            dayjs().endOf('week').add(1, 'week').add(1, 'd')
        ]
    },
    {
        label: 'Tháng sau',
        value: [
            dayjs().startOf('month').add(1, 'month'),
            dayjs().endOf('month').add(1, 'month'),
        ]
    },

]

export const defaultSortOptions = [
    {value: "gia_tri", label: "gia_tri"},
    {value: "ngay_bat_dau", label: "Ngày bắt đầu"},
    {value: "ngay_ket_thuc", label: "Ngày kết thúc"},

]

