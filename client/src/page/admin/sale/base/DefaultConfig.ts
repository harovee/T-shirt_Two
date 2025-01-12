
import { SaleRequest } from '@/infrastructure/services/api/admin/sale.api';
import dayjs, { Dayjs } from 'dayjs';

export interface FormState {
    ma: string;
    ten: string;
    loai: string;
    giaTri: number;
    giaTriGiamToiDa: number | null;
    ngayBatDauVaKetThuc: [Dayjs, Dayjs] | [];
    nguoiSua: string | undefined;
    trangThai: boolean;
    createdDate: bigint | null;
    lastModifiedDate: bigint | null;
}

export const defaultSaleRequest: SaleRequest = {
    ma: null,
    ten: null,
    loai: null,
    giaTri: null,
    giaTriGiamToiDa: null,
    ngayBatDau: null,
    ngayKetThuc: null,
    nguoiSua: null,
    trangThai: null
}
export const defaultFormState: FormState = {
    ma: '',
    ten: '',
    loai: 'PERCENT',
    giaTri: 0,
    giaTriGiamToiDa: null,
    ngayBatDauVaKetThuc: [],
    nguoiSua: undefined,
    trangThai: false,
    createdDate: null,
    lastModifiedDate: null,
}

export const defaultSaleDatePickerRules = [
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

